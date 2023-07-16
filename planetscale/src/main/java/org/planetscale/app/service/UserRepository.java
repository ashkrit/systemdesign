package org.planetscale.app.service;

import java.util.List;

public interface UserRepository {

    void register(User u);
    List<User> users();
}
