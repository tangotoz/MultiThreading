package com.tango.experiment.client.GUI.JPanel;

import com.tango.experiment.client.GUI.LoginFrame;
import com.tango.experiment.client.GUI.MainFrame;
import com.tango.experiment.client.service.UserAndDocumentService;
import com.tango.experiment.pojo.User;
import com.tango.experiment.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class PersonInfoPanel extends JPanel implements ActionListener {
    private User currentUser;
    private JLabel roleLabel;
    private JButton changePasswordButton, logoutButton;
    private MainFrame mainFrame;
    private LoginFrame loginFrame;
    private JLabel updatedAtValue;


    public PersonInfoPanel(User user, MainFrame mainFrame, LoginFrame loginFrame) {
        this.mainFrame = mainFrame;
        this.loginFrame = loginFrame;
        this.currentUser = user;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 5, 5)); // 减小水平和垂直间距
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "个人信息",
                0,
                0,
                new Font("Fira Code", Font.BOLD, 14),
                new Color(33, 150, 243)
        ));

        // 公用字体
        Font labelFont = new Font("Fira Code", Font.PLAIN, 12);
        Font valueFont = new Font("Fira Code", Font.BOLD, 12);

        // 用户名
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(labelFont);
        JLabel usernameValue = new JLabel(currentUser.getUsername());
        usernameValue.setFont(valueFont);

        // 角色
        JLabel roleTitleLabel = new JLabel("角色:");
        roleTitleLabel.setFont(labelFont);
        roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setFont(valueFont);

        // 创建时间
        JLabel createdAtLabel = new JLabel("创建时间:");
        createdAtLabel.setFont(labelFont);
        JLabel createdAtValue = new JLabel(currentUser.getCreatedAt().toString());
        createdAtValue.setFont(valueFont);

        // 最后更新时间
        JLabel updatedAtLabel = new JLabel("最后更新时间:");
        updatedAtLabel.setFont(labelFont);
        updatedAtValue = new JLabel(currentUser.getUpdatedAt().toString());
        updatedAtValue.setFont(valueFont);

        // 添加组件
        panel.add(usernameLabel);
        panel.add(usernameValue);
        panel.add(roleTitleLabel);
        panel.add(roleLabel);
        panel.add(createdAtLabel);
        panel.add(createdAtValue);
        panel.add(updatedAtLabel);
        panel.add(updatedAtValue);

        panel.setBackground(new Color(245, 245, 245)); // 设置浅灰背景色
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        changePasswordButton = createStyledButton("修改密码", new Color(70, 130, 180));
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        logoutButton = createStyledButton("退出", new Color(220, 20, 60));
        logoutButton.addActionListener(this);

        buttonPanel.add(changePasswordButton);
        buttonPanel.add(logoutButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Fira Code", Font.BOLD, 14)); // 使用 Fira Code 字体
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            mainFrame.dispose();
            loginFrame.setVisible(true);
        }
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog();
        dialog.setVisible(true);
    }

    class ChangePasswordDialog extends JDialog {

        private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
        private JButton submitButton, cancelButton;

        public ChangePasswordDialog() {
            setTitle("修改密码");
            setSize(450, 350);
            setModal(true);
            setLocationRelativeTo(null);

            init();
        }

        private void init() {
            // 设置主面板
            JPanel mainPanel = new JPanel();
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8); // 设置组件间距

            // 标签与输入框
            JLabel currentPasswordLabel = new JLabel("当前密码:");
            currentPasswordField = new JPasswordField(15);
            JLabel newPasswordLabel = new JLabel("新密码:");
            newPasswordField = new JPasswordField(15);
            JLabel confirmPasswordLabel = new JLabel("确认密码:");
            confirmPasswordField = new JPasswordField(15);

            // 添加标签和输入框
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(currentPasswordLabel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(currentPasswordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(newPasswordLabel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(newPasswordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(confirmPasswordLabel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(confirmPasswordField, gbc);

            // 按钮面板
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            submitButton = createStyledButton("提交", new Color(70, 130, 180));
            cancelButton = createStyledButton("取消", new Color(220, 20, 60));

            submitButton.addActionListener(e -> handleChangePassword());
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(submitButton);
            buttonPanel.add(cancelButton);

            // 添加按钮区域
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            mainPanel.add(buttonPanel, gbc);

            // 添加主面板到对话框
            add(mainPanel);
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));

            // 悬停效果
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(color.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(color);
                }
            });

            return button;
        }

        private void handleChangePassword() {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请完整填写密码字段", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "两次密码输入不匹配", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "密码修改成功！", "信息", JOptionPane.INFORMATION_MESSAGE);
            try {
                UserAndDocumentService.updateUser(String.valueOf(currentUser.getUserId()), currentUser.getUsername(), MD5Utils.encrypt(newPassword), currentUser.getRole());
                updatedAtValue.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            } catch (IOException | ClassNotFoundException e) {
                log.error("failed to update user:{}", e.getMessage());
            }
            dispose();
        }
    }

}
