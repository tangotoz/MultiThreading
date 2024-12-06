package com.tango.experiment.client.GUI;

import com.tango.experiment.client.GUI.JPanel.UserManagePanel;
import com.tango.experiment.pojo.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class MainFrame extends JFrame {
    private User user;

    public MainFrame(User user, LoginFrame loginFrame) {
        this.user = user;
        init();
    }

    private void init() {
        setTitle("欢迎" + user.getUsername() + "!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 使用 JTabbedPane 作为主界面的布局管理
        JTabbedPane tabbedPane = new JTabbedPane();

        // 用户管理标签页
        UserManagePanel userManagePanel = new UserManagePanel();
        tabbedPane.addTab("用户管理", userManagePanel);

        // 文档管理标签页
        JPanel documentManagementPanel = new JPanel();
        documentManagementPanel.setLayout(new BorderLayout());
        JLabel documentManagementLabel = new JLabel("文档管理", SwingConstants.CENTER);
        documentManagementLabel.setFont(new Font("Fira Code", Font.BOLD, 20));
        documentManagementPanel.add(documentManagementLabel, BorderLayout.CENTER);
        tabbedPane.addTab("文档管理", documentManagementPanel);

        // 个人中心标签页
        JPanel personalCenterPanel = new JPanel();
        personalCenterPanel.setLayout(new BorderLayout());
        JLabel personalCenterLabel = new JLabel("个人中心", SwingConstants.CENTER);
        personalCenterLabel.setFont(new Font("Fira Code", Font.BOLD, 20));
        personalCenterPanel.add(personalCenterLabel, BorderLayout.CENTER);
        tabbedPane.addTab("个人中心", personalCenterPanel);

        // 将标签页添加到主界面
        add(tabbedPane);

        setVisible(true);
    }
}
