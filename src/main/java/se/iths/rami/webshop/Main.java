package se.iths.rami.webshop;

import se.iths.rami.webshop.model.Discount;
import se.iths.rami.webshop.model.HalfPriceDiscount;
import se.iths.rami.webshop.model.Product;
import se.iths.rami.webshop.model.TenPercentDiscount;

public class Main {
    static void main() {
//        Product product = new Product("test", "test", 45, "test");
//        IO.println(product.toFileLine());

        Product product = Product.fromFileLine("aftertest;after;100;desc");
        IO.println(product.toFileLine());

        String discountCode = IO.readln("Enter discount code: ");
        Discount discount = null;

        if (discountCode.equals("halfprice")) {
            discount = new HalfPriceDiscount();
        } else if (discountCode.equals("tenpercent")) {
            discount = new TenPercentDiscount();
        } else {
            throw new IllegalArgumentException("Please enter discount code: halfprice or tenpercent");
        }

        IO.println(discount.calculatePrice(product.getPrice()));
    }
}
