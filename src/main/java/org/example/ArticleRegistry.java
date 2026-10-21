package org.example;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ArticleRegistry {

    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<String, Integer> numbersByName = new HashMap<>();

    public void addItem(int articleNumber, Item item) {
        items.put(articleNumber, item);
        numbersByName.put(item.getName().toLowerCase(Locale.ROOT),
                articleNumber);
    }

    public Item findByArticleNumber(int articleNumber) {
        return items.get(articleNumber);
    }

    /**
     * Finds an article by its name handle, case-insensitively.
     *
     * @param name the article handle, e.g. {@code soy-milk}
     * @return the article, or null when unknown
     */
    public Item findByName(String name) {
        Integer number = numbersByName.get(name.toLowerCase(Locale.ROOT));
        return number == null ? null : items.get(number);
    }

    /**
     * Creates the registry pre-seeded with the standard twelve-article
     * assortment of spec &sect;5 (article numbers 1-12, weighed templates
     * priced per kilogram).
     *
     * @return a seeded registry
     */
    public static ArticleRegistry withStandardAssortment() {
        ArticleRegistry registry = new ArticleRegistry();
        registry.addItem(1, new UnitPricedArticle("soy-milk", 3121, Vat.FOOD,
                1, "Alpro Soymilk"));
        registry.addItem(2, new UnitPricedArticle("oat-drink", 2942, Vat.FOOD,
                2, "Alpro Oat Drink"));
        registry.addItem(3, new UnitPricedArticle("orange-juice", 2674,
                Vat.FOOD, 3, "Tropicana Orange Juice"));
        registry.addItem(4, new UnitPricedArticle("hummus", 2228, Vat.FOOD,
                4, "Hummus Original 200g"));
        registry.addItem(5, new UnitPricedArticle("tofu", 2496, Vat.FOOD,
                5, "Organic Firm Tofu"));
        registry.addItem(6, new UnitPricedArticle("black-beans", 1781,
                Vat.FOOD, 6, "Black Beans 400g"));
        registry.addItem(7, new UnitPricedArticle("dark-chocolate", 2049,
                Vat.FOOD, 7, "Maraboo Dark Chocolate 100g"));
        registry.addItem(8, new UnitPricedArticle("candle", 3996,
                Vat.STANDARD, 8, "Scented Candle Vanilla"));
        registry.addItem(9, new WeighedItem("apple", 2223, Vat.FOOD, 1000,
                9, "Apple"));
        registry.addItem(10, new WeighedItem("banana", 1420, Vat.FOOD, 1000,
                10, "Banana"));
        registry.addItem(11, new WeighedItem("vegan-cheese", 17768, Vat.FOOD,
                1000, 11, "Vegan Cheese Slices"));
        registry.addItem(12, new WeighedItem("olives", 13348, Vat.FOOD, 1000,
                12, "Marinated Olives"));
        return registry;
    }
}
