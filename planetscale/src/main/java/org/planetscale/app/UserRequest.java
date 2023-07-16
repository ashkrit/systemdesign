package org.planetscale.app;

import org.planetscale.app.service.UserRepository.User;

public class UserRequest {
    public final String id;
    public final String firstName;
    public final String lastName;
    public final String email;

    public UserRequest(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User as(String idValue) {
        return new User(idValue, firstName, lastName, email);
    }
}
