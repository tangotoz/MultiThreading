package com.tango.experiment.client.GUI;

import com.tango.experiment.client.service.UserAndDocumentService;
import com.tango.experiment.pojo.User;
import com.tango.experiment.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class RegisterFrame extends JFrame {
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JPasswordField confirmPasswordText;
    private JButton registerButton;
    private JButton backButton;
    private LoginFrame loginFrame;
    private JLabel errorLabel;

    private JToggleButton togglePasswordButton; // 切换密码可见性的按钮
    private ImageIcon eyeOpenIcon; // 眼睛图标（显示密码）
    private ImageIcon eyeClosedIcon; // 眼睛图标（隐藏密码）

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        init();
    }

    private void init() {
        eyeOpenIcon = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/eye-open.png")))
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        eyeClosedIcon = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/eye-close.png"))).
                getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        setTitle("用户注册");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300); // 调整窗口高度
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 255));

        Font font = new Font("Fira Code", Font.PLAIN, 14);

        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setBounds(50, 30, 100, 30);
        usernameLabel.setFont(font);
        panel.add(usernameLabel);

        usernameText = new JTextField();
        usernameText.setBounds(150, 30, 180, 30);
        usernameText.setFont(font);
        panel.add(usernameText);

        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(50, 80, 100, 30);
        passwordLabel.setFont(font);
        panel.add(passwordLabel);

        passwordText = new JPasswordField();
        passwordText.setBounds(150, 80, 180, 30);
        passwordText.setFont(font);
        panel.add(passwordText);

        // 切换密码可见性按钮
        togglePasswordButton = new JToggleButton(eyeClosedIcon); // 默认图标为“关闭眼睛”
        togglePasswordButton.setBounds(330, 80, 40, 30); // 放置按钮在密码框右侧
        togglePasswordButton.setBackground(new Color(240, 248, 255));
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setBorderPainted(false);
        togglePasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility());
        panel.add(togglePasswordButton);


        JLabel confirmPasswordLabel = new JLabel("确认密码:");
        confirmPasswordLabel.setBounds(50, 130, 100, 30);
        confirmPasswordLabel.setFont(font);
        panel.add(confirmPasswordLabel);

        confirmPasswordText = new JPasswordField();
        confirmPasswordText.setBounds(150, 130, 180, 30);
        confirmPasswordText.setFont(font);
        panel.add(confirmPasswordText);

        registerButton = new JButton("注册");
        registerButton.setBounds(150, 180, 80, 30);
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Fira Code", Font.BOLD, 14));
        panel.add(registerButton);

        backButton = new JButton("返回");
        backButton.setBounds(240, 180, 80, 30);
        backButton.setBackground(new Color(34, 139, 34));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Fira Code", Font.BOLD, 14));
        panel.add(backButton);

        // 添加 errorLabel
        errorLabel = new JLabel();
        errorLabel.setBounds(50, 230, 300, 30);
        errorLabel.setFont(new Font("Fira Code", Font.ITALIC, 12));
        errorLabel.setForeground(Color.RED); // 设置为红色，用于显示错误信息
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER); // 居中显示
        panel.add(errorLabel);


        // 添加注册按钮的事件监听器
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        // 添加返回按钮的事件监听器
        backButton.addActionListener(e -> {
            // 返回登录界面
            dispose();  // 关闭注册窗口
            loginFrame.setVisible(true);
        });

        add(panel);
        setVisible(true);
    }

    private void togglePasswordVisibility() {
        if (togglePasswordButton.isSelected()) {
            passwordText.setEchoChar((char) 0); // 显示密码
            confirmPasswordText.setEchoChar((char) 0); // 显示确认密码
            togglePasswordButton.setIcon(eyeOpenIcon); // 更改为“打开眼睛”图标
        } else {
            passwordText.setEchoChar('*'); // 隐藏密码
            confirmPasswordText.setEchoChar('*'); // 隐藏确认密码
            togglePasswordButton.setIcon(eyeClosedIcon); // 更改为“关闭眼睛”图标
        }
    }

    // 注册逻辑函数
    private void register() {
        String username = usernameText.getText();
        String password = new String(passwordText.getPassword());
        String confirmPassword = new String(confirmPasswordText.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("请填写所有字段");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("两次输入的密码不一致，请重新输入");
            return;
        }

        try {
            // 检查用户名是否已存在
            UserAndDocumentService.init();
            User existingUser = UserAndDocumentService.searchUser(username);
            if (Objects.nonNull(existingUser)) {
                showError("用户名已被注册");
                return;
            }

            // 保存用户数据
            boolean success = UserAndDocumentService.insertUser(username, MD5Utils.encrypt(password), "browser");
            if (success) {
                JOptionPane.showMessageDialog(this, "注册成功，请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
                dispose();  // 关闭注册窗口
                new LoginFrame();  // 打开登录窗口
            } else {
                showError("注册失败，请稍后重试");
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("注册时出错: {}", e.getMessage());
            showError("系统错误请重试");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message); // 显示错误信息
    }
}
