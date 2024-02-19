package com.vinola.valenciact.storage;

import com.vinola.valenciact.ValenciaCT;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://" + ValenciaCT.getInstance().getConfig().getString("ip") + "/" + ValenciaCT.getInstance().getConfig().getString("db") + "?useSSL=false";
    private static final String USER = ValenciaCT.getInstance().getConfig().getString("user");
    private static final String PASSWORD = ValenciaCT.getInstance().getConfig().getString("pass");

    private static Database instance = null;

    private Database() {
        checkTables();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void checkTables() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connect();
            statement = connection.createStatement();

            ResultSet playersTable = connection.getMetaData().getTables(null, null, "Players", null);
            if (!playersTable.next()) {
                statement.execute("CREATE TABLE Players (uuid VARCHAR(50) PRIMARY KEY)");
            }

            ResultSet structuresTable = connection.getMetaData().getTables(null, null, "Structures", null);
            if (!structuresTable.next()) {
                statement.execute("CREATE TABLE Structures (" +
                        "id VARCHAR(50) PRIMARY KEY, " +
                        "owner VARCHAR(50), " +
                        "status BOOLEAN, " +
                        "hologram_id VARCHAR(50), " +
                        "worldName VARCHAR(50), " +
                        "yaw FLOAT, " +
                        "xi INT, " +
                        "yi INT, " +
                        "zi INT, " +
                        "goldBlockX INT, " +
                        "goldBlockY INT, " +
                        "goldBlockZ INT, " +
                        "FOREIGN KEY (owner) REFERENCES Players(uuid))");
            }

            ResultSet blocksTable = connection.getMetaData().getTables(null, null, "Blocks", null);
            if (!blocksTable.next()) {
                statement.execute("CREATE TABLE Blocks (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "structure_id VARCHAR(50), " +
                        "x INT, " +
                        "y INT, " +
                        "z INT, " +
                        "FOREIGN KEY (structure_id) REFERENCES Structures(id))");
            }

            ResultSet cristalTable = connection.getMetaData().getTables(null, null, "CristalUses", null);
            if (!cristalTable.next()) {
                statement.execute("CREATE TABLE CristalUses (" +
                        "uuid VARCHAR(50) PRIMARY KEY, " +
                        "uses INT)");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}