package com.tango.experiment.client.GUI;

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
import java.util.Objects;
import java.util.Random;

@Slf4j
public class LoginFrame extends JFrame implements ActionListener {
    private JTextField userText;
    private JPasswordField passwordText;
    private JTextField captchaText; // 用于输入验证码
    private JLabel captchaLabel; // 显示验证码
    private String generatedCaptcha; // 保存生成的验证码
    private JButton loginButton;
    private JButton registerButton;
    private JToggleButton togglePasswordButton; // 切换密码可见性的按钮
    private JLabel errorLabel; // 错误信息提示

    private ImageIcon eyeOpenIcon; // 眼睛图标（显示密码）
    private ImageIcon eyeClosedIcon; // 眼睛图标（隐藏密码）

    public LoginFrame() {
        init();
    }

    private void init() {
        // 加载眼睛图标
        eyeOpenIcon = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/eye-open.png")))
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        eyeClosedIcon = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/eye-close.png")))
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        setTitle("文档管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300); // 调整窗口高度
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 255));

        Font font = new Font("Fira Code", Font.PLAIN, 14);

        JLabel userLabel = new JLabel("用户名:");
        userLabel.setBounds(50, 30, 100, 30);
        userLabel.setFont(font);
        panel.add(userLabel);

        userText = new JTextField();
        userText.setBounds(150, 30, 180, 30);
        userText.setFont(font);
        panel.add(userText);

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
        togglePasswordButton.setFont(new Font("Fira Code", Font.PLAIN, 14));
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setBorderPainted(false);
        togglePasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        togglePasswordButton.addActionListener(e -> togglePasswordVisibility());
        panel.add(togglePasswordButton);

        // 验证码输入框
        JLabel captchaTitle = new JLabel("验证码:");
        captchaTitle.setBounds(50, 130, 100, 30);
        captchaTitle.setFont(font);
        panel.add(captchaTitle);

        captchaText = new JTextField();
        captchaText.setBounds(150, 130, 100, 30);
        captchaText.setFont(font);
        panel.add(captchaText);

        // 验证码显示
        captchaLabel = new JLabel(generateCaptcha());
        captchaLabel.setBounds(260, 130, 100, 30); // 设置验证码在输入框右边
        captchaLabel.setFont(new Font("Fira Code", Font.BOLD, 14));
        captchaLabel.setForeground(new Color(255, 69, 0)); // 橙色字体
        captchaLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // 鼠标指针样式
        panel.add(captchaLabel);

        // 添加点击刷新验证码功能
        captchaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                captchaLabel.setForeground(randomColor());
                refreshCaptcha();
            }
        });

        // 添加错误提示标签
        errorLabel = new JLabel("");
        errorLabel.setBounds(50, 220, 300, 30); // 设置位置
        errorLabel.setFont(new Font("Fira Code", Font.PLAIN, 12));
        errorLabel.setForeground(Color.RED); // 红色字体
        panel.add(errorLabel);

        loginButton = new JButton("登录");
        loginButton.setBounds(150, 180, 80, 30);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Fira Code", Font.BOLD, 14));
        panel.add(loginButton);

        registerButton = new JButton("注册");
        registerButton.setBounds(240, 180, 80, 30);
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Fira Code", Font.BOLD, 14));
        panel.add(registerButton);

        // 添加登录按钮的事件监听器
        loginButton.setActionCommand("login");
        loginButton.addActionListener(this);

        // 添加注册按钮的事件监听器
        registerButton.setActionCommand("register");
        registerButton.addActionListener(this);

        add(panel);
        setVisible(true);
    }

    // 切换密码框的可见性
    private void togglePasswordVisibility() {
        if (togglePasswordButton.isSelected()) {
            passwordText.setEchoChar((char) 0); // 显示密码
            togglePasswordButton.setIcon(eyeOpenIcon); // 更改为“打开眼睛”图标
        } else {
            passwordText.setEchoChar('*'); // 隐藏密码
            togglePasswordButton.setIcon(eyeClosedIcon); // 更改为“关闭眼睛”图标
        }
    }

    // 登录逻辑函数
    private void login() {
        String username = userText.getText();
        String password = new String(passwordText.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            showError("你输入的账号或密码为空");
            return;
        }
        try {
            UserAndDocumentService.init();
            User user = UserAndDocumentService.searchUser(username);
            if (Objects.isNull(user)) {
                showError("你输入的账号不存在");
                return;
            }

            if (!user.getPassword().equals(MD5Utils.encrypt(password))) {
                showError("你输入的账号密码错误");
                return;
            }
            if (!captchaText.getText().equals(generatedCaptcha)) {
                showError("你输入的验证码错误，请重新输入");
                refreshCaptcha();
                return;
            }

            setVisible(false);
            SwingUtilities.invokeLater(() -> new MainFrame(user, this));
        } catch (IOException | ClassNotFoundException e) {
            log.error("log error:{}", e.getMessage());
        }
    }

    public void register() {
        setVisible(false);
        SwingUtilities.invokeLater(() -> new RegisterFrame(this));
    }

    // 生成验证码的方法
    private String generateCaptcha() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 4; i++) { // 4位验证码
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }
        generatedCaptcha = captcha.toString();
        return generatedCaptcha;
    }

    // 刷新验证码
    private void refreshCaptcha() {
        captchaLabel.setText("");
        captchaLabel.setText(generateCaptcha());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "login" -> login();
            case "register" -> register();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message); // 显示错误信息
    }

    private Color randomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new Color(red, green, blue);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
