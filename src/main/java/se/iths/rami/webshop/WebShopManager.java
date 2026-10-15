package se.iths.rami.webshop;

import se.iths.rami.webshop.model.Discount;
import se.iths.rami.webshop.model.HalfPriceDiscount;
import se.iths.rami.webshop.model.Product;
import se.iths.rami.webshop.model.TenPercentDiscount;

import java.io.UncheckedIOException;
import java.util.List;

public class WebShopManager {
    private ProductStorage productStorage;
    private OutInputHandler outInputHandler;

    public WebShopManager(ProductStorage productStorage, OutInputHandler outInputHandler) {
        this.productStorage = productStorage;
        this.outInputHandler = outInputHandler;
    }

    void saveProductToStorage(Product product) {
        productStorage.saveProduct(product);
    }

    public List<Product> getProductsFromStorage() {
        return productStorage.getProducts();
    }

    public Product getProductFromStorage(String articleNumber) {
        return productStorage.getProduct(articleNumber);
    }

    void startWebshop() {
        outInputHandler.theme();
        Discount discount = discountMenu();

        if (discount == null) {
            return;
        }

        while (true) {
            String choice = outInputHandler.menu();

            if (choice == null) {
                return;
            }

            switch (choice) {
                case "1" -> addProductMenu();
                case "2" -> getProductsMenu(discount);
                case "3" -> getProductMenu(discount);
                case "4" -> {
                    return;
                }

                default -> outInputHandler.errorInfo("Invalid choice");
            }
        }
    }

    public Discount discountMenu() {
        Discount discount = null;
        String[] labels = {"Enter discount code:"};
        String[] values = new String[labels.length];

        while (true) {

            String[] input = outInputHandler.prompt("Discount Code", labels, values);

            if (input == null) {
                return discount;
            }

            String discountCode = input[0];

            if (discountCode.equals("tenpercent")) {
                discount = new TenPercentDiscount();
            } else if (discountCode.equals("halfprice")) {
                discount = new HalfPriceDiscount();
            } else {
                outInputHandler.errorInfo("Invalid code: please enter tenpercent or halfprice");
                continue;
            }

            outInputHandler.info(discount.getDescription() + " added", "Discount applied");
            return discount;
        }
    }

    public void getProductsMenu(Discount discount) {
        List<Product> products;
        try {
            products = getProductsFromStorage();

        } catch (UncheckedIOException e) {
            outInputHandler.errorInfo(e.getMessage());
            return;
        }


        if (products.isEmpty()) {
            outInputHandler.info("No products found!", "Products");
            return;
        }

        createProductTable(products, discount, "All products");
    }

    public void getProductMenu(Discount discount) {
        String[] labels = {"Article number:"};
        String[] values = new String[labels.length];
        while (true) {
            try {
                String[] input = outInputHandler.prompt("Search product", labels, values);

                if (input == null) {
                    return;
                }

                Product product = getProductFromStorage(input[0]);
                if (product == null) {
                    throw new IllegalArgumentException("Product not found!");
                }

                createProductTable(List.of(product), discount, "Product info");
            } catch (UncheckedIOException | IllegalArgumentException e) {
                outInputHandler.errorInfo(e.getMessage());
            }
        }
    }


    public void addProductMenu() {
        String[] labels = {"Article Number:", "Title:", "Price:", "Description"};
        String[] values = new String[labels.length];
        while (true) {

            String[] input = outInputHandler.prompt("Add product", labels, values);

            if (input == null) {
                return;
            }

            values = input;

            try {
                String articleNumber = input[0];
                String title = input[1];

                if (input[2] == null || input[2].isBlank()) {
                    throw new IllegalArgumentException("Price cant be empty");
                }

                double price = Double.parseDouble(input[2]);
                String description = input[3];

                Product product = new Product(articleNumber, title, price, description);
                saveProductToStorage(product);

                outInputHandler.info("Successfully added " + title + " to storage!", "Storage add");
                return;

            } catch (NumberFormatException e) {
                outInputHandler.errorInfo("Price must be a number");
            } catch (UncheckedIOException | IllegalArgumentException e) {
                outInputHandler.errorInfo(e.getMessage());
            }
        }
    }

    private void createProductTable(List<Product> products, Discount discount, String title) {
        String[] columns = {"Article number", "Title", "Price", "Description"};
        Object[][] data = new Object[products.size()][4];

        for (int i = 0; i < products.size(); i++) {

            Product product = products.get(i);

            data[i][0] = product.getArticleNumber();
            data[i][1] = product.getTitle();
            data[i][2] = String.format("%.2f", discount.calculatePrice(product.getPrice()));
            data[i][3] = product.getDescription();

        }

        outInputHandler.showTable(title, columns, data);
    }
}
