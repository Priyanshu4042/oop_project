import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static UserManager instance;
    private final Map<String, User> users;

    private UserManager() {
        users = new HashMap<>();
        addTestUsers();
    }

    private void addTestUsers() {
        users.put("admin", new User("admin", "admin123"));
        users.put("test", new User("test", "test123"));
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public boolean authenticate(String username, String password) {
        try {
            User user = users.get(username);
            return user != null && user.getPassword().equals(password);
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(String username, String password) throws IllegalArgumentException {

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        if (users.containsKey(username)) {
            return false;
        }

        users.put(username, new User(username, password));
        return true;
    }
}