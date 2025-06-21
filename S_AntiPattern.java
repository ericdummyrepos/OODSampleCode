public class GodClass {
    // business field/data
    private String employeeName;
    private int hoursWorked;
    private double hourlyRate;

    // UI
    private String buttonColor;
    private String layoutStyle;

    // network access
    private String apiEndpoint;
    private String authToken;

    // logging
    private StringBuilder log = new StringBuilder();

    // database connection
    private Connection dbConnection;

    public GodClass(String name, int hours, double rate) {
        this.employeeName = name;
        this.hoursWorked = hours;
        this.hourlyRate = rate;
        this.apiEndpoint = "https://api.company.com";
        this.authToken = "default-token";
    }

    // business logic
    public double calculatePay() {
        log.append("Calculating pay...\n");
        return hoursWorked * hourlyRate;
    }

    // UI
    public void renderUI() {
        log.append("Rendering UI with style " + layoutStyle + "\n");
        System.out.println("Rendering button with color: " + buttonColor);
    }

    // communication - network
    public void sendToServer() {
        log.append("Sending data to server: " + apiEndpoint + "\n");
        System.out.println("Sending employee data to " + apiEndpoint);
    }

    // database ops
    public void saveToDatabase() {
        log.append("Saving to database...\n");
    }

    // logging
    public void printLog() {
        System.out.println(log.toString());
    }
}
