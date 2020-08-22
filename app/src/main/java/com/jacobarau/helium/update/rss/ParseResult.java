package com.jacobarau.helium.update.rss;

import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;

import java.util.Arrays;
import java.util.List;

public class ParseResult {
    public Subscription subscription;
    public List<Item> items;

    @Override
    public String toString() {
        return "ParseResult{" +
                "subscription=" + subscription +
                ", items=" + Arrays.toString(items.toArray()) +
                '}';
    }
}
