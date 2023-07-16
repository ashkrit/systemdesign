package org.planetscale.app.service;

import java.util.List;

public class UserService {

    public final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> users() {
        return this.repository.users();
    }

    public void register(User u) {
        this.repository.register(u);
    }
}
