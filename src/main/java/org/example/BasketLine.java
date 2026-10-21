package org.example;

/**
 * One display line of a basket: a quantity label ("3", "300g") and the full
 * description of the article it refers to (spec &sect;4.8).
 */
public class BasketLine {

    private final String quantity;
    private final String description;

    public BasketLine(String quantity, String description) {
        this.quantity = quantity;
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }
}
