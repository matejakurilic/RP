package hr.unipu.tfpu.rp.projekt;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

public class Postcards {
    private static String url = "jdbc:postgresql://localhost:5432/projekt";
    private static String user = "postgres";
    private static String pass = "postgres";
    JList<String> list1;

    public void showImage(final Image front, final Image back, String name) {
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(front.getWidth(null) + back.getWidth(null), front.getHeight(null) + 50);
        frame.add(new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(front, 0, 0, null);
                g.drawImage(back, front.getWidth(null), 0,null);
            }
        });
        frame.setVisible(true);
    }

    public void listValueSelected (ListSelectionEvent e) {
        String name = list1.getSelectedValue();
        String getImages = "SELECT image, isFront FROM postcards WHERE name = '" + name + "';";
        Image front = null, back = null;
        try (Connection connection = DriverManager.getConnection(
                url, user, pass)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getImages);
            while (resultSet.next()) {
                InputStream is = resultSet.getBinaryStream(1);
                if (resultSet.getBoolean("isFront")) {
                    front = ImageIO.read(is);
                } else {
                    back = ImageIO.read(is);
                }
            }
        } catch (SQLException | IOException exc) {
            exc.printStackTrace();
        }
        showImage(front, back, name);


    }

    public void initComponents() {
        JFrame f = new JFrame("Popis razglednica");
        JPanel panel1 = new JPanel(new BorderLayout());


        ArrayList<String> arr = new ArrayList<String>();
        String query = "SELECT DISTINCT name FROM postcards ORDER BY name ASC;";

        try (Connection connection = DriverManager.getConnection(
                url, user, pass)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                arr.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        list1 = new JList<String>(arr.toArray(new String[arr.size()]));
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                listValueSelected(e);
            }
        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list1);
        list1.setLayoutOrientation(JList.VERTICAL);
        panel1.add(scrollPane);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.add(panel1);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

}
