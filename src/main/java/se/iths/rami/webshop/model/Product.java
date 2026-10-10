package se.iths.rami.webshop.model;

public class Product {
    private String articleNumber;
    private String title;
    private double price;
    private String description;

    public Product(String articleNumber, String title,
                   double price, String description) {
        if (articleNumber == null || articleNumber.isBlank()) {
            throw new IllegalArgumentException("Article number cant be empty");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cant be empty");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cant be empty");
        }

        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Price must be positive number");
        }

        this.articleNumber = articleNumber;
        this.title = title;
        this.price = price;
        this.description = description;

    }

    public String toFileLine() {
        return articleNumber + ";" + title + ";" + price + ";" + description;
    }

    public static Product fromFileLine(String line) {
        String[] lineSplitter = line.split(";");

        if (lineSplitter.length != 4) {
            throw new IllegalArgumentException("4 fields needed with ; between each one");
        }

        for (String field : lineSplitter) {
            if (field.isBlank()) {
                throw new IllegalArgumentException("Fields cant be empty!");
            }
        }
        try {
            String articleNumber = lineSplitter[0];
            String title = lineSplitter[1];
            double price = Double.parseDouble(lineSplitter[2]);
            String description = lineSplitter[3];

            return new Product(articleNumber, title, price, description);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format, price must be a number");
        }
    }

    //Get
    public String getArticleNumber() {
        return articleNumber;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    //Set
    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
