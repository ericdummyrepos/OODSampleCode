class FileLogger {
    public void log(String message) {
        System.out.println("Logging to file: " + message);
    }
}

class UserService {
    private FileLogger logger = new FileLogger();  // tightly coupled

    public void registerUser(String user) {
        logger.log("User registered: " + user);
    }
}
