package code.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static code.jdbc.Common.*;

public class CheckPayment extends JFrame implements ActionListener {
    private JLabel roomCountLabel;
    private JList<String> roomList;
    private JTextField roomNumberField;
    private JTextField roomTypeField;
    private JTextField roomStatusField;
    private JTextField paymentAmountField;
    private JTextField paymentStatusField;
    private JButton addButton;
    private JButton checkoutButton;
    private JButton paymentButton;

    static Connection connection;
    static ResultSet rs;
    static PreparedStatement ps;

    public CheckPayment() {
        linkDB();
        setTitle("查询房间号");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        roomCountLabel = new JLabel("房间数量：");
        roomList = new JList<>();
        roomNumberField = new JTextField(10);
        roomTypeField = new JTextField(10);
        roomStatusField = new JTextField(10);
        paymentAmountField = new JTextField(10);
        paymentStatusField = new JTextField(10);
        addButton = new JButton("添加房间");
        checkoutButton = new JButton("退房");
        paymentButton = new JButton("付款");

        JPanel jp1 = new JPanel();
        jp1.add(roomCountLabel);

        JScrollPane jsp = new JScrollPane(roomList);
        JPanel jp2 = new JPanel();
        jp2.add(new JLabel("房间号:"));
        jp2.add(roomNumberField);
        jp2.add(new JLabel("房间类型:"));
        jp2.add(roomTypeField);
        jp2.add(new JLabel("状态:"));
        jp2.add(roomStatusField);
        jp2.add(new JLabel("付款金额:"));
        jp2.add(paymentAmountField);
        jp2.add(new JLabel("付款状态:"));
        jp2.add(paymentStatusField);
        jp2.add(addButton);
        jp2.add(checkoutButton);
        jp2.add(paymentButton);

        add(jp1, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);

        setVisible(true);

        // 获取数据库中的房间列表
        ArrayList<String> rooms = getRoomList();
        updateJList(rooms);

        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击事件
                    String selectedRoom = roomList.getSelectedValue();
                    if (selectedRoom != null) {
                        roomDetails(selectedRoom);
                    }
                }
            }
        });
        addButton.addActionListener(this);
        checkoutButton.addActionListener(this);
        paymentButton.addActionListener(this);
    }

    private static void linkDB() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查询数据库中的房间号并返回一个字符串列表
    private ArrayList<String> getRoomList() {
        ArrayList<String> roomNumbers = new ArrayList<>();

        try {
            ps = connection.prepareStatement("SELECT room_number FROM room");
            rs = ps.executeQuery();

            // 遍历结果集并将房间号添加到列表中
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                roomNumbers.add(roomNumber);
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
        roomCountLabel.setText("房间数量：" + rooms.size());
    }

    // 显示房间详细信息
    private void roomDetails(String roomNumber) {
        JOptionPane.showMessageDialog(this, roomNumber + " 是大床房,有空房间" ,"房间详情", JOptionPane.INFORMATION_MESSAGE);
    }

    // 插入房间数据到数据库
    private void insertRoom(String roomNumber, String roomType, String status) {
        try {
            ps = connection.prepareStatement("INSERT INTO room (room_number, room_type, room_status) VALUES (?, ?, ?)");
            ps.setString(1, roomNumber);
            ps.setString(2, roomType);
            ps.setString(3, status);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "房间添加成功", "提示信息", JOptionPane.INFORMATION_MESSAGE);

            // 更新房间列表
            ArrayList<String> rooms = getRoomList();
            updateJList(rooms);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "房间添加失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 退房并更新数据库中的房间状态
    private void checkoutRoom(String roomNumber) {
        try {
            ps = connection.prepareStatement("UPDATE room SET room_status = 'checked out' WHERE room_number = ?");
            ps.setString(1, roomNumber);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "房间已退房", "提示信息", JOptionPane.INFORMATION_MESSAGE);

            // 更新房间列表
            ArrayList<String> rooms = getRoomList();
            updateJList(rooms);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "退房失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 检查并记录付款信息到数据库
    private void checkPayment(String roomNumber, String amount, String status) {
        try {
            ps = connection.prepareStatement("INSERT INTO payment (room_number, amount, status) VALUES (?, ?, ?)");
            ps.setString(1, roomNumber);
            ps.setString(2, amount);
            ps.setString(3, status);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "付款成功", "提示信息", JOptionPane.INFORMATION_MESSAGE);

            // 更新房间列表
            ArrayList<String> rooms = getRoomList();
            updateJList(rooms);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "付款失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QueryRooms::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String roomNumber = roomNumberField.getText();
            String roomType = roomTypeField.getText();
            String status = roomStatusField.getText();
            insertRoom(roomNumber, roomType, status);
        } else if (e.getSource() == checkoutButton) {
            String roomNumber = roomNumberField.getText();
            checkoutRoom(roomNumber);
        } else if (e.getSource() == paymentButton) {
            String roomNumber = roomNumberField.getText();
            String amount = paymentAmountField.getText();
            String status = paymentStatusField.getText();
            checkPayment(roomNumber, amount, status);
        }
    }
}
