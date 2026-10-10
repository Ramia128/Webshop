package se.iths.rami.webshop;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ProductStorage storage = new ProductFileStorage();
            WebShopManager webShopManager = new WebShopManager(storage);

            webShopManager.startWebshop();
        });


//        Product product = new Product("test", "test", 45, "test");
//        IO.println(product.toFileLine());

//        ProductStorage productStorage = new ProductFileStorage();
//
//        Product product = productStorage.getProduct("aftetest");
////        IO.println(product.toFileLine());
////
////        for (Product savedProduct : productStorage.getProducts()) {
////            IO.println(savedProduct.toFileLine());
////        }
//
//        IO.println(product.toFileLine());


//        String discountCode = IO.readln("Enter discount code: ");
//        Discount discount = null;
//
//        if (discountCode.equals("halfprice")) {
//            discount = new HalfPriceDiscount();
//        } else if (discountCode.equals("tenpercent")) {
//            discount = new TenPercentDiscount();
//        } else {
//            throw new IllegalArgumentException("Please enter discount code: halfprice or tenpercent");
//        }

//        IO.println("New price: " + discount.calculatePrice(product.getPrice()) + " After " + discount.getDescription());
    }
}
