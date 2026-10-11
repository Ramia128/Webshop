package se.iths.rami.webshop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import se.iths.rami.webshop.model.Product;

import java.io.UncheckedIOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductFileStorageTest {
    @TempDir
    Path tempdir;

    ProductStorage productStorage;
    Product product;

    @BeforeEach
    void setUp() {
        productStorage = new ProductFileStorage(tempdir.resolve("products-testing.txt"));
        product = new Product("New Product", "Test", 777, "from test");
    }

    @Test
    @DisplayName("New product saves to file")
    void saveNewProduct() {
        productStorage.saveProduct(product);
        assertNotNull(productStorage.getProduct(product.getArticleNumber()));
    }

    @Test
    @DisplayName("Existing product throws null")
    void saveDuplicateProduct() {
        productStorage.saveProduct(product);
        assertThrows(IllegalArgumentException.class, () -> productStorage.saveProduct(product));
    }

    @Test
    @DisplayName("Save file with different case throws exception")
    void saveDuplicateCaseProduct() {
        productStorage.saveProduct(product);
        product.setArticleNumber("NEW PRoduct");
        assertThrows(IllegalArgumentException.class, () -> productStorage.saveProduct(product));
    }

    @Test
    @DisplayName("Saving to file inside directory throws exeption")
    void saveMissingDirectoryFile() {
        ProductStorage invalidStorage = new ProductFileStorage(tempdir.resolve("data/products.txt"));
        assertThrows(UncheckedIOException.class, () -> invalidStorage.saveProduct(product));
    }


}
