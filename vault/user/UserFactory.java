package vault.user;

import vault.password.Password;

public class UserFactory {
    
    public static final User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(new Password(password));
        user.setSaver(new UserSaver(user));
        return user;
    }
}
