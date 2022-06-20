package hr.unipu.tfpu.rp.projekt;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;


public class LoginWindow extends JFrame {
    private JPanel panel1;
    private JTextField username;
    private JPasswordField password;
    private JButton login;
    private JButton newUser;

    private static String url = "jdbc:postgresql://localhost:5432/projekt";
    private static String user = "postgres";
    private static String pass = "postgres";

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return String.valueOf(password.getPassword());
    }

    public LoginWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setTitle("Prijava");
        add(panel1);
        validate();

        panel1.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (checkInfo(getUsername(), getPassword())) { // open main window
                    dispose();
                    Postcards pc = new Postcards();
                    pc.initComponents();
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void wrongInfo(Statement statement, String username, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM users WHERE username = '" + username + "'");
        if (!resultSet.next()) {
            UserNotFound dialog = new UserNotFound();
            dialog.pack();
            dialog.setVisible(true);
        }
        else {
            WrongPass dialog = new WrongPass();
            dialog.pack();
            dialog.setVisible(true);
        };
    }

    public static boolean checkInfo(String username, String password) {
        try (Connection connection = DriverManager.getConnection(
                url, user, pass)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT id " +
                            "FROM users WHERE username = '" + username + "'" +
                                "AND password = crypt('" + password +"', password);");

            if (!resultSet.next()) {
                wrongInfo(statement, username, password);
                return false;
            } else return true;

        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
        return false;
    }

    public static void insertImages() {
        Images img = new Images();
        ArrayList<Images> images = img.images;
        String insertIntoQuery = "INSERT INTO postcards(name, image, isFront) VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, user, pass); PreparedStatement pst = connection.prepareStatement(insertIntoQuery)) {
            for (Images i : images) {
                try (FileInputStream fin = new FileInputStream(i.getImage())) {
                    pst.setString(1,i.getFilename());
                    pst.setBinaryStream(2, fin, (int) i.getImage().length());
                    pst.setBoolean(3, i.isFront());
                    pst.executeUpdate();
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main (String[]args){
        LoginWindow lw = new LoginWindow();
        lw.setVisible(true);

    //insertImages();

        lw.login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkInfo(lw.getUsername(), lw.getPassword())) { // open main window
                    lw.dispose();
                    Postcards pc = new Postcards();
                    pc.initComponents();
                }
            }
        });

        lw.newUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewUser nu = new NewUser();
                lw.setVisible(false);
                nu.setVisible(true);
                nu.createUser.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (nu.insertNewUser()) {
                            nu.dispose();
                            lw.setVisible(true);
                        }
                        ;
                    }
                });
            }
        });


    }

}





