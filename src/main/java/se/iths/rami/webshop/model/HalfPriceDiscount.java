package se.iths.rami.webshop.model;

public class HalfPriceDiscount extends Discount{

    public HalfPriceDiscount(){
        super("50% Discount");
    }

    @Override
    public double calculatePrice(double originalPrice) {
        return 0.5 * originalPrice;
    }
}
