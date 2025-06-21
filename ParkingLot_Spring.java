// 🚀 Parking Lot - Spring Boot Starter Structure with SOLID & Design Patterns

// --- 1. Model Layer ---

public enum VehicleType {
    COMPACT, LARGE, HANDICAPPED
}

public class Vehicle {
    private String licensePlate;
    private VehicleType type;
    // getters/setters/constructors
}

public class ParkingSpot {
    private String id;
    private VehicleType type;
    private boolean available = true;
    // getters/setters/occupy/release
}

public class Ticket {
    private String id;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    // calculate duration etc.
}

public class Receipt {
    private double amount;
    private String method;
    // constructor/getters
}

// --- 2. Strategy Pattern for Payment ---

public interface PaymentStrategy {
    Receipt pay(double amount);
}

@Service
public class CreditCardPayment implements PaymentStrategy {
    public Receipt pay(double amount) {
        return new Receipt(amount, "CreditCard");
    }
}

@Service
public class CashPayment implements PaymentStrategy {
    public Receipt pay(double amount) {
        return new Receipt(amount, "Cash");
    }
}

// --- 3. Spot Allocation Strategy ---

public interface SpotAllocationStrategy {
    Optional<ParkingSpot> findSpot(List<ParkingSpot> spots, VehicleType type);
}

@Service
public class NearestSpotStrategy implements SpotAllocationStrategy {
    public Optional<ParkingSpot> findSpot(List<ParkingSpot> spots, VehicleType type) {
        return spots.stream()
            .filter(s -> s.isAvailable() && s.getType() == type)
            .findFirst();
    }
}

// --- 4. Observer Pattern ---

public interface ParkingLotObserver {
    void onSpotAllocated(ParkingSpot spot);
    void onSpotReleased(ParkingSpot spot);
}

@Service
public class SpotLogger implements ParkingLotObserver {
    public void onSpotAllocated(ParkingSpot spot) {
        System.out.println("Allocated: " + spot.getId());
    }
    public void onSpotReleased(ParkingSpot spot) {
        System.out.println("Released: " + spot.getId());
    }
}

// --- 5. ParkingLot Singleton Style (managed by Spring) ---

@Service
public class ParkingLot {
    private final List<ParkingSpot> spots = new ArrayList<>();
    private final List<ParkingLotObserver> observers;
    private final SpotAllocationStrategy strategy;

    public ParkingLot(List<ParkingLotObserver> observers, SpotAllocationStrategy strategy) {
        this.observers = observers;
        this.strategy = strategy;
    }

    public Optional<ParkingSpot> allocate(VehicleType type) {
        Optional<ParkingSpot> spot = strategy.findSpot(spots, type);
        spot.ifPresent(s -> {
            s.setAvailable(false);
            observers.forEach(o -> o.onSpotAllocated(s));
        });
        return spot;
    }

    public void release(ParkingSpot spot) {
        spot.setAvailable(true);
        observers.forEach(o -> o.onSpotReleased(spot));
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }
}

// --- 6. ParkingService with Dependency Inversion ---

@Service
public class ParkingService {
    private final ParkingLot parkingLot;

    public ParkingService(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public Ticket enter(Vehicle vehicle) {
        Optional<ParkingSpot> spot = parkingLot.allocate(vehicle.getType());
        if (spot.isEmpty()) throw new RuntimeException("No spot");
        return new Ticket(UUID.randomUUID().toString(), spot.get());
    }

    public Receipt exit(Ticket ticket, PaymentStrategy strategy) {
        ticket.setExitTime(LocalDateTime.now());
        long minutes = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes();
        double cost = Math.ceil(minutes / 60.0) * 5;
        parkingLot.release(ticket.getSpot());
        return strategy.pay(cost);
    }
}

// --- 7. REST Controller ---

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    private final ParkingService parkingService;
    private final CreditCardPayment creditCardPayment;

    public ParkingController(ParkingService service, CreditCardPayment payment) {
        this.parkingService = service;
        this.creditCardPayment = payment;
    }

    @PostMapping("/enter")
    public Ticket enter(@RequestBody Vehicle vehicle) {
        return parkingService.enter(vehicle);
    }

    @PostMapping("/exit")
    public Receipt exit(@RequestBody Ticket ticket) {
        return parkingService.exit(ticket, creditCardPayment);
    }
}
