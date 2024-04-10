package lammerbutnoob.reddit;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;


public class Account {
    public static int currentUserID = 1;

    private static boolean authenticate(String input, byte[] hashedPassword) throws NoSuchAlgorithmException {
        byte[] hashedInput = hashPassword(input);
        return Arrays.equals(hashedInput, hashedPassword);
    }

    public static byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] convertString2Array(String arrayString) {
        arrayString = arrayString.substring(1, arrayString.length() - 1); // Remove the brackets
        String[] splitArray = arrayString.split(", "); // Split the string into an array of strings
        byte[] byteArray = new byte[splitArray.length]; // Create an array of bytes
        for (int i = 0; i < splitArray.length; i++) {
            byteArray[i] = Byte.parseByte(splitArray[i]); // Convert each string to a byte
        }
        return byteArray;
    }

    //Returns 1 for correct user, -1 for invalid password, -2 for Invalid credentials, -3 Invalid Email, -4
//    Authentication exception
    public static int login(String email, String password) {
        try {
            //Connection
            Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
            //Preparing statement
            PreparedStatement stmt = con.prepareStatement("SELECT * from users where email = ?");
            stmt.setString(1, email);
            if (stmt.execute()) {
                ResultSet rs = stmt.getResultSet();
                //Validating the password
                if (authenticate(password, convertString2Array(rs.getString("password")))) {
                    //Set the current user
                    currentUserID = rs.getInt("id");
                    //Correct user
                    con.close();
                    return 1;
                } else {
                    //Wrong password
                    con.close();
                    return -1;
                }
            } else {
                //Invalid credentials
                con.close();
                return -2;
            }
        } //if SQL throws an exception
        catch (SQLException e) {
            return -3;
        }
        //If authentication throws an exception
        catch (NoSuchAlgorithmException e) {
            return -4;
        }
    }

    //Returns 1 for OK, -1 for already has an account
    public static int signUp(String username, String email, String password, String gender) {
        try {
            password = Arrays.toString(Account.hashPassword(password));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            //If dedicated JDBC
            Class.forName("org.sqlite.JDBC");
            //Connection
            Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
            //Prepared statement
            PreparedStatement preparedStatement = con.prepareStatement("INSERT into users(username,email,password,isMale) values (?,?,?,?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setInt(4, (Objects.equals(gender, "Female")) ? 0 : 1);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            con.close();
            return 1;
            // Check constraint (:
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed"))
                return -1;
            else throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Failed!
        return 0;
    }

    public static String getName() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT username from users where id =" + currentUserID);
        String name = rs.getString("username");
        con.close();
        return name;
    }

    public static String getName(int id) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT username from users where id =" + id);
        String name = rs.getString("username");
        con.close();
        return name;
    }


}
