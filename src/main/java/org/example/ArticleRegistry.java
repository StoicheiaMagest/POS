package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleRegistry {

    //private final Map<Integer, Item> items = new HashMap<>();

    private ArrayList<Item> items = new ArrayList<>();

    //bara item?
    /*public void addItem(int articleNumber, Item item) {
        items.put(articleNumber, item);
    }*/

    public void addItem(Item item){
        items.add(item);
    }

    public ArrayList<Item> getItems(){
        return items;
    }

    public Item findByArticleNumber(int articleNumber) {
        return items.get(articleNumber);
    }
}
