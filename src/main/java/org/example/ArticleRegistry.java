package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ArticleRegistry {

    private final Map<Integer, Item> items = new TreeMap<>();

    public void addItem(int articleNumber, Item item) {
        items.put(articleNumber, item);
    }

    public Item findByArticleNumber(int articleNumber) {
        return items.get(articleNumber);
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
}
