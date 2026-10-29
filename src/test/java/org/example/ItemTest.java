
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemTest {

    private static class TestItem extends Item {

        TestItem(String name, long price, Vat vat, int articleId) {
            super(name, price, vat, articleId);
        }

        TestItem(String name, long price, Vat vat, int articleId,
                 String discountType) {
            super(name, price, vat, articleId, discountType);
        }

        @Override
        public long getPrice() {
            return price;
        }

        @Override
        public Vat getVatRate() {
            return vat;
        }

        @Override
        public long getInclusivePrice() {
            return price;
        }
    }

    @Test
    void constructor_without_discount_should_set_properties() {
        Item item = new TestItem("The Bible", 10000, Vat.BOOK, 1001);

        assertEquals("The Bible", item.getName());
        assertEquals(10000, item.getPrice());
        assertEquals(Vat.BOOK, item.getVatRate());
        assertEquals(1001, item.getArticleNumber());
        assertNull(item.getDiscountType());
    }

    @Test
    void constructor_with_discount_should_set_properties() {
        Item item = new TestItem(
                "The Bible", 10000, Vat.BOOK, 1001, "PERCENTAGE"
        );

        assertEquals("The Bible", item.getName());
        assertEquals(10000, item.getPrice());
        assertEquals(Vat.BOOK, item.getVatRate());
        assertEquals(1001, item.getArticleNumber());
        assertEquals("PERCENTAGE", item.getDiscountType());
    }
}
