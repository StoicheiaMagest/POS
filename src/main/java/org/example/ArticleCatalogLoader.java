package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ArticleCatalogLoader {

    private final ArticleRegistry registry;

    public ArticleCatalogLoader(InputStream input) {
        registry = new ArticleRegistry();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

            reader.readLine(); // Skip header

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.stripLeading().startsWith("#")) {
                    continue;
                }

                String[] fields = line.split(";");

                int articleNumber = Integer.parseInt(fields[0]);
                String name = fields[1];
                String pricingType = fields[2];
                int price = Integer.parseInt(fields[3]);
                Vat vat = Vat.valueOf(fields[4]);

                if (pricingType.equals("WEIGHED")) {
                    Item item = new WeighedItem(
                            name,
                            price,
                            vat,
                            1000,
                            articleNumber
                    );

                    registry.registerItem(articleNumber, item);

                } else if (pricingType.equals("UNIT")) {
                    Item item = new UnitPricedArticle(
                            name,
                            price,
                            vat,
                            articleNumber
                    );

                    registry.registerItem(articleNumber, item);
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Could not read article catalog", e);
        }
    }

    public Item findByArticleNumber(int articleNumber) {
        return registry.findByArticleNumber(articleNumber);
    }
}