package hr.unipu.tfpu.rp.projekt;

import javax.swing.*;
import java.sql.*;
import java.util.Arrays;

public class NewUser extends JFrame {

    private JPanel panel1;
    private JTextField username;
    private JPasswordField pass1;
    private JPasswordField pass2;
    JButton createUser;

    private static String url = "jdbc:postgresql://localhost:5432/projekt";
    private static String user = "postgres";
    private static String pass = "postgres";

    public String getUsername() {
        return username.getText();
    }



    public boolean checkPass() {
        return (Arrays.equals(pass1.getPassword(), pass2.getPassword())) ;
    }

    public boolean insertNewUser() {
        String username = getUsername();
        String password;
        String insertIntoQuery;
        if (checkPass()) {
            password = String.valueOf(pass1.getPassword());
            insertIntoQuery = "INSERT INTO users (username, password) VALUES ('"+ username +"'" +
                    ", crypt('" + password + "'::TEXT, gen_salt('bf')))";
        } else {
            PasswordNotMatching dialog = new PasswordNotMatching();
            dialog.pack();
            dialog.setVisible(true);
            return false;
        } try (Connection connection = DriverManager.getConnection(url, user, pass); PreparedStatement pst = connection.prepareStatement(insertIntoQuery)) {
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public NewUser () {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 300);
        setLocationRelativeTo(null);
        setTitle("Kreiranje novog korisnika.");
        add(panel1);
        validate();
    }
}
