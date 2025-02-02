package com.tango.experiment.client.GUI.JPanel;

import com.tango.experiment.client.service.UserAndDocumentService;
import com.tango.experiment.pojo.User;
import com.tango.experiment.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import static com.tango.experiment.client.service.UserAndDocumentService.searchUser;

@Slf4j
public class UserManagePanel extends JPanel implements ActionListener {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, deleteButton, updateButton, searchButton;
    private List<User> users;

    public UserManagePanel() {
        init();
        initTableAppearance();
        try {
            users = UserAndDocumentService.getAllUser();
        } catch (IOException | ClassNotFoundException e) {
            log.error("UserManagePanel: {}", e.getMessage());
        }
        loadData(users);
    }

    private void init() {
        setLayout(new BorderLayout());
        // 初始化表格模型
        String[] columnNames = {"用户ID", "用户名", "密码", "角色", "创建时间", "更改时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // 底部的搜索框和按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 搜索框
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 30));
        bottomPanel.add(searchField, BorderLayout.WEST);

        // 按钮部分
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        addButton = new JButton("增加");
        deleteButton = new JButton("删除");
        updateButton = new JButton("修改");
        searchButton = new JButton("查询");

        addButton.setActionCommand("add");
        deleteButton.setActionCommand("delete");
        updateButton.setActionCommand("update");
        searchButton.setActionCommand("search");

        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        updateButton.addActionListener(this);
        searchButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(searchButton);

        // 设置按钮颜色和样式
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        updateButton.setBackground(new Color(100, 149, 237)); // 设置蓝色
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchButton.setBackground(new Color(34, 139, 34)); // 设置绿色
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData(List<User> users) {
        tableModel.setRowCount(0);
        users.forEach(user -> {
            tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            });
        });
    }

    private void addUser() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        // 用户名输入框
        JLabel usernameLabel = new JLabel("用户名:");
        JTextField usernameField = new JTextField();
        panel.add(usernameLabel);
        panel.add(usernameField);

        // 密码输入框
        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        // 角色选择框
        JLabel roleLabel = new JLabel("角色:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"admin", "browser", "operator"});
        panel.add(roleLabel);
        panel.add(roleComboBox);

        // 弹出对话框
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "添加用户",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空!", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean success = UserAndDocumentService.insertUser(username, MD5Utils.encrypt(password), role);
                if (success) {
                    JOptionPane.showMessageDialog(this, "用户添加成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
                    users = UserAndDocumentService.getAllUser();
                    loadData(users); // 实时更新JTable
                } else {
                    JOptionPane.showMessageDialog(this, "用户添加失败!", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "添加用户失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                log.error("UserManagePanel addUser error: {}", e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的用户", "错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定删除所选中的用户吗?",
                "确定删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                boolean success = UserAndDocumentService.deleteUser(userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "删除成功");
                    users = UserAndDocumentService.getAllUser();
                    loadData(users);
                } else {
                    JOptionPane.showMessageDialog(this, "删除失败");
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "删除用户失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                log.error("UserManagePanel deleteUser error: {}", e.getMessage());
            }
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的用户!", "错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentUsername = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPassword = (String) tableModel.getValueAt(selectedRow, 2);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 3);
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));


        // 用户名输入框
        JLabel usernameLabel = new JLabel("用户名:");
        JTextField usernameField = new JTextField(currentUsername);
        panel.add(usernameLabel);
        panel.add(usernameField);

        // 密码输入框
        JLabel passwordLabel = new JLabel("密码:");
        JTextField passwordField = new JTextField(currentPassword);
        panel.add(passwordLabel);
        panel.add(passwordField);


        // 角色选择框
        JLabel roleLabel = new JLabel("角色:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"admin", "browser", "operator"});
        roleComboBox.setSelectedItem(currentRole);
        panel.add(roleLabel);
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "修改用户",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );


        if (result == JOptionPane.OK_OPTION) {
            String newUsername = usernameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            String newRole = (String) roleComboBox.getSelectedItem();

            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名或密码不能为空!", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean success = UserAndDocumentService.updateUser(String.valueOf(userId), newUsername, MD5Utils.encrypt(newPassword), newRole);
                if (success) {
                    JOptionPane.showMessageDialog(this, "用户修改成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
                    users = UserAndDocumentService.getAllUser();
                    loadData(users); // 更新表格
                } else {
                    JOptionPane.showMessageDialog(this, "用户修改失败!", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "修改用户失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                log.error("UserManagePanel updateUser error: {}", e.getMessage());
            }
        }
    }

    private void searchUser() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入查询关键字!", "错误", JOptionPane.WARNING_MESSAGE);
            try {
                users = UserAndDocumentService.getAllUser();
                loadData(users);
            } catch (IOException | ClassNotFoundException e) {
                log.error("UserManagePanel searchUser error: {}", e.getMessage());
            }
            return;
        }

        try {
            users = UserAndDocumentService.getUserByLike(keyword);
            loadData(users);
        } catch (IOException | ClassNotFoundException e) {
            log.error("UserManagePanel searchUser error: {}", e.getMessage());
        }

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "add" -> addUser();
            case "delete" -> deleteUser();
            case "update" -> updateUser();
            case "search" -> searchUser();
        }
    }

    private void initTableAppearance() {
        userTable.setFont(new Font("Fira Code", Font.PLAIN, 12));
        userTable.getTableHeader().setBackground(new Color(70, 130, 180));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.getTableHeader().setOpaque(true);
        userTable.getTableHeader().setFont(new Font("Fira Code", Font.BOLD, 14));

        userTable.setRowHeight(25);
        userTable.setGridColor(Color.DARK_GRAY);
        userTable.setShowGrid(true);

        // 确保设置渲染器是为角色列（索引3）专门指定
        userTable.setDefaultRenderer(String.class, new CustomTableCellRenderer());
        resizeColumnWidth(userTable);

        // 添加鼠标悬停效果
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                int row = userTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    userTable.setRowSelectionInterval(row, row);
                }
            }
        });
    }

    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(new Color(135, 206, 250)); // 鼠标悬停时变蓝
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    }


    private void resizeColumnWidth(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            int width = 100; // 设置默认宽度
            for (int j = 0; j < table.getRowCount(); j++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                width = Math.max(width, table.getValueAt(j, i).toString().length() * 10);
            }
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }


}
