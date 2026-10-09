package se.iths.rami.webshop.model;

public class TenPercentDiscount extends Discount {

    public TenPercentDiscount() {
        super("10% Discount");
    }

    @Override
    public double calculatePrice(double originalPrice) {
        return 0.9 * originalPrice;
    }
    
}
