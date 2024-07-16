package code.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static code.jdbc.Common.*;

public class ClientLogin extends JFrame implements ActionListener {
    JPanel jp1, jp2;
    JButton queryButton, BookRoomButton, CheckoutRoomButton,quitButton;
    static Connection connection;
    static ResultSet rs;
    static PreparedStatement ps;

    public ClientLogin() {
        linkDB();
        jp1 = new JPanel();
        jp2 = new JPanel();
        queryButton = new JButton("查询房间");
        BookRoomButton = new JButton("入住");
        CheckoutRoomButton = new JButton("退订");
        quitButton = new JButton("退出");

        queryButton.addActionListener(this);
        BookRoomButton.addActionListener(this);
        CheckoutRoomButton.addActionListener(this);
        quitButton.addActionListener(this);

        jp2.add(queryButton);
        jp2.add(CheckoutRoomButton);
        jp2.add(BookRoomButton);
        jp2.add(quitButton);

        add(jp1);
        add(jp2);

        setLayout(new GridLayout(2, 1));
        setTitle("后台管理信息");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private static void linkDB() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("退出")) {
            System.exit(0);

        } else if (e.getActionCommand().equals("查询房间")) {
            new QueryRooms();
        } else if (e.getActionCommand().equals("入住")) {
            new BookRoom();
        } else if (e.getActionCommand().equals("退订")){
            new CheckoutRoom();
        }
    }

    public static void main(String[] args) {
        new ClientLogin();
    }
}
