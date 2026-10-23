package org.example;

import java.io.InputStreamReader;

// Interactive CRUD command line for trying out the basket, discounts, VAT and receipt.
// This is a manual tool, not a JUnit test: run its main method.
// The command logic lives in BasketCli, which is covered by BasketCliTest.
public class CRUDTest {

    public static void main(String[] args) {
        new BasketCli(createCatalog(), new InputStreamReader(System.in), System.out).run();
    }

    // Pre-existing items the user can pick from.
    // 111 (milk) has 3 for 2 and 222 (bread) has 2 kr off, see Discount.
    static ArticleRegistry createCatalog() {
        ArticleRegistry catalog = new ArticleRegistry();
        catalog.addItem(111, new UnitPricedArticle("Milk", 1000, Vat.FOOD, 111));
        catalog.addItem(222, new UnitPricedArticle("Bread", 2000, Vat.FOOD, 222));
        catalog.addItem(333, new WeighedItem("Apples", 2990, Vat.FOOD, 0, 333));
        catalog.addItem(444, new UnitPricedArticle("Book", 5000, Vat.BOOK, 444));
        catalog.addItem(555, new UnitPricedArticle("Pen", 1000, Vat.STANDARD, 555));
        return catalog;
    }
}
