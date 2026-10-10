package se.iths.rami.webshop;

import se.iths.rami.webshop.model.Product;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ProductFileStorage implements ProductStorage {
    private Path file = Path.of("products.txt");

    @Override
    public void saveProduct(Product product) {
        try {
            Files.writeString(file, product.toFileLine() + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed saving product to file", e);
        }
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        if (Files.notExists(file)) {
            return products;
        }

        try {
            List<String> lines = Files.readAllLines(file);

            for (String line : lines) {
                Product product = Product.fromFileLine(line);
                products.add(product);
            }

            return products;

        } catch (IOException e) {
            throw new UncheckedIOException("Couldnt read file", e);
        }
    }

    @Override
    public Product getProduct(String articleNumber) {
        List<Product> products = getProducts();

        for (Product product : products) {
            if (product.getArticleNumber().equals(articleNumber)) {
                return product;
            }
        }
        return null;
    }


}
