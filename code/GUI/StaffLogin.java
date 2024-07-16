package code.GUI;

import static code.jdbc.Common.*;
import static code.utils.MD5.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StaffLogin extends JFrame implements ActionListener {
    JPanel jp1, jp2, jp3, jp4, jp5;
    JLabel companyIdLabel, userNameLabel, pwdLabel;
    JButton loginButton, registerButton, resetButton, quitButton;
    JTextField companyIdTextField, userNameTextField;
    JPasswordField pwdField;

    static Connection connection;

    static ResultSet rs;
    static PreparedStatement ps;

    String userName;
    String pwd;


    public StaffLogin() {
        linkDB();

        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        jp4 = new JPanel();
        jp5 = new JPanel();

        companyIdLabel = new JLabel("员工id");
        userNameLabel = new JLabel(" 用 户 名 ");
        pwdLabel = new JLabel("  密   码   ");

        companyIdTextField = new JTextField(10);
        userNameTextField = new JTextField(10);

        pwdField = new JPasswordField(10);

        loginButton = new JButton("登录");
        registerButton = new JButton("注册");
        resetButton = new JButton("重置");
        quitButton = new JButton("退出");

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
        resetButton.addActionListener(this);
        quitButton.addActionListener(this);

        jp1.add(companyIdLabel);
        jp1.add(companyIdTextField);

        jp2.add(userNameLabel);
        jp2.add(userNameTextField);

        jp3.add(pwdLabel);
        jp3.add(pwdField);

        jp4.add(loginButton);
        jp4.add(registerButton);

        jp5.add(resetButton);
        jp5.add(quitButton);

        add(jp1);
        add(jp2);
        add(jp3);
        add(jp4);
        add(jp5);

        setLayout(new GridLayout(6, 1));
        setTitle("员工客户端查询任务中心");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void clear() {
        companyIdTextField.setText("");
        userNameTextField.setText("");
        pwdField.setText("");
    }

    public void linkDB() {
        try {
            Class.forName(driverName);

            connection = DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("重置")) {
            clear();
        } else if (e.getActionCommand().equals("退出")) {
            System.exit(0);
        } else if (e.getActionCommand().equals("登录")) {
            checkEmployee();
        } else {
            dispose();
            //new RegisterEmployee();
        }
    }

    private void checkEmployee() {
        try {
            if (companyIdTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入员工代号！", "提示消息", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ps = connection.prepareStatement("select company_id from companies where company_id = ?");
            ps.setInt(1, Integer.parseInt(companyIdTextField.getText()));
            rs = ps.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "没有该员工！", "提示消息", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ps = connection.prepareStatement("select employee_name, employee_pwd from employees" +
                    " where company_id = ? and employee_name = ?");
            ps.setInt(1, Integer.parseInt(companyIdTextField.getText()));
            ps.setString(2, userNameTextField.getText());

            rs = ps.executeQuery();
            if (rs.next()) {
                userName = rs.getString(1);
                pwd = rs.getString(2);
                login();
            } else {
                JOptionPane.showMessageDialog(null, "没有此用户或用户名为空！\n请重新输入", "提示消息", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // pwd.equals(String.valueOf(pwdField.getPassword()
    public void login() {
        if(userName.equals(userNameTextField.getText()) && decrypt(String.valueOf(pwdField.getPassword()), pwd)) {
            JOptionPane.showMessageDialog(null, "登录成功", "提示信息", JOptionPane.WARNING_MESSAGE);
            clear();
            dispose();
            new EmployeeTasks(userName, 0);
        } else if (String.valueOf(pwdField.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入密码！", "提示信息", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "用户名或密码错误！\n请重新输入", "提示信息", JOptionPane.ERROR_MESSAGE);
            clear();
        }
    }

    public static void main(String[] args) {
        new LoginEmployee();
    }
}