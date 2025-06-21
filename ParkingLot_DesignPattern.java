----------------------------------------------------
// 使用 Factory Pattern 创建车位和票据
----------------------------------------------------
public class ParkingSpotFactory {
    public static ParkingSpot createSpot(String id, VehicleType type) {
        return new ParkingSpot(id, type);
    }
}

public class TicketFactory {
    public static Ticket createTicket(ParkingSpot spot) {
        return new Ticket(UUID.randomUUID().toString(), spot);
    }
}

// 使用方式
ParkingSpot spot = ParkingSpotFactory.createSpot("A123123", VehicleType.COMPACT);
Ticket ticket = TicketFactory.createTicket(spot);

----------------------------------------------------
// 使用 Strategy Pattern 管理支付方式 和 车位分配策略
----------------------------------------------------
public interface SpotAllocationStrategy {
    Optional<ParkingSpot> findSpot(List<ParkingSpot> spots, VehicleType type);
}

public class NearestSpotStrategy implements SpotAllocationStrategy {
    public Optional<ParkingSpot> findSpot(List<ParkingSpot> spots, VehicleType type) {
        return spots.stream()
                .filter(s -> s.isAvailable() && s.getType() == type)
                .findFirst(); // 最近的第一个
    }
}

public class RandomSpotStrategy implements SpotAllocationStrategy {
    public Optional<ParkingSpot> findSpot(List<ParkingSpot> spots, VehicleType type) {
        Collections.shuffle(spots);
        return spots.stream()
                .filter(s -> s.isAvailable() && s.getType() == type)
                .findFirst(); // 随便哪个open的
    }
}

// ParkingLot 中注入策略
public class ParkingLot {
    private final List<ParkingSpot> spots = new ArrayList<>();
    private final SpotAllocationStrategy allocationStrategy;

    public ParkingLot(SpotAllocationStrategy strategy) {
        this.allocationStrategy = strategy;
    }

    public Optional<ParkingSpot> allocateSpot(VehicleType type) {
        return allocationStrategy.findSpot(spots, type);
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }
}

----------------------------------------------------
// 使用 Observer Pattern 做车位事件通知
----------------------------------------------------
public interface ParkingLotObserver {
    void onSpotAllocated(ParkingSpot spot);
    void onSpotReleased(ParkingSpot spot);
}

public class SpotLogger implements ParkingLotObserver {
    public void onSpotAllocated(ParkingSpot spot) {
        System.out.println("Spot allocated: " + spot.getType());
    }

    public void onSpotReleased(ParkingSpot spot) {
        System.out.println("Spot released: " + spot.getType());
    }
}

----------------------------------------------------
// 使用 Singleton Pattern 保证 ParkingLot 是唯一的
----------------------------------------------------
public class ParkingLotSingleton {
    private static ParkingLotSingleton instance;
    private final ParkingLot lot;

    private ParkingLotSingleton() {
        this.lot = new ParkingLot(new NearestSpotStrategy());
    }

    public static synchronized ParkingLotSingleton getInstance() {
        if (instance == null) {
            instance = new ParkingLotSingleton();
        }
        return instance;
    }

    public ParkingLot getParkingLot() {
        return lot;
    }
}

----------------------------------------------------
// 使用 Decorator Pattern 添加打印/日志功能到支付
----------------------------------------------------
public class PaymentLoggerDecorator implements PaymentStrategy {
    private final PaymentStrategy wrapped;

    public PaymentLoggerDecorator(PaymentStrategy strategy) {
        this.wrapped = strategy;
    }

    @Override
    public Receipt pay(double amount) {
        System.out.println("Logging payment of $" + amount);
        return wrapped.pay(amount);
    }
}

PaymentStrategy original = new CreditCardPayment();
PaymentStrategy loggingPayment = new PaymentLoggerDecorator(original);



// 总结

// | 模式                | 应用点                 | 优势          |
// | ----------------- | ------------------- | ----------- |
// | Factory Pattern   | 车位/票据/车辆创建          | 解耦对象创建      |
// | Strategy Pattern  | 支付策略、车位分配策略         | 满足开闭原则      |
// | Observer Pattern  | 空位变动 → UI 通知/日志/报警器 | 实现事件驱动架构    |
// | Singleton Pattern | 全局唯一停车场/配置中心        | 保证状态一致      |
// | Decorator Pattern | 支付增强（日志/打印/审计）      | 动态扩展行为不破坏原类 |
