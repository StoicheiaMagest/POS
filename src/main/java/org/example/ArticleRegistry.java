package org.example;

import java.util.Collection;
import java.util.Collections;
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

    // All articles, sorted by article number.
    public Collection<Item> getAllItems() {
        return Collections.unmodifiableCollection(items.values());
    }
}
