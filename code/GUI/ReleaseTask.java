package code.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import static code.jdbc.Common.*;

public class ReleaseTask extends JFrame implements ActionListener {

    JPanel jp1, jp2, jp3;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel employeeLabel;
    private JComboBox<String> employeeBox;
    private JButton addButton, backButton;

    static Connection connection;
    static PreparedStatement ps;
    static ResultSet rs;
    int company_id;

    public ReleaseTask(int company_id) {
        this.company_id = company_id;
        setTitle("项目进度查询管理系统-项目发布");

        // 初始化界面元素
        nameLabel = new JLabel("项目名称:");
        nameTextField = new JTextField(20);
        employeeLabel = new JLabel("选择员工:");
        employeeBox = new JComboBox<>();

        addButton = new JButton("项目发布");
        backButton = new JButton("返回");

        addButton.addActionListener(this);
        backButton.addActionListener(this);

        setLayout(new GridLayout(4, 1));

        jp1 = new JPanel();
        jp1.add(nameLabel);
        jp1.add(nameTextField);

        jp2 = new JPanel();
        jp2.add(employeeLabel);
        jp2.add(employeeBox);

        jp3 = new JPanel();
        jp3.add(addButton);
        jp3.add(backButton);

        add(jp1);
        add(jp2);
        add(jp3);

        try {
            connection = DriverManager.getConnection(url, username, password);
            loadEmployees(); // 加载员工列表
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 设置窗口属性
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 加载员工列表
    private void loadEmployees() throws SQLException {
        ps = connection.prepareStatement("select employee_name FROM employees where company_id = ?");
        ps.setInt(1, company_id);
        rs = ps.executeQuery();

        ArrayList<String> employees = new ArrayList<>();
        while (rs.next()) {
            String employeeName = rs.getString(1);
            employees.add(employeeName);
        }

        employeeBox.setModel(new DefaultComboBoxModel<>(employees.toArray(new String[0])));
    }

    public static void main(String[] args) {
        new ReleaseTask(1001);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("项目发布")) {
            String projectName = nameTextField.getText().trim();
            String selectedEmployee = (String) employeeBox.getSelectedItem();
            int employee_id = 0;

            if (projectName.isEmpty()) {
                JOptionPane.showMessageDialog(ReleaseTask.this, "请输入项目名称", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(ReleaseTask.this, "请选择员工", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ps = connection.prepareStatement("SELECT employee_id FROM employees where employee_name = ?");
                ps.setString(1, selectedEmployee);
                rs = ps.executeQuery();
                if (rs.next()) {
                    employee_id = rs.getInt(1);
                }

                ps = connection.prepareStatement("INSERT INTO tasks (task_name, employee_id) VALUES (?, ?)");
                ps.setString(1, projectName);
                ps.setInt(2, employee_id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(ReleaseTask.this, "项目添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                nameTextField.setText(""); // 清空文本框
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(ReleaseTask.this, "添加项目失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            dispose();
            new CompanyEmployee(company_id);
        }
    }
}
