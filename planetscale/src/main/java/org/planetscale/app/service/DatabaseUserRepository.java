package org.planetscale.app.service;

import com.google.gson.Gson;
import org.planetscale.app.UserRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseUserRepository implements UserRepository {

    private final ConnectionHandler connectionHandler;

    public DatabaseUserRepository(Map<String, String> config) {
        this.connectionHandler = new ConnectionHandler(config);
        registerTables();

        register(new User(UUID.randomUUID().toString(), "Todd", "Smith", "todd@hello.com"));
        register(new User(UUID.randomUUID().toString(), "scott", "Zoo", "scott@hello.com"));

    }

    private void registerTables() {
        try (Connection connection = connectionHandler.openConnection()) {

            String table = String.format("CREATE TABLE %s (ID VARCHAR, CONTENT VARCHAR)", "users");

            System.out.println(table);

            connection.createStatement().execute(table);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void register(User u) {

        String value = new Gson().toJson(u);
        String key = u.id();

        try (Connection connection = connectionHandler.openConnection()) {

            String insert = """
                    INSERT INTO  %s (ID ,CONTENT)
                    VALUES (?,?)
                    """.formatted("users");

            PreparedStatement statement = connection.prepareStatement(insert);
            int index = 1;
            statement.setString(index++, key);
            statement.setString(index++, value);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<User> users() {
        String sql = String.format("SELECT ID, CONTENT FROM %s", "users");

        try (Connection connection = connectionHandler.openConnection();
             ResultSet rs = connection.createStatement().executeQuery(sql)) {
            List<User> users = new ArrayList<>();

            Gson g = new Gson();
            while (rs.next()) {
                String value = rs.getString("CONTENT");
                UserRequest user = g.fromJson(value, UserRequest.class);
                users.add(user.as(user.id));
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
