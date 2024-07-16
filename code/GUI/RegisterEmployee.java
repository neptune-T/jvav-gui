package code.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

import static code.jdbc.Common.*;
import static code.utils.MD5.encrypt;

public class RegisterEmployee extends JFrame implements ActionListener {
    JPanel jp1, jp2, jp3, jp4, jp5, jp6;
    JLabel companyIdLabel, userNameLabel, pwdLabel, checkPwdLabel, keyLabel;
    JButton registerButton, backButton;
    JTextField companyIdTextField, userNameTextField, keyTextField;
    JPasswordField passwordField, checkPasswordField;

    static Connection connection;
    static ResultSet rs;
    String pwd;
    String checkPwd;

    public RegisterEmployee() {
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        jp4 = new JPanel();
        jp5 = new JPanel();
        jp6 = new JPanel();

        companyIdLabel = new JLabel("企业代号");
        userNameLabel = new JLabel(" 用 户 名 ");
        pwdLabel = new JLabel("  密   码   ");
        checkPwdLabel = new JLabel("确认密码");
        keyLabel = new JLabel("企业密钥");

        companyIdTextField = new JTextField(10);
        userNameTextField = new JTextField(10);
        keyTextField = new JTextField(10);

        passwordField = new JPasswordField(10);
        checkPasswordField = new JPasswordField(10);

        registerButton = new JButton("注册");
        backButton = new JButton("返回");

        registerButton.addActionListener(this);
        backButton.addActionListener(this);

        jp1.add(companyIdLabel);
        jp1.add(companyIdTextField);

        jp2.add(userNameLabel);
        jp2.add(userNameTextField);

        jp3.add(pwdLabel);
        jp3.add(passwordField);

        jp4.add(checkPwdLabel);
        jp4.add(checkPasswordField);

        jp5.add(keyLabel);
        jp5.add(keyTextField);

        jp6.add(registerButton);
        jp6.add(backButton);

        add(jp1);
        add(jp2);
        add(jp3);
        add(jp4);
        add(jp5);
        add(jp6);

        setLayout(new GridLayout(7, 1));
        setTitle("项目进度查询管理系统-员工注册");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        try {
            Class.forName(driverName);

            connection = DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("注册")) {
            pwd = Arrays.toString(passwordField.getPassword());
            checkPwd = Arrays.toString(checkPasswordField.getPassword());

            if (companyIdTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入企业代号！", "提示消息", JOptionPane.WARNING_MESSAGE);
            } else if (userNameTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入用户名！", "提示消息", JOptionPane.WARNING_MESSAGE);
            } else if ("".equals(passwordField.getText().toString().trim()) || "".equals(checkPasswordField.getText().toString().trim())) {
                JOptionPane.showMessageDialog(null, "请输入密码和确认密码！", "提示消息", JOptionPane.WARNING_MESSAGE);
            }
            else if (!pwd.equals(checkPwd)) {
                JOptionPane.showMessageDialog(null, "密码和确认密码不一致！", "提示消息", JOptionPane.WARNING_MESSAGE);
            } else if (keyTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入密钥！", "提示消息", JOptionPane.WARNING_MESSAGE);
            } else {
                registerEmployee();
            }
        } else {
            dispose();
            new LoginEmployee();
        }
    }

    public void registerEmployee() {
        try {
            PreparedStatement ps = connection.prepareStatement("select employee_name, employee_pwd from employees" +
                    " where company_id = ? and employee_name = ?");
            ps.setInt(1, Integer.parseInt(companyIdTextField.getText()));
            ps.setString(2, userNameTextField.getText());

            rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "该用户已存在！", "提示消息", JOptionPane.WARNING_MESSAGE);
                clear();
                return;
            }

            ps = connection.prepareStatement("select company_key from companies where company_id = ?");
            ps.setInt(1, Integer.parseInt(companyIdTextField.getText()));
            rs = ps.executeQuery();
            if (rs.next()) {
                if (!rs.getString(1).equals(keyTextField.getText())) {
                    JOptionPane.showMessageDialog(null, "密钥错误！", "提示消息", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            ps = connection.prepareStatement("insert into employees (company_id, employee_name, employee_pwd) " +
                        "values (?, ?, ?)");
            ps.setInt(1, Integer.parseInt(companyIdTextField.getText()));
            ps.setString(2, userNameTextField.getText());
            ps.setString(3, encrypt(passwordField.getText()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "注册成功！", "提示消息", JOptionPane.WARNING_MESSAGE);
            dispose();

            new LoginEmployee();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        companyIdTextField.setText("");
        userNameTextField.setText("");
        keyTextField.setText("");
        passwordField.setText("");
        checkPasswordField.setText("");
    }

    public static void main(String[] args) {
        new RegisterEmployee();
    }
}
