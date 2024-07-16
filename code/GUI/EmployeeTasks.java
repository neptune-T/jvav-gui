package code.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;

import static code.jdbc.Common.*;

public class EmployeeTasks extends JFrame implements ActionListener {
    private JLabel taskCount;
    private JList<String> jList;
    private JButton backButton, editButton;

    static Connection connection;
    static ResultSet rs;
    static PreparedStatement ps;
    String name;
    int identity;
    int company_id;

    public EmployeeTasks(String name, int identity) {
        this.name = name;
        this.identity = identity;
        linkDB();

        try {
            ps = connection.prepareStatement("select company_id from employees where employee_name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                company_id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        setTitle("项目进度查询管理系统-员工信息");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        taskCount = new JLabel(name + "    项目数量：");
        jList = new JList<>();
        backButton = new JButton("返回");
        editButton = new JButton(identity == 0 ? "修改信息" : "查看信息");

        backButton.addActionListener(this);
        editButton.addActionListener(this);

        JPanel jp1 = new JPanel();
        jp1.add(taskCount);

        JScrollPane jsp = new JScrollPane(jList);

        JPanel jp2 = new JPanel();
        jp2.add(backButton);
        jp2.add(editButton);

        add(jp1, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);

        setVisible(true);

        // 获取数据库中的员工列表
        ArrayList<String> employees = getEmployeeList();
        updateJList(employees);

        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击事件
                    String selectedEmployee = jList.getSelectedValue();
                    if (selectedEmployee != null) {
                        employeeDetails(selectedEmployee);
                    }
                }
            }
        });
    }

    private static void linkDB() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查询数据库中的员工姓名并返回一个字符串列表
    private ArrayList<String> getEmployeeList() {
        ArrayList<String> taskNames = new ArrayList<>();

        try {
            ps = connection.prepareStatement("select task_name from tasks join employees" +
                    " on tasks.employee_id = employees.employee_id where employee_name = ?;");
            ps.setString(1, name);
            rs = ps.executeQuery();

            // 遍历结果集并将员工姓名添加到列表中
            while (rs.next()) {
                String task_name = rs.getString(1);
                taskNames.add(task_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return taskNames;
    }

    // 更新GUI中的员工列表
    private void updateJList(ArrayList<String> taskNames) {
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (String task : taskNames) {
            listModel.addElement(task);
        }

        jList.setModel(listModel);
        taskCount.setText(name + "    项目数量：" + taskNames.size());
    }

    // 显示员工详细信息
    private void employeeDetails(String employeeName) {
        dispose();
        new TaskDetail(employeeName, name, identity);
    }

    public static void main(String[] args) {
        new EmployeeTasks("zhang", 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("返回")) {
            dispose();
            if (identity == 0) {
                new LoginEmployee();
            } else {
                new CompanyEmployee(company_id);
            }
        } else {
            dispose();
            new MessageEmployee(name, company_id, identity);
        }
    }
}
