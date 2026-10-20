package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class OrderTest {
    Account account = new Account();
    ArticleRegistry ar = new ArticleRegistry();
    
    

    @Test
    void cannot_scan_empty_basket() {
        Basket basket = new Basket();
        Scanner scanner = new Scanner(basket, ar);
        Order order = new Order(account, scanner, basket);

        assertThrows(IllegalArgumentException.class, () -> {
            order.scanItems(basket);
        });
    }

    @Test
    void scanItems_return_correct_totalCost_for_one_item() {
        Item item = new UnitPricedArticle("Coffee", 2500, Vat.FOOD, 1);
        Basket basket = new Basket();
        Scanner scanner = new Scanner(basket, ar);
        Order order = new Order(account, scanner, basket);

        basket.addItem(item);
        order.scanItems(basket);
        int expectedTotal = item.getPrice();

        assertEquals(expectedTotal, order.getTotalCost());
    }
    
    //logik basket -> order
    //logiken order -> reciept


}
