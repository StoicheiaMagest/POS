
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasketWeighedItemIntegrationTest {

    @Test
    void addingWeighedItemAddsPriceIncludingVatToBasket() {
        Basket basket = new Basket();

        WeighedItem apple = new WeighedItem(
                "Pink Lady Apple",
                100,
                Vat.FOOD,
                500,
                42
        );

        basket.addItem(apple);

        // 100 öre/kg × 500 g / 1000 = 50 öre exklusive moms.
        // 12 % moms på 50 öre avrundas till 6 öre.
        assertEquals(50, apple.getPrice());
        assertEquals(56L, apple.getInclusivePrice());
        assertEquals(56L, basket.getTotalPrice());
    }

    @Test
    void addingTwoWeighedItemsAddsBothInclusivePricesToBasket() {
        Basket basket = new Basket();

        WeighedItem apple = new WeighedItem(
                "Pink Lady Apple",
                100,
                Vat.FOOD,
                500,
                42
        );

        WeighedItem banana = new WeighedItem(
                "Banana",
                200,
                Vat.FOOD,
                500,
                43
        );

        basket.addItem(apple);
        basket.addItem(banana);

        assertEquals(56L, apple.getInclusivePrice());
        assertEquals(112L, banana.getInclusivePrice());
        assertEquals(168L, basket.getTotalPrice());
        assertEquals(2, basket.getItems().size());
    }

    @Test
    void removingWeighedItemSubtractsItsInclusivePriceFromBasket() {
        Basket basket = new Basket();

        WeighedItem apple = new WeighedItem(
                "Pink Lady Apple",
                100,
                Vat.FOOD,
                500,
                42
        );

        basket.addItem(apple);

        assertEquals(56L, basket.getTotalPrice());

        basket.removeItem(apple);

        assertEquals(0L, basket.getTotalPrice());
        assertTrue(basket.getItems().isEmpty());
    }

    @Test
    void basketCalculatesCorrectTotalForDifferentVatRates() {
        Basket basket = new Basket();

        WeighedItem food = new WeighedItem(
                "Food",
                2000,
                Vat.FOOD,
                500,
                101
        );

        WeighedItem standardItem = new WeighedItem(
                "Standard-rated item",
                2000,
                Vat.STANDARD,
                500,
                102
        );

        WeighedItem book = new WeighedItem(
                "Book",
                2000,
                Vat.BOOK,
                500,
                103
        );

        basket.addItem(food);
        basket.addItem(standardItem);
        basket.addItem(book);

        assertEquals(1120L, food.getInclusivePrice());
        assertEquals(1250L, standardItem.getInclusivePrice());
        assertEquals(1060L, book.getInclusivePrice());
        assertEquals(3430L, basket.getTotalPrice());
    }

    @Test
    void vatRoundsSmallAmountsCorrectly() {
        Basket basket = new Basket();

        // 10 öre/kg × 500 g = 5 öre exklusive moms.
        // 12 % av 5 öre är 0,6 öre, vilket avrundas till 1 öre.
        WeighedItem item = new WeighedItem(
                "Low-priced food",
                10,
                Vat.FOOD,
                500,
                104
        );

        basket.addItem(item);

        assertEquals(5, item.getPrice());
        assertEquals(1L, item.getVatAmount());
        assertEquals(6L, item.getInclusivePrice());
        assertEquals(6L, basket.getTotalPrice());
    }

    @Test
    void basketRemainsCorrectWhenSameItemIsAddedTwiceAndRemovedOnce() {
        Basket basket = new Basket();

        WeighedItem apple = new WeighedItem(
                "Pink Lady Apple",
                100,
                Vat.FOOD,
                500,
                105
        );

        basket.addItem(apple);
        basket.addItem(apple);

        assertEquals(112L, basket.getTotalPrice());
        assertEquals(2, basket.getItems().size());

        basket.removeItem(apple);

        assertEquals(56L, basket.getTotalPrice());
        assertEquals(1, basket.getItems().size());
        assertTrue(basket.getItems().contains(apple));
    }

    @Test
    void zeroWeightProducesZeroPrice() {
        Basket basket = new Basket();

        WeighedItem item = new WeighedItem(
                "Food with zero weight",
                1000,
                Vat.FOOD,
                0,
                108
        );

        basket.addItem(item);

        assertEquals(0, item.getPrice());
        assertEquals(0L, item.getInclusivePrice());
        assertEquals(0L, basket.getTotalPrice());
    }
}
