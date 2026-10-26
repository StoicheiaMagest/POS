package org.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.example.RegisterTestSupport.run;
import static org.junit.jupiter.api.Assertions.*;

// The tests that depend on the real discount rules. The register is given its own registry whose
// items carry a discount type, and the expected output comes from a real Receipt for the same order,
// so no names, prices or discount texts are hard-coded. The article numbers do not matter.
// Everything else about the register is tested with literal text in RegisterSystemColourArticlesTest.
public class RegisterSystemTest2 {

    private static final int ARTICLE_A = 10;   // 3 for 2
    private static final int ARTICLE_B = 20;   // 2 kr off

    private final ArticleRegistry registry = discountedArticles();

    private ArticleRegistry discountedArticles() {
        ArticleRegistry registry = new ArticleRegistry();
        registry.addItem(ARTICLE_A,
                new UnitPricedArticle("Red", 400, Vat.FOOD, ARTICLE_A, DiscountType.THREE_FOR_TWO));
        registry.addItem(ARTICLE_B,
                new UnitPricedArticle("Blue", 150, Vat.FOOD, ARTICLE_B, DiscountType.TWO_KR_OFF));
        return registry;
    }

    private Item article(int articleNumber) {
        return registry.findByArticleNumber(articleNumber);
    }

    private PurchaseOccasion occasionFor(Customer customer, Item... items) {
        Basket basket = new Basket();
        for (Item item : items) {
            basket.addItem(item);
        }
        return new PurchaseOccasion(basket, customer, new Account(0), new Discount());
    }

    // What LIST should print: the receipt for the same order without its two header lines.
    private String previewFor(Customer customer, Item... items) {
        String receipt = new Receipt(1, LocalDateTime.now(), "Preview", occasionFor(customer, items)).print();
        return receipt.substring(receipt.indexOf('\n', receipt.indexOf('\n') + 1) + 1);
    }

    private String[] withCommand(String[] commands, String extra) {
        String[] all = Arrays.copyOf(commands, commands.length + 1);
        all[commands.length] = extra;
        return all;
    }

    // What PAY printed on the receipt after its header lines, up to the end of the Totalt line.
    private String receiptBody(String output) {
        int start = output.indexOf('\n', output.indexOf("kvitto")) + 1;
        int end = output.indexOf("Totalt", start);
        return output.substring(start, output.indexOf('\n', end));
    }

    // What the last LIST printed: the block ending in the last Totalt line (commands are separated by a blank line).
    private String listBlock(String output) {
        int end = output.indexOf('\n', output.lastIndexOf("Totalt"));
        int start = output.lastIndexOf("\n\n", end - 1) + 2;
        return output.substring(start, end);
    }

    @Test
    void listAppliesTheSameDiscountsAsPay() {
        Item a = article(ARTICLE_A);
        Item b = article(ARTICLE_B);
        // make sure the scenario really triggers discounts, otherwise the test proves nothing
        assertFalse(occasionFor(null, a, a, a, b).getDiscounts().isEmpty());

        String output = run(registry, "NEW", "SCAN " + ARTICLE_A, "SCAN " + ARTICLE_A, "SCAN " + ARTICLE_A,
                "SCAN " + ARTICLE_B, "LIST");

        assertTrue(output.contains(previewFor(null, a, a, a, b)), output);
    }

    @Test
    void listGivesAMemberTwoKrOffButNotAnAnonymousCustomer() {
        Item b = article(ARTICLE_B);
        Customer member = new Customer("Member", "19900101-1234", true, false);
        // 2 kr off is a member discount: the two customers must get different results
        assertNotEquals(previewFor(null, b, b), previewFor(member, b, b));

        String anonymous = run(registry, "NEW", "SCAN " + ARTICLE_B, "SCAN " + ARTICLE_B, "LIST");
        String asMember = run(registry, "NEW MEMBER", "SCAN " + ARTICLE_B, "SCAN " + ARTICLE_B, "LIST");

        assertTrue(anonymous.contains(previewFor(null, b, b)), anonymous);
        assertTrue(asMember.contains(previewFor(member, b, b)), asMember);
    }

    @Test
    void listMatchesWhatPayPrintsOnTheReceipt() {
        Item a = article(ARTICLE_A);
        // a non-member only gets item discounts: make sure the scenario triggers them, otherwise
        // LIST and PAY could agree on a receipt with nothing but the senior discount
        assertFalse(occasionFor(null, a, a, a, article(ARTICLE_B)).getDiscounts().isEmpty());

        String[] order = {"NEW SENIOR MEMBER", "SCAN " + ARTICLE_A, "SCAN " + ARTICLE_A, "SCAN " + ARTICLE_A,
                "SCAN " + ARTICLE_B};
        String listed = run(registry, withCommand(order, "LIST"));
        String paid = run(registry, withCommand(order, "PAY"));

        assertEquals(receiptBody(paid), listBlock(listed));
    }
}
