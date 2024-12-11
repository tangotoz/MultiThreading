package com.tango.experiment.client.GUI.JPanel;

import com.tango.experiment.client.service.UserAndDocumentService;
import com.tango.experiment.pojo.Doc;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

@Slf4j
public class DocManagePanel extends JPanel implements ActionListener {
    private JTable docTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton, uploadButton, downloadButton;
    private List<Doc> documents;

    public DocManagePanel() {
        init();
        initTableAppearance();
        try {
            documents = UserAndDocumentService.getAllDoc();
        } catch (IOException | ClassNotFoundException e) {
            log.error("DocManagePanel: {}", e.getMessage());
        }
        loadData(documents);
    }

    private void init() {
        setLayout(new BorderLayout());
        // 初始化表格模型
        String[] columnNames = {"文档ID", "文档名称", "描述", "上传时间", "下载次数"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        docTable = new JTable(tableModel);
        docTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(docTable);
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
        uploadButton = new JButton("上传");
        downloadButton = new JButton("下载");
        searchButton = new JButton("查询");

        uploadButton.setActionCommand("upload");
        downloadButton.setActionCommand("download");
        searchButton.setActionCommand("search");

        uploadButton.addActionListener(this);
        downloadButton.addActionListener(this);
        searchButton.addActionListener(this);

        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(searchButton);

        // 设置按钮颜色和样式
        uploadButton.setBackground(new Color(70, 130, 180));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        downloadButton.setBackground(new Color(34, 139, 34));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFocusPainted(false);
        downloadButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchButton.setBackground(new Color(220, 20, 60));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData(List<Doc> documents) {
        tableModel.setRowCount(0);
        if (documents != null)
            documents.forEach(doc -> {
                tableModel.addRow(new Object[]{
                        doc.getDocumentId(),
                        doc.getFileName(),
                        doc.getDescription(),
                        doc.getUploadAt(),
                        doc.getDownloadCount()
                });
            });
    }

    private void searchDocuments() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入查询关键字!", "错误", JOptionPane.WARNING_MESSAGE);
            try {
                documents = UserAndDocumentService.getAllDoc();
                loadData(documents);
            } catch (IOException | ClassNotFoundException e) {
                log.error("DocManagePanel searchDocuments error: {}", e.getMessage());
            }
            return;
        }

//        try {
//            documents = UserAndDocumentService.searchDocuments(keyword);
//            loadData(documents);
//        } catch (IOException | ClassNotFoundException e) {
//            log.error("DocManagePanel searchDocuments error: {}", e.getMessage());
//        }
    }

    private void uploadDocument() {
        // 模拟上传文档
        // 你可以通过文件选择器让用户选择文件并上传
        JOptionPane.showMessageDialog(this, "上传文档功能暂未实现", "功能未实现", JOptionPane.INFORMATION_MESSAGE);
    }

    private void downloadDocument() {
        int selectedRow = docTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要下载的文档", "错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedDocName = (String) tableModel.getValueAt(selectedRow, 1);
        // 模拟下载文档
        // 实际上，你需要在这里处理文档下载的逻辑
        JOptionPane.showMessageDialog(this, "下载文档: " + selectedDocName, "下载", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "upload" -> uploadDocument();
            case "download" -> downloadDocument();
            case "search" -> searchDocuments();
        }
    }

    private void initTableAppearance() {
        docTable.setFont(new Font("Fira Code", Font.PLAIN, 12));
        docTable.getTableHeader().setBackground(new Color(70, 130, 180));
        docTable.getTableHeader().setForeground(Color.WHITE);
        docTable.getTableHeader().setOpaque(true);
        docTable.getTableHeader().setFont(new Font("Fira Code", Font.BOLD, 14));

        docTable.setRowHeight(25);
        docTable.setGridColor(Color.DARK_GRAY);
        docTable.setShowGrid(true);

        // 确保设置渲染器是为文档列（假设是文档名列）专门指定
        docTable.setDefaultRenderer(String.class, new CustomTableCellRenderer());
        resizeColumnWidth(docTable);
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
