public class DiscountCalculator {
    public double calculate(String customerType) {
        if (customerType.equals("VIP")) {
            return 0.2;
        } else if (customerType.equals("Regular")) {
            return 0.1;
        }
        return 0;
    }
}
