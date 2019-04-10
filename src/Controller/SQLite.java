package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

public class SQLite {

    public int DEBUG_MODE = 0;
    String driverURL = "jdbc:sqlite:" + "database.db";
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    public String errorMessage;

    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL,\n"
                + " name TEXT NOT NULL,\n"
                + " stock INTEGER DEFAULT 0,\n"
                + " price DOUBLE DEFAULT 0, \n"
                + " timestamp TEXT NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " event TEXT NOT NULL,\n"
                + " username TEXT NOT NULL,\n"
                + " desc TEXT NOT NULL,\n"
                + " timestamp TEXT NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " name TEXT NOT NULL UNIQUE,\n"
                + " stock INTEGER DEFAULT 0,\n"
                + " price REAL DEFAULT 0.00\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL UNIQUE,\n"
                + " password TEXT NOT NULL,\n"
                + " role INTEGER DEFAULT 2,\n"
                + " locked INTEGER DEFAULT 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string

        }
    }

    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void dropUserTable() {
        String sql = "DROP TABLE IF EXISTS users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }

    }

    public void addHistory(String username, String name, int stock, double price, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,price,timestamp) VALUES('" + username + "','" + name + "','" + stock + "','" + price + "','" + timestamp + "')";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (Exception ex) {/* Log: Log exception */
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void addLogs(String event, String username, String desc, String timestamp) {
        String sql = "INSERT INTO logs(event,username,desc,timestamp) VALUES('" + event + "','" + username + "','" + desc + "','" + timestamp + "')";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (Exception ex) {/* Log: Log exception */
            ex.printStackTrace(pw);
            errorMessage = sw.toString();
        }
    }

    public void addProduct(String name, int stock, double price) {
        String sql = "INSERT INTO product(name,stock,price) VALUES('" + name + "','" + stock + "','" + price + "')";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string    
        }

    }

    public void removeProduct(String productname) {
        String sql = "DELETE from product WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productname);
            stmt.executeUpdate();
        } catch (Exception ex) {
            /* Log: Log exception */
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string  
        }
    }

    public boolean addUser(String username, String password) {
        String sql = "INSERT INTO users(username,password) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (Exception ex) {
            /* Log: Log exception */
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
            return false;
        }
    }

    public void updateRole(String username, int role) {
        String sql = "UPDATE users SET role = ? WHERE username = ?";
        int result = 0;
        System.out.println("test");

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, role);
            stmt.setString(2, username);
            stmt.executeUpdate();
            result = 1;
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string        
        }
        if (result == 1) {
            System.out.println("User: " + username + " changed role to ->" + role); // Debug
            // Log: User attempt counter set to N
        }
    }

    public void updateLocked(String username, int locked) {
        String sql = "UPDATE users SET locked = ? WHERE username = ?";
        int result = 0;
        System.out.println("test");

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, locked);
            stmt.setString(2, username);
            stmt.executeUpdate();
            result = 1;
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string        
        }
        if (result == 1) {
            System.out.println("User: " + username + " increased attemp counter" + locked); // Debug
            // Log: User attempt counter set to N
        }
    }

    public ArrayList<History> getHistory() {
        String sql = "SELECT id, username, name, stock, price, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getInt("price"),
                        rs.getString("timestamp")));
            }
        } catch (Exception ex) {
        }
        return histories;
    }

    public ArrayList<Logs> getLogs() {
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                        rs.getString("event"),
                        rs.getString("username"),
                        rs.getString("desc"),
                        rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
        return logs;
    }

    public ArrayList<Product> getProduct() {
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getFloat("price")));
            }
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
        return products;
    }

    public ArrayList<User> getUsers() {
        String sql = "SELECT id, username, password, role, locked FROM users";
        ArrayList<User> users = new ArrayList<User>();

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("role"),
                        rs.getInt("locked")));
            }
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string

        }
        return users;
    }

// Ito yung nasa MP1 final natin (Dalawa talaga yung addUser na class sa MP1)
//    public void addUser(String username, String password) {
//        String sql = "INSERT INTO users(username,password) VALUES('" + username + "','" + password + "')";
//        String log4jConfigFile = System.getProperty("user.dir")
//                + File.separator + "log4j.properties";
//        PropertyConfigurator.configure(log4jConfigFile);
//        try (Connection conn = DriverManager.getConnection(driverURL);
//                Statement stmt = conn.createStatement()) {
//            stmt.execute(sql);
//		//  For this activity, we would not be using prepared statements first.
//		//      String sql = "INSERT INTO users(username,password) VALUES(?,?)";
//		//      PreparedStatement pstmt = conn.prepareStatement(sql)) {
//		//      pstmt.setString(1, username);
//		//      pstmt.setString(2, password);
//		//      pstmt.executeUpdate();
//        } catch (Exception ex) {
//            LOGGER.info("Error in adding users, current user is : " + System.getProperty("user.name") + "|" + " IP ADDRESS IS: " + ipAddress);
//
//        }
//    }
    public boolean addUser(String username, String password, int role) {
        String sql = "INSERT INTO users(username,password,role) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, role);
            stmt.executeUpdate();
            return true;
        } catch (Exception ex) {
            /* Log: Log exception */
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
            return false;
        }
    }

    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (Exception ex) {
            /* Log: Log exception */
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void lockAccount(String username, int lock) {
        String sql = "UPDATE users SET locked = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lock);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public Product getProduct(String name) {
        String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        Product product = null;
        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            product = new Product(rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getFloat("price"));
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
        return product;
    }

    boolean checkExistingUsers(String username) {
        String sql = "SELECT username FROM ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            return true;
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string

        }
        return true;
    }

    public void updatePassword(String username, String password) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }

    }

    public void updateProduct(String productName, String newName, int stock, double price) {
        String sql = "UPDATE product SET name = ?, stock = ?, price = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, stock);
            stmt.setDouble(3, price);
            stmt.setString(4, productName);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }

    public void deleteLogsTable() {
        String sql = "DELETE FROM logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db truncated.");
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            errorMessage = sw.toString(); // stack trace as a string
        }
    }
//    public String searchUser(String username, String password) {
//        String sql = "SELECT username FROM users WHERE username='" + username + "');";
//        String sq2 = "SELECT password FROM users WHERE password='" + password + "');";
//        try (Connection conn = DriverManager.getConnection(driverURL);
//                Statement stmt = conn.createStatement();
//                ResultSet rs = stmt.executeQuery(sql)) {
//                    String user = rs.getString("username");
//                    return user;
//        } catch (Exception ex) {
//        }
//        return "";
//    }
}
