package org.planetscale.app;

import com.google.gson.Gson;
import spark.Spark;

import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

public class SparkApp {
    public static void main(String[] args) {
        Spark.port(8080);

        get("/hello", (req, res) -> "Hello World");
        get("/users", (req, res) -> {

            res.type("application/json");
            List<User> users = Arrays.asList(
                    new User("100", "Todd", "Smith", "todd@hello.com"),
                    new User("101", "scott", "Zoo", "scott@hello.com")
            );

            return new Gson().toJson(users);

        });

    }

    static class User {
        public final String id;
        public final String firstName;
        public final String lastName;

        public final String email;

        User(String id, String firstName, String lastName, String email) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
    }
}
