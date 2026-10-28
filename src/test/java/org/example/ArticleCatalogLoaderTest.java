package org.example;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ArticleCatalogLoaderTest {

    @Test
    void loadsWeighedItemFromCsv() {
        InputStream input = getClass()
                .getResourceAsStream("/articles.csv");

        assertNotNull(input, "Test CSV file should exist");

        ArticleCatalogLoader loader =
                new ArticleCatalogLoader(input);

        Item item = loader.findByArticleNumber(42);

        assertInstanceOf(WeighedItem.class, item);

        WeighedItem apple = (WeighedItem) item;
        assertEquals(42, apple.getArticleNumber());
        assertEquals("Pink Lady Apple", apple.getName());
        assertEquals(100, apple.getPricePerKg());
        assertEquals(Vat.FOOD, apple.getVatRate());
    }

    @Test
    void loadsUnitPricedArticleFromCsv() {
        InputStream input = getClass()
                .getResourceAsStream("/articles.csv");

        assertNotNull(input, "Test CSV file should exist");

        ArticleCatalogLoader loader =
                new ArticleCatalogLoader(input);

        Item item = loader.findByArticleNumber(101);

        assertInstanceOf(UnitPricedArticle.class, item);

        UnitPricedArticle soyMilk = (UnitPricedArticle) item;
        assertEquals(101, soyMilk.getArticleNumber());
        assertEquals("Soy-milk", soyMilk.getName());
        assertEquals(1200, soyMilk.getPrice());
        assertEquals(Vat.FOOD, soyMilk.getVatRate());
    }
}