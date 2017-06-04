package com.factoriodb.model;

/**
 * @author austinjones
 */
public class Item {
    private String name;

    public Item(String name, ItemType type) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
