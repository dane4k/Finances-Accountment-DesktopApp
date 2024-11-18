package org.example.finapp.models;

public class CategoryItem {
    private String name;
    private boolean isDeletable;

    public CategoryItem(String name, boolean isDeletable) {
        this.name = name;
        this.isDeletable = isDeletable;
    }

    public String getName() {
        return name;
    }

    public boolean isDeletable() {
        return isDeletable;
    }
}