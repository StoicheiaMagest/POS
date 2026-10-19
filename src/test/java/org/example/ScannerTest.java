package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerTest {
    

    @Test
    void scanner_should_return_correct_price() {
    Scanner scanner = new Scanner();
    UnitPricedArticle article = new UnitPricedArticle(
                "The Bible", 
                10000,  // 10000 cents base price
                Vat.BOOK, // 6% VAT
                1001
        );
    WeighedItem weighedItem = new WeighedItem(
                "Pink Lady Apple",
                100, // 100 cents per kg
                Vat.FOOD,
                500, // 500 grams
                42   // article id
        );
    scanner.add(article);
    scanner.add(weighedItem);
    assertEquals(0.0, scanner.getTotal());
    }
    
}
