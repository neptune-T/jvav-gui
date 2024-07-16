package code.GUI;

import code.utils.PhoneRegex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static code.jdbc.Common.*;
import static code.jdbc.Common.password;

public class MessageEmployee extends JFrame implements ActionListener {
    JPanel jp1, jp2, jp3, jp4, jp5;
    JLabel userNameLabel, userName, phoneLabel, phoneL, depLabel, phoneLabel1, employeeNumLabel;
    JButton editButton, backButton;
    JTextField depTextField, phoneTextField, employeeNumTextField;

    static Connection connection;
    static ResultSet rs;
    static PreparedStatement ps;
    String phone;
    String department;
    String employee_num;
    String name;
    int company_id;
    int identity;

    public MessageEmployee(String name, int company_id, int identity) {
        this.name = name;
        this.company_id = company_id;
        this.identity = identity;

        try {
            Class.forName(driverName);

            connection = DriverManager.getConnection(url, username, password);

            if (connection == null) {
                System.out.println("连接失败");
            } else {
                System.out.println("连接成功");
            }

            ps = connection.prepareStatement("select employee_phone, department, employee_num from employees" +
                    " where employee_name = ? and company_id = ?");
            ps.setString(1, name);
            ps.setString(2, String.valueOf(company_id));

            rs = ps.executeQuery();

            if (rs.next()) {
                phone = rs.getString(1);
                department = rs.getString(2);
                employee_num = rs.getString(3);
            }

            if (phone == null) {
                phone = "暂无";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        jp4 = new JPanel();
        jp5 = new JPanel();

        userNameLabel = new JLabel("用户名:");
        userName = new JLabel(name);
        phoneLabel = new JLabel("  联系电话：");
        phoneL = new JLabel(phone);
        depLabel = new JLabel("  部   门   ");
        phoneLabel1 = new JLabel("联系电话");
        employeeNumLabel = new JLabel("  工   号   ");

        // 设置字体、加粗和大小
        Font font = new Font("SansSerif", Font.BOLD, 24);
        userNameLabel.setFont(font);
        userName.setFont(font);
        phoneLabel.setFont(font);
        phoneL.setFont(font);


        depTextField = new JTextField(10);
        phoneTextField = new JTextField(10);
        employeeNumTextField = new JTextField(10);

        if (identity == 1) {
            depTextField.setEditable(false);
            phoneTextField.setEditable(false);
            employeeNumTextField.setEditable(false);
        }

        depTextField.setText(department);
        phoneTextField.setText(phone.equals("暂无") ? null : phone);
        employeeNumTextField.setText(employee_num);

        editButton = new JButton("修改");
        backButton = new JButton("返回");

        if (identity == 1) {
            editButton.setEnabled(false);
        }

        editButton.addActionListener(this);
        backButton.addActionListener(this);

        jp1.add(userNameLabel);
        jp1.add(userName);
        jp1.add(phoneLabel);
        jp1.add(phoneL);

        jp2.add(depLabel);
        jp2.add(depTextField);

        jp3.add(phoneLabel1);
        jp3.add(phoneTextField);

        jp4.add(employeeNumLabel);
        jp4.add(employeeNumTextField);

        jp5.add(editButton);
        jp5.add(backButton);

        add(jp1);
        add(jp2);
        add(jp3);
        add(jp4);
        add(jp5);

        setLayout(new GridLayout(6, 1));
        if (identity == 0) {
            setTitle("项目进度查询管理系统-员工信息修改");
        } else {
            setTitle("项目进度查询管理系统-员工信息查看");
        }
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("修改")) {
            try {
                department = depTextField.getText();
                phone = phoneTextField.getText();
                employee_num = employeeNumTextField.getText();

                if (!PhoneRegex.check(phone)) {
                    JOptionPane.showMessageDialog(null, "请填写正确的电话号码！", "提示消息", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ps = connection.prepareStatement("update employees set department = ?, employee_phone = ?, employee_num = ?" +
                        "where company_id = ? and employee_name = ?");
                ps.setString(1, department);
                ps.setString(2, phone);
                ps.setString(3, employee_num);
                ps.setInt(4, company_id);
                ps.setString(5, name);

                ps.executeUpdate();

                // 回显联系电话
                phoneL.setText(phone);

                JOptionPane.showMessageDialog(null, "修改成功！", "提示消息", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            dispose();
            new EmployeeTasks(name, identity);
        }
    }

    public static void main(String[] args) {
        new MessageEmployee("chen", 1002, 0);
    }
}
