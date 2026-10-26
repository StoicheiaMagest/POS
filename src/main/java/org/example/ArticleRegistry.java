package org.example;

import java.util.HashMap;
import java.util.Map;

public class ArticleRegistry {

    private final Map<Integer, Item> items = new HashMap<>();

    public void addItem(int articleNumber, Item item) {
        items.put(articleNumber, item);
    }

    public Item findByArticleNumber(int articleNumber) {
        return items.get(articleNumber);
    }
}
