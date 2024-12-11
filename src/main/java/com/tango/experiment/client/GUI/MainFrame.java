package com.tango.experiment.client.GUI;

import com.tango.experiment.client.GUI.JPanel.DocManagePanel;
import com.tango.experiment.client.GUI.JPanel.PersonInfoPanel;
import com.tango.experiment.client.GUI.JPanel.UserManagePanel;
import com.tango.experiment.pojo.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User user;
    private LoginFrame loginFrame;

    public MainFrame(User user, LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
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
        DocManagePanel documentManagementPanel = new DocManagePanel();
        tabbedPane.addTab("文档管理", documentManagementPanel);

        // 个人中心标签页
        PersonInfoPanel personInfoPanel = new PersonInfoPanel(user, this, loginFrame);
        tabbedPane.addTab("个人中心", personInfoPanel);

        // 将标签页添加到主界面
        add(tabbedPane);

        setVisible(true);
    }
}
