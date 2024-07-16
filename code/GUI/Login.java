package code.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame implements ActionListener {

    JPanel jp1, jp2, jp3;
    JLabel titleLabel;
    JButton ClientButton, StaffButton;

    public Login() {
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();

        titleLabel = new JLabel("让您的路程精彩纷呈");
        Font font = new Font("SansSerif", Font.BOLD, 24);
        titleLabel.setFont(font);

        ClientButton = new JButton("客户端");
        StaffButton = new JButton("员工查询端");

        ClientButton.setActionCommand("Client");
        StaffButton.setActionCommand("Staff");

        ClientButton.addActionListener(this);
        StaffButton.addActionListener(this);

        jp1.add(titleLabel);
        jp2.add(ClientButton);
        jp3.add(StaffButton);

        add(jp1);
        add(jp2);
        add(jp3);

        setLayout(new GridLayout(3, 1));
        setTitle("中山国际酒店-登录页面");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Client")) {
            dispose();
            new ClientLogin();
        } else if (e.getActionCommand().equals("Staff")) {
            dispose();
            new StaffLogin();
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
