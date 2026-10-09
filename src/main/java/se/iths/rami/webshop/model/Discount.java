package se.iths.rami.webshop.model;

public abstract class Discount {
    protected String description;

    public Discount(String description) {
        this.description = description;
    }

    public abstract double calculatePrice(double originalPrice);

    //GET
    public String getDescription() {
        return description;
    }
}
