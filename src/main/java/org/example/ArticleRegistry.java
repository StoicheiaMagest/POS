package org.example;

import java.util.HashMap;
import java.util.Map;

public class ArticleRegistry {

    private final Map<String, Item> items = new HashMap<>();

    public void addItem(String articleNumber, Item item) {
        items.put(articleNumber, item);
    }

    public Item findByArticleNumber(String articleNumber) {
        return items.get(articleNumber);
    }
}
