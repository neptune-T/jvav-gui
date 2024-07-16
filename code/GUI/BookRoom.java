package code.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static code.jdbc.Common.*;

public class BookRoom extends JFrame implements ActionListener {
    private JLabel roomCountLabel;
    private JList<String> roomList;
    private JTextField roomNumberField;
    private JButton bookButton, exitButton;

    static Connection connection;
    static ResultSet rs;
    static PreparedStatement ps;

    public BookRoom() {
        linkDB();
        setTitle("订房");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        roomCountLabel = new JLabel("可用房间数量：");
        roomList = new JList<>();
        roomNumberField = new JTextField(10);
        bookButton = new JButton("订房");
        exitButton = new JButton("返回");

        JPanel jp1 = new JPanel();
        jp1.add(roomCountLabel);

        JScrollPane jsp = new JScrollPane(roomList);
        JPanel jp2 = new JPanel();
        jp2.add(new JLabel("房间号:"));
        jp2.add(roomNumberField);
        jp2.add(bookButton);
        jp2.add(exitButton);

        add(jp1, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);

        setVisible(true);

        // 获取数据库中状态为"有"的房间列表
        ArrayList<String> rooms = getAvailableRooms();
        updateJList(rooms);

        bookButton.addActionListener(this);
        exitButton.addActionListener(this);
    }

    private static void linkDB() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查询数据库中状态为"有"的房间并返回一个字符串列表
    private ArrayList<String> getAvailableRooms() {
        ArrayList<String> roomNumbers = new ArrayList<>();

        try {
            ps = connection.prepareStatement("SELECT room_number, room_type, room_status FROM room WHERE room_status = '有'");
            rs = ps.executeQuery();

            // 遍历结果集并将房间信息添加到列表中
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                String roomStatus = rs.getString("room_status");
                roomNumbers.add("房间号: " + roomNumber + ", 类型: " + roomType + ", 状态: " + roomStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roomNumbers;
    }

    // 更新GUI中的房间列表
    private void updateJList(ArrayList<String> rooms) {
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (String room : rooms) {
            listModel.addElement(room);
        }

        roomList.setModel(listModel);
        roomCountLabel.setText("可用房间数量：" + rooms.size());
    }

    // 更新数据库中的房间状态
    private void bookRoom(String roomNumber) {
        try {
            ps = connection.prepareStatement("UPDATE room SET room_status = '无' WHERE room_number = ?");
            ps.setString(1, roomNumber);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "订房成功", "提示信息", JOptionPane.INFORMATION_MESSAGE);

                // 更新房间列表
                ArrayList<String> rooms = getAvailableRooms();
                updateJList(rooms);
            } else {
                JOptionPane.showMessageDialog(this, "订房失败，房间号不存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "订房失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bookButton) {
            String roomNumber = roomNumberField.getText();
            bookRoom(roomNumber);
        } else if (e.getSource() == exitButton) {
            dispose();
            new ClientLogin(); // 确保 ClientLogin 类存在并正确实现
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookRoom::new);
    }
}
