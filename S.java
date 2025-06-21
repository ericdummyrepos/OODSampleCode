public class Employee {
    private String name;
    private int hoursWorked;
    private double hourlyRate;

    public Employee(String name, int hoursWorked, double hourlyRate) {
        this.name = name;
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    public String getName() {
        return name;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}

public class SalaryCalculator {
    public double calculateSalary(Employee employee) {
        return employee.getHoursWorked() * employee.getHourlyRate();
    }
}

public class ReportPrinter {
    public void printReport(Employee employee, double salary) {
        System.out.println("Employee: " + employee.getName());
        System.out.println("Total Salary: $" + salary);
    }
}

public class EmployeeRepository {
    public void save(Employee employee) {
        System.out.println("Saving employee " + employee.getName() + " to database...");
    }
}

// Main example
public class Main {
    public static void main(String[] args) {
        Employee emp = new Employee("Alice", 40, 25.0);

        SalaryCalculator calculator = new SalaryCalculator();
        double salary = calculator.calculateSalary(emp);

        ReportPrinter printer = new ReportPrinter();
        printer.printReport(emp, salary);

        EmployeeRepository repo = new EmployeeRepository();
        repo.save(emp);
    }
}
