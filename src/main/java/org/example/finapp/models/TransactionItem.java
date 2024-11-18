package org.example.finapp.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class TransactionItem {
    private DoubleProperty amount;
    private ObjectProperty<LocalDate> date;
    private StringProperty type;
    private StringProperty category;
    private IntegerProperty categoryId;

    public TransactionItem(double amount, LocalDate date, String type, String category) {
        this.amount = new SimpleDoubleProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
        this.type = new SimpleStringProperty(type);
        this.category = new SimpleStringProperty(category);
        this.categoryId = new SimpleIntegerProperty(-1);
    }


    public double getAmount() {
        return amount.get();
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }
}