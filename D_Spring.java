@Component
class FileLogger implements Logger {
    public void log(String msg) { System.out.println(msg); }
}

@Service
class UserService {
    private final Logger logger;

    @Autowired
    public UserService(Logger logger) {
        this.logger = logger;
    }
}
