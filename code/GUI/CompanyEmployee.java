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

public class CompanyEmployee extends JFrame implements ActionListener {

    private JLabel employeeCount;
    private JList<String> jList;
    private JButton backButton, editButton, releaseButton;

    static Connection connection;
    static ResultSet rs;
    static PreparedStatement ps;
    int company_id;

    public CompanyEmployee(int company_id) {
        this.company_id = company_id;
        linkDB();

        setTitle("项目进度查询管理系统-员工信息");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        employeeCount = new JLabel("员工数量：");
        jList = new JList<>();
        backButton = new JButton("返回");
        editButton = new JButton("修改信息");
        releaseButton = new JButton("项目发布");

        backButton.addActionListener(this);
        editButton.addActionListener(this);
        releaseButton.addActionListener(this);

        JPanel jp1 = new JPanel();
        jp1.add(employeeCount);

        JScrollPane jsp = new JScrollPane(jList);

        JPanel jp2 = new JPanel();
        jp2.add(releaseButton);
        jp2.add(editButton);
        jp2.add(backButton);

        add(jp1, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);

        setVisible(true);

        // 获取数据库中的员工列表
        ArrayList<String> employees = getEmployeeList();
        updateJList(employees);

        // 设置员工列表的选择监听器
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
        ArrayList<String> employeeNames = new ArrayList<>();

        try {
            ps = connection.prepareStatement("SELECT employee_name FROM employees where company_id = ?");
            ps.setInt(1, company_id);
            rs = ps.executeQuery();

            // 遍历结果集并将员工姓名添加到列表中
            while (rs.next()) {
                String name = rs.getString(1);
                employeeNames.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employeeNames;
    }

    // 更新GUI中的员工列表
    private void updateJList(ArrayList<String> employees) {
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (String employee : employees) {
            listModel.addElement(employee);
        }

        jList.setModel(listModel);
        employeeCount.setText("员工数量：" + employees.size());
    }

    // 显示员工详细信息
    private void employeeDetails(String employeeName) {
        dispose();
        new EmployeeTasks(employeeName, 1);
    }

    public static void main(String[] args) {
        new CompanyEmployee(1000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("返回")) {
            dispose();
            new CompanyEmployee (company_id);
        } else if (e.getActionCommand().equals("项目发布")) {
            dispose();
            new ReleaseTask(company_id);
        } else {
            dispose();

        }
    }
}