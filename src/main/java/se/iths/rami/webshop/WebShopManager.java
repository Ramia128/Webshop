package se.iths.rami.webshop;

import se.iths.rami.webshop.model.Discount;
import se.iths.rami.webshop.model.HalfPriceDiscount;
import se.iths.rami.webshop.model.Product;
import se.iths.rami.webshop.model.TenPercentDiscount;

import javax.swing.*;
import java.awt.*;
import java.io.UncheckedIOException;
import java.util.List;

public class WebShopManager {
    private ProductStorage productStorage;

    public WebShopManager(ProductStorage productStorage) {
        this.productStorage = productStorage;
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
        webshopTheme();
        Discount discount = discountMenu();

        if (discount == null) {
            return;
        }

        while (true) {
            String choice = JOptionPane.showInputDialog(null,
                    """
                            1. Add product
                            2. Show all products
                            3. Search for a product
                            4. Exit
                            """, "Webshop menu", JOptionPane.PLAIN_MESSAGE);

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

                default -> errorMessage("Invalid choice", "Error");
            }
        }
    }

    private Discount discountMenu() {
        Discount discount = null;
        JPanel panel = createPanel(1, 2);
        JTextField discountField = new JTextField(15);

        panel.add(new JLabel("Enter discount code:"));
        panel.add(discountField);

        while (true) {
            int panelWindow = confirmDialog(panel, "Dicount code");

            if (panelWindow != JOptionPane.OK_OPTION) {
                return discount;
            }

            String discountCode = discountField.getText().trim();
            if (discountCode.equals("tenpercent")) {
                discount = new TenPercentDiscount();
            } else if (discountCode.equals("halfprice")) {
                discount = new HalfPriceDiscount();
            } else {
                errorMessage("Invalid code: please enter tenpercent or halfprice",
                        "Invalid discount code");
                discountField.setText("");
                continue;
            }

            plainMessage(discount.getDescription() + " added", "Discount applied");
            return discount;
        }
    }

    private void getProductsMenu(Discount discount) {
        List<Product> products = getProductsFromStorage();

        if (products.isEmpty()) {
            plainMessage("No products found!", "Products");
            return;
        }

        createProductTable(products, discount, "All products");
    }

    private void getProductMenu(Discount discount) {
        JPanel panel = createPanel(1, 2);

        JTextField articleNumberField = new JTextField(10);
        panel.add(new JLabel("Article number:"));
        panel.add(articleNumberField);

        while (true) {

            try {
                int panelWindow = confirmDialog(panel, "Search product");
                String articleNumber = articleNumberField.getText().trim();

                if (panelWindow != JOptionPane.OK_OPTION) {
                    return;
                }

                Product product = getProductFromStorage(articleNumber);
                if (product == null) {
                    throw new IllegalArgumentException("Product not found!");
                }

                createProductTable(List.of(product), discount, "Product info");
            } catch (IllegalArgumentException e) {
                errorMessage(e.getMessage(), "Error");
                articleNumberField.setText("");
            }
        }
    }


    private void addProductMenu() {
        JPanel panel = createPanel(4, 2);

        JTextField articleNumberField = new JTextField(10);
        JTextField titleField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JTextField descriptionField = new JTextField(10);

        panel.add(new JLabel("Article Number:"));
        panel.add(articleNumberField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        while (true) {
            int panelWindow = confirmDialog(panel, "Add product");

            if (panelWindow != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                String articleNumber = articleNumberField.getText().trim();
                String title = titleField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String description = descriptionField.getText().trim();

                Product product = new Product(articleNumber, title, price, description);
                saveProductToStorage(product);

                plainMessage("Successfully added " + title + " to storage!", "Storage add");
                return;

            } catch (NumberFormatException e) {
                errorMessage("Price must be a number", "Error");
            } catch (UncheckedIOException | IllegalArgumentException e) {
                errorMessage(e.getMessage(), "Error");
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
            ;
            data[i][3] = product.getDescription();
        }

        JTable table = new JTable(data, columns);
        table.setCellSelectionEnabled(true);
        table.setAutoCreateRowSorter(true);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 200));

        JOptionPane.showMessageDialog(null, scrollPane, title,
                JOptionPane.PLAIN_MESSAGE);
    }

    private int confirmDialog(JPanel panel, String title) {
        return JOptionPane.showConfirmDialog(null, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void errorMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void plainMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel createPanel(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        return panel;
    }

    private void webshopTheme() {
        Color background = new Color(225, 225, 225);
        Color textColor = new Color(35, 45, 60);

        UIManager.put("OptionPane.background", background);
        UIManager.put("Panel.background", background);
        UIManager.put("OptionPane.messageForeground", textColor);
        UIManager.put("Label.foreground", textColor);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", textColor);
    }
}
