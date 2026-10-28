package org.example;

import java.util.HashMap;
import java.util.Map;

public class ArticleRegistry {

    private final Map<Integer, Item> items = new HashMap<>();

    public void registerItem(int articleNumber, Item item) {
        if (articleNumber < 0) {
            throw new IllegalArgumentException(
                    "Article number cannot be negative: " + articleNumber
            );
        }

        if (item == null) {
            throw new IllegalArgumentException(
                    "Item cannot be null"
            );
        }

        if (items.containsKey(articleNumber)) {
            throw new IllegalArgumentException(
                    "Article number already exists: " + articleNumber
            );
        }

        items.put(articleNumber, item);
    }


    public Item findByArticleNumber(int articleNumber) {
        return items.get(articleNumber);
    }

}
