package com.factoriodb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author austinjones
 */
public class Items implements Iterable<Item> {
    private Map<String, Item> map;
    private Collection<Item> items;

    public Items(Collection<Item> items) {
        this.items = new ArrayList<>(items);
        this.map = items.stream()
                .filter((e) -> e.name() != null)
                .collect(Collectors.toMap((e) -> e.name(), (e) -> e, (a,b) -> a));
    }

    public Item get(String name) {
        return map.get(name);
    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }
}
