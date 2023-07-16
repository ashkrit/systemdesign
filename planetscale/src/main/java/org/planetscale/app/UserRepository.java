package org.planetscale.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    record User(String id, String firstName, String lastName, String email) {
    }

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public UserRepository() {
        register(new User("1000", "Todd", "Smith", "todd@hello.com"));
        register(new User("1001", "scott", "Zoo", "scott@hello.com"));
    }

    private void register(User u) {
        users.put(u.id(), u);
    }

    public List<User> users() {
        return new ArrayList<>(users.values());
    }

}
