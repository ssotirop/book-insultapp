package org.openshift;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InsultGenerator {
    public static String generateInsult() {
        String vowels = "AEIOU";
        String article = "an";
        String theInsult = "";
        String port = ":5432";
        String driverJDBC = "org.postgresql.Driver";
        Connection connection = null;

        String username = System.getenv("POSTGRESQL_USER");
        String password = System.getenv("PGPASSWORD");
        String databaseURL = "jdbc:postgresql://";
        databaseURL += System.getenv("POSTGRESQL_SERVICE_HOST");
        databaseURL += port;
        databaseURL += "/" + System.getenv("POSTGRESQL_DATABASE");

        try {
            Class.forName(driverJDBC);
            connection = DriverManager.getConnection(databaseURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.getStackTrace();
        }

        try {
            if (connection != null) {
                String SQL = "select a.string AS first, b.string AS second, c.string AS noun from short_adjective a , long_adjective b, noun c ORDER BY random() limit 1;";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(SQL);
                while (rs.next()) {
                    if (vowels.indexOf(rs.getString("first").charAt(0)) == -1) {
                        article = "a";
                    }
                    theInsult = String.format("Thou art %s %s %s %s!", article,
                    rs.getString("first"), rs.getString("second"), rs.getString("noun"));
                }
                rs.close();
                connection.close();
            } else {
                return "Database connection problem!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return theInsult;
    }
