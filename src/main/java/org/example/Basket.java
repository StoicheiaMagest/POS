
package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Basket {
    private int totalPrice;
    private ArrayList<Item> items;

    public Basket() {
        this.items = new ArrayList<>();
        this.totalPrice = 0;
    }

    public void addItem(Item item) {
        this.items.add(item);
        this.totalPrice = this.totalPrice + item.getPrice();
    }

    public void removeItem(Item item) {
        if (this.items.remove(item)) {
            this.totalPrice = this.totalPrice - item.getPrice();
        }
    }

    /**
     * Removes one entry whose handle matches the given name, case-
     * insensitively: one piece for unit-priced articles, the whole weighed
     * entry for weighed goods (spec &sect;4.7, assumption A2).
     *
     * @param name the article handle, e.g. {@code soy-milk}
     * @return the removed item, or null when the basket has no such item
     */
    public Item removeOne(String name) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equalsIgnoreCase(name)) {
                Item removed = items.remove(i);
                totalPrice -= removed.getPrice();
                return removed;
            }
        }
        return null;
    }

    /**
     * Groups the basket for display: identical unit-priced articles are
     * counted into one line, weighed entries each keep their own line with
     * their weight (spec &sect;4.8). Insertion order is preserved.
     *
     * @return one line per distinct article/weighed entry
     */
    public List<BasketLine> lines() {
        List<BasketLine> lines = new ArrayList<>();
        Map<String, Integer> unitLineIndexByName = new HashMap<>();
        for (Item item : items) {
            if (item instanceof WeighedItem weighed) {
                lines.add(new BasketLine(weighed.getWeight() + "g",
                        item.getFullDescription()));
            } else {
                Integer index = unitLineIndexByName.get(item.getName());
                if (index == null) {
                    lines.add(new BasketLine("1", item.getFullDescription()));
                    unitLineIndexByName.put(item.getName(),
                            lines.size() - 1);
                } else {
                    BasketLine line = lines.get(index);
                    int count = Integer.parseInt(line.getQuantity()) + 1;
                    lines.set(index, new BasketLine(
                            String.valueOf(count), line.getDescription()));
                }
            }
        }
        return lines;
    }

    public ArrayList<Item> getItems() {
        return new ArrayList<>(items);
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
