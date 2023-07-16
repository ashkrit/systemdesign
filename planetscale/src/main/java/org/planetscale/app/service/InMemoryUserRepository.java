package org.planetscale.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {


    private final Map<String, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {

        register(new User(UUID.randomUUID().toString(), "Todd", "Smith", "todd@hello.com"));
        register(new User(UUID.randomUUID().toString(), "scott", "Zoo", "scott@hello.com"));
    }

    public void register(User u) {
        users.put(u.id(), u);
    }

    public List<User> users() {
        return new ArrayList<>(users.values());
    }

}
