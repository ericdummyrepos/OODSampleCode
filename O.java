public interface DiscountStrategy {
    double getDiscount();
}

public class VipDiscount implements DiscountStrategy {
    @Override
    public double getDiscount() {
        return 0.2;
    }
}

public class RegularDiscount implements DiscountStrategy {
    @Override
    public double getDiscount() {
        return 0.1;
    }
}

public class NoDiscount implements DiscountStrategy {
    @Override
    public double getDiscount() {
        return 0.0;
    }
}

public class DiscountCalculator {
    public double calculate(DiscountStrategy strategy) {
        return strategy.getDiscount();
    }
}

public class Main {
    public static void main(String[] args) {
        DiscountCalculator calculator = new DiscountCalculator();

        DiscountStrategy vip = new VipDiscount();
        DiscountStrategy regular = new RegularDiscount();
        DiscountStrategy none = new NoDiscount();

        System.out.println("VIP Discount: " + calculator.calculate(vip));
        System.out.println("Regular Discount: " + calculator.calculate(regular));
        System.out.println("No Discount: " + calculator.calculate(none));
    }
}
