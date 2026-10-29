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

    @Test
    void skips_comment_lines_in_csv() {
        String csv = """
            articleNumber;name;pricingType;price;vatType
            # This is a comment
              # This is also a comment
            101;Soy-milk;UNIT;1200;FOOD
            """;

        InputStream input = new java.io.ByteArrayInputStream(
                csv.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        ArticleCatalogLoader loader = new ArticleCatalogLoader(input);

        assertEquals("Soy-milk",
                loader.findByArticleNumber(101).getName());
    }

    @Test
    void throws_exception_when_csv_cannot_be_read() {
        InputStream input = new InputStream() {
            @Override
            public int read() throws java.io.IOException {
                throw new java.io.IOException("Simulated read failure");
            }
        };

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ArticleCatalogLoader(input)
        );

        assertEquals("Could not read article catalog",
                exception.getMessage());

        assertInstanceOf(java.io.IOException.class,
                exception.getCause());
    }


}