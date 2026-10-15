package se.iths.rami.webshop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import se.iths.rami.webshop.model.Discount;
import se.iths.rami.webshop.model.HalfPriceDiscount;
import se.iths.rami.webshop.model.Product;
import se.iths.rami.webshop.model.TenPercentDiscount;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WebShopManagerTest {
    @TempDir
    Path tempDir;

    private ProductStorage tempStorage;
    private TestHandler outInputHandler;
    private WebShopManager webShopManager;
    private Product product;

    @BeforeEach
    void setUp() {
        tempStorage = new ProductFileStorage(tempDir.resolve("test.txt"));
        outInputHandler = new TestHandler();
        product = new Product("B001", "Java testning",
                799, "Bok om javatestning");
        webShopManager = new WebShopManager(tempStorage, outInputHandler);

    }

    @Test
    @DisplayName("New product saves to file")
    void saveNewProduct() {
        webShopManager.saveProductToStorage(product);
        Product savedProduct = webShopManager.getProductFromStorage(product.getArticleNumber());
        assertEquals("B001", savedProduct.getArticleNumber());
        assertEquals("Java testning", savedProduct.getTitle());
        assertEquals(799, savedProduct.getPrice());
        assertEquals("Bok om javatestning", savedProduct.getDescription());
    }

    @Test
    @DisplayName("Saving multiple products to file")
    void saveMultipleProducts() {
        webShopManager.saveProductToStorage(product);
        webShopManager.saveProductToStorage(
                new Product("E001", "Fläkt",
                        999, "Fläkt med tre hastigheter"));
        assertEquals(2, webShopManager.getProductsFromStorage().size());
    }

    @Test
    @DisplayName("Existing product throws null")
    void saveDuplicateProduct() {
        webShopManager.saveProductToStorage(product);
        assertThrows(IllegalArgumentException.class, () -> webShopManager.saveProductToStorage(product));
        assertEquals(1, webShopManager.getProductsFromStorage().size());
    }

    @Test
    @DisplayName("Save file with different case throws exception")
    void saveDuplicateCaseProduct() {
        webShopManager.saveProductToStorage(product);
        product.setArticleNumber("b001");
        assertThrows(IllegalArgumentException.class, () -> webShopManager.saveProductToStorage(product));
    }

    @Test
    @DisplayName("Saving to file inside directory throws exception")
    void saveMissingDirectoryFile() {
        ProductStorage invalidStorage = new ProductFileStorage(tempDir.resolve("data/products.txt"));
        webShopManager = new WebShopManager(invalidStorage, outInputHandler);
        assertThrows(UncheckedIOException.class, () -> webShopManager.saveProductToStorage(product));
    }

    @Test
    @DisplayName("Return empty list on no product")
    void getProductsReturnEmptyList() {
        assertTrue(webShopManager.getProductsFromStorage().isEmpty());
        assertEquals(0, webShopManager.getProductsFromStorage().size());
    }

    @Test
    @DisplayName("Throws exception when cant read file")
    void getProductsCantReadFile() throws IOException {
        Path file = tempDir.resolve("invalid.txt");
        Files.writeString(file, "ÿ", StandardCharsets.ISO_8859_1);
        tempStorage = new ProductFileStorage(file);
        webShopManager = new WebShopManager(tempStorage, outInputHandler);
        assertThrows(UncheckedIOException.class, () -> webShopManager.getProductsFromStorage());
    }

    @Test
    @DisplayName("getProduct not null if exists")
    void getProductNotNull() {
        webShopManager.saveProductToStorage(product);
        assertNotNull(webShopManager.getProductFromStorage(product.getArticleNumber()));
    }

    @Test
    @DisplayName("getProduct null if not exists")
    void getProductNull() {
        assertNull(webShopManager.getProductFromStorage(product.getArticleNumber()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "    "})
    @DisplayName("exception when articlenumber is null or empty")
    void getProductException(String articleNumber) {
        assertThrows(IllegalArgumentException.class, () -> webShopManager.getProductFromStorage(articleNumber));
    }

    @Test
    @DisplayName("getProduct gets product when using different case")
    void getProductReturnCorrectOnDifferentCase() {
        webShopManager.saveProductToStorage(product);
        Product savedProduct = webShopManager.getProductFromStorage("b001");
        assertEquals("B001", savedProduct.getArticleNumber());
    }

    @Test
    @DisplayName("Applies tenpercent on correct input")
    void discountMenuTenPercent() {
        webShopManager.saveProductToStorage(product);
        outInputHandler.promptInput = new String[]{"tenpercent"};
        Discount discount = webShopManager.discountMenu();

        assertInstanceOf(TenPercentDiscount.class, discount);
        assertEquals(0.9 * product.getPrice(),
                discount.calculatePrice(webShopManager.getProductFromStorage("B001").getPrice()));
        assertEquals("10% Discount added", outInputHandler.infoMessage);
    }

    @Test
    @DisplayName("Applies halfprice on correct input")
    void discountMenuHalfPrice() {
        webShopManager.saveProductToStorage(product);
        outInputHandler.promptInput = new String[]{"halfprice"};
        Discount discount = webShopManager.discountMenu();

        assertInstanceOf(HalfPriceDiscount.class, discount);
        assertEquals(0.5 * product.getPrice(),
                discount.calculatePrice(webShopManager.getProductFromStorage("B001").getPrice()));
        assertEquals("50% Discount added", outInputHandler.infoMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"TENPERCENT", "HALFPRICE", " ", "!"})
    @DisplayName("Error message on wrong input")
    void discountMenuErrorMessage(String message) {
        outInputHandler.promptInput = new String[]{message};
        webShopManager.discountMenu();
        assertEquals("Invalid code: please enter tenpercent or halfprice", outInputHandler.errorMessage);
    }

    @Test
    @DisplayName("Discount is null when input is null")
    void discountNullWhenInputNull() {
        outInputHandler.promptInput = null;
        Discount discount = webShopManager.discountMenu();
        assertNull(discount);
    }

    @Test
    @DisplayName("Tenpercent discount applies on getProductsMenu")
    void getProductsTenPercent() {
        multipleProduct();
        outInputHandler.promptInput = new String[]{"tenpercent"};
        Discount discount = webShopManager.discountMenu();

        List<Product> products = webShopManager.getProductsFromStorage();
        webShopManager.getProductsMenu(discount);

        assertNotNull(outInputHandler.testData);
        assertEquals(4, products.size());
        assertEquals(products.size(), outInputHandler.testData.length);
        for (int i = 0; i < products.size(); i++) {
            product = products.get(i);
            assertEquals(String.format("%.2f", 0.9 * product.getPrice()), outInputHandler.testData[i][2]);
        }

    }

    @Test
    @DisplayName("getProductsMenu correct data with halfprice and length for creating table")
    void getProductsCorrectDataAndLength() {
        multipleProduct();
        outInputHandler.promptInput = new String[]{"halfprice"};
        Discount discount = webShopManager.discountMenu();

        List<Product> products = webShopManager.getProductsFromStorage();
        webShopManager.getProductsMenu(discount);

        assertNotNull(outInputHandler.testData);
        assertEquals(4, products.size());
        assertEquals(products.size(), outInputHandler.testData.length);
        for (int i = 0; i < products.size(); i++) {
            product = products.get(i);

            assertEquals(product.getArticleNumber(), outInputHandler.testData[i][0]);
            assertEquals(product.getTitle(), outInputHandler.testData[i][1]);
            assertEquals(String.format("%.2f", 0.5 * product.getPrice()), outInputHandler.testData[i][2]);
            assertEquals(product.getDescription(), outInputHandler.testData[i][3]);

        }

    }

    @Test
    @DisplayName("getProductsMenu shows message when no product found")
    void getProductsNoProductMessage() {
        outInputHandler.promptInput = new String[]{"tenpercent"};
        Discount discount = webShopManager.discountMenu();
        webShopManager.getProductsMenu(discount);
        assertEquals("No products found!", outInputHandler.infoMessage);
    }

    @Test
    @DisplayName("getProductMenu correct data for table")
    void getProductMenuShowCorrectData() {
        webShopManager.saveProductToStorage(product);
        Discount discount = new TenPercentDiscount();
        outInputHandler.promptInput = new String[]{"B001"};
        List<Product> products = List.of(webShopManager.getProductFromStorage("B001"));
        webShopManager.getProductMenu(discount);

        assertNotNull(outInputHandler.testData);
        assertEquals(1, outInputHandler.testData.length);

        for (int i = 0; i < products.size(); i++) {
            product = products.get(i);

            assertEquals(product.getArticleNumber(), outInputHandler.testData[i][0]);
            assertEquals(product.getTitle(), outInputHandler.testData[i][1]);
            assertEquals(String.format("%.2f", 0.9 * product.getPrice()), outInputHandler.testData[i][2]);
            assertEquals(product.getDescription(), outInputHandler.testData[i][3]);

        }
    }

    @Test
    @DisplayName("getProductMenu error message when product not found")
    void getProductMenuException() {
        Discount discount = new TenPercentDiscount();
        outInputHandler.promptInput = new String[]{"NoProduct"};
        webShopManager.getProductMenu(discount);
        assertEquals("Product not found!", outInputHandler.errorMessage);
    }

    @Test
    @DisplayName("addProductMenu adds product when input is correct")
    void addProductMenuAddingProduct() {
        outInputHandler.promptInput = new String[]{"E003", "Subwoofer", "1899", "Newest Subwoofer"};
        webShopManager.addProductMenu();
        Product newProduct = webShopManager.getProductFromStorage("E003");
        assertEquals("Successfully added Subwoofer to storage!",
                outInputHandler.infoMessage);

        assertEquals("E003", newProduct.getArticleNumber());
        assertEquals("Subwoofer", newProduct.getTitle());
        assertEquals(1899, newProduct.getPrice());
        assertEquals("Newest Subwoofer", newProduct.getDescription());
    }

    @Test
    @DisplayName("adding existing product shows message")
    void addProductMenuExistingProduct() {
        webShopManager.saveProductToStorage(product);
        outInputHandler.promptInput = new String[]{"B001", "Java testning", "799", "Bok om javatestning"};
        webShopManager.addProductMenu();
        assertEquals("Product already exists", outInputHandler.errorMessage);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("addProduct error message when articlenumber is empty or null")
    void addProductArticleNumberEmpty(String source) {
        outInputHandler.promptInput = new String[]{source, "Subwoofer", "1899", "Newest Subwoofer"};
        webShopManager.addProductMenu();
        assertEquals("Article number cant be empty", outInputHandler.errorMessage);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("addProduct error message when price is empty or null")
    void addProductPriceEmpty(String source) {
        outInputHandler.promptInput = new String[]{"E003", "Subwoofer", source, "Newest Subwoofer"};
        webShopManager.addProductMenu();
        assertEquals("Price cant be empty", outInputHandler.errorMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-100", "-1", "NaN", "Infinity", "-Infinity"})
    @DisplayName("addProduct error message when price is null or negative")
    void addProductPriceNullOrNegative(String source) {
        outInputHandler.promptInput = new String[]{"E003", "Subwoofer", source, "Newest Subwoofer"};
        webShopManager.addProductMenu();
        assertEquals("Price must be positive number", outInputHandler.errorMessage);
    }

    @Test
    @DisplayName("Price Numberformatexception when not a number")
    void addProductPriceNumberException() {
        outInputHandler.promptInput = new String[]{"E003", "Subwoofer", "source", "Newest Subwoofer"};
        webShopManager.addProductMenu();
        assertEquals("Price must be a number", outInputHandler.errorMessage);
    }


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("addProduct error message when description is empty or null")
    void addProductDescriptionEmpty(String source) {
        outInputHandler.promptInput = new String[]{"E003", "Subwoofer", "1899", source};
        webShopManager.addProductMenu();
        assertEquals("Description cant be empty", outInputHandler.errorMessage);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("addProduct error message when title is empty")
    void addProductTitleEmpty(String source) {
        outInputHandler.promptInput = new String[]{"E003", source, "1899", "Newest Subwoofer"};
        webShopManager.addProductMenu();
        assertEquals("Title cant be empty", outInputHandler.errorMessage);
    }

    void multipleProduct() {
        webShopManager.saveProductToStorage(product);
        webShopManager.saveProductToStorage(
                new Product("test2", "testing2", 800, "more test"));
        webShopManager.saveProductToStorage(
                new Product("test3", "testing3", 600, "more test"));
        webShopManager.saveProductToStorage(
                new Product("test4", "testing4", 550, "more test"));
    }
}
