// S - 各类（Spot, Ticket, Receipt）职责单一
// O - 支付方式通过 PaymentStrategy 轻松扩展
// L - 任意 PaymentStrategy 替换不影响逻辑
// I - PaymentStrategy 精简职责
// D - ParkingService 依赖 PaymentStrategy 抽象而不是具体实现

// Vehicle Entity
public class Vehicle {
    private final String licensePlate;
    private final VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public VehicleType getType() { return type; }
    public String getLicensePlate() { return licensePlate; }
}

public enum VehicleType {
    COMPACT, LARGE, HANDICAPPED
}

// ParkingSpot Entity
public class ParkingSpot {
    private final String id;
    private final VehicleType type;
    private boolean available = true;

    public ParkingSpot(String id, VehicleType type) {
        this.id = id;
        this.type = type;
    }

    public boolean isAvailable() { return available; }
    public void occupy() { available = false; }
    public void release() { available = true; }
    public VehicleType getType() { return type; }
}

// Ticket Entity
public class Ticket {
    private final String id;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(String id, ParkingSpot spot) {
        this.id = id;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }

    public void markExit() {
        this.exitTime = LocalDateTime.now();
    }

    public long getDurationMinutes() {
        return java.time.Duration.between(entryTime, exitTime).toMinutes();
    }

    public ParkingSpot getSpot() { return spot; }
}

// 业务逻辑 - 分配停车位
public class ParkingLot {
    private final List<ParkingSpot> spots = new ArrayList<>(); // 看要求，如果只有一个实现，可以省去interface定义，但一定要告知

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public Optional<ParkingSpot> allocateSpot(VehicleType type) {
        return spots.stream()
                .filter(s -> s.isAvailable() && s.getType() == type)
                .findFirst();
    }

    public void releaseSpot(ParkingSpot spot) {
        spot.release();
    }
}

// Payment - D - Strategy
public interface PaymentStrategy {
    Receipt pay(double amount);
}

public class CreditCardPayment implements PaymentStrategy {
    public Receipt pay(double amount) {
        System.out.println("Paid $" + amount + " via Credit Card");
        return new Receipt(amount, "CreditCard");
    }
}

public class CashPayment implements PaymentStrategy {
    public Receipt pay(double amount) {
        System.out.println("Paid $" + amount + " via Cash");
        return new Receipt(amount, "Cash");
    }
}

public class PaymentProcessor {
    private final PaymentStrategy strategy; // 依赖 抽象，not 具体

    public PaymentProcessor(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public Receipt process(double amount) {
        return strategy.pay(amount);
    }
}

public class Receipt {
    private final double amount;
    private final String method;

    public Receipt(double amount, String method) {
        this.amount = amount;
        this.method = method;
    }
}

// 业务逻辑
public class ParkingService {
    private final ParkingLot lot;

    public ParkingService(ParkingLot lot) {
        this.lot = lot;
    }

    public Ticket enter(Vehicle vehicle) {
        Optional<ParkingSpot> spotOpt = lot.allocateSpot(vehicle.getType());
        if (spotOpt.isEmpty()) throw new RuntimeException("No available spots");

        ParkingSpot spot = spotOpt.get();
        spot.occupy();
        return new Ticket(UUID.randomUUID().toString(), spot);
    }

    public void exit(Ticket ticket, PaymentProcessor paymentProcessor) { // 高层“只”依赖抽象
        ticket.markExit();
        long minutes = ticket.getDurationMinutes();
        double amount = Math.ceil(minutes / 60.0) * 5; // $5 per hour

        paymentProcessor.process(amount);
        lot.releaseSpot(ticket.getSpot());
    }
}

// 一种业务驱动写法，mock流程
public class Main {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot();
        lot.addSpot(new ParkingSpot("S1", VehicleType.COMPACT));

        ParkingService service = new ParkingService(lot);
        Vehicle car = new Vehicle("ABC-123", VehicleType.COMPACT);

        Ticket ticket = service.enter(car);

        // After 2 hours...
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        PaymentProcessor processor = new PaymentProcessor(new CreditCardPayment());
        service.exit(ticket, processor);
    }
}
