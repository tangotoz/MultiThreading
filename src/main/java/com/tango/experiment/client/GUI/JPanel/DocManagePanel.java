package com.tango.experiment.client.GUI.JPanel;

import com.tango.experiment.client.service.DownloadService;
import com.tango.experiment.client.service.UploadService;
import com.tango.experiment.client.service.UserAndDocumentService;
import com.tango.experiment.pojo.Doc;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

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
    private JButton searchButton, uploadButton, downloadButton, statsButton;
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
        statsButton = new JButton("统计");

        uploadButton.setActionCommand("upload");
        downloadButton.setActionCommand("download");
        searchButton.setActionCommand("search");
        statsButton.setActionCommand("stats");

        uploadButton.addActionListener(this);
        downloadButton.addActionListener(this);
        searchButton.addActionListener(this);
        statsButton.addActionListener(this);

        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(statsButton);

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

        statsButton.setBackground(new Color(255, 165, 0));
        statsButton.setForeground(Color.WHITE);
        statsButton.setFocusPainted(false);
        statsButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

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

        try {
            documents = UserAndDocumentService.searchDoc(keyword);
            loadData(documents);
        } catch (IOException | ClassNotFoundException e) {
            log.error("DocManagePanel searchDocuments error: {}", e.getMessage());
        }
    }

    private void showStatistics() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        // 收集下载次数并计算统计数据
        for (Doc doc : documents) {
            int downloadCount = doc.getDownloadCount();
            String fileName = doc.getFileName();

            // 直方图数据
            dataset.addValue(downloadCount, "Downloads", fileName);

            // 圆饼图数据
            pieDataset.setValue(fileName, downloadCount);
        }

        // 创建直方图
// Create the bar chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "下载次数统计", // 图表标题
                "文件名", // X轴标签
                "下载次数", // Y轴标签
                dataset
        );

        // 创建圆饼图
        JFreeChart pieChart = ChartFactory.createPieChart(
                "下载次数分布", // 图表标题
                pieDataset, // 数据集
                true, // 显示图例
                true,
                false
        );

        // 显示图表
        JPanel chartPanel = new JPanel(new GridLayout(1, 2));
        chartPanel.add(new ChartPanel(barChart));
        chartPanel.add(new ChartPanel(pieChart));

        JOptionPane.showMessageDialog(this, chartPanel, "下载统计", JOptionPane.INFORMATION_MESSAGE);
    }

    private void uploadDocument() {
        // 打开文件选择对话框
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择要上传的文件");
        fileChooser.setFileHidingEnabled(false);  // 显示所有文件，包括隐藏的文件

        // 如果用户选择了文件并点击了“打开”
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // 获取选中的文件
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // 获取文件的路径和其他必要信息
            String fileName = selectedFile.getName();
            String description = JOptionPane.showInputDialog(this, "请输入文件描述：");

            if (description == null || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "文件描述不能为空！");
                return;
            }

            new UploadService(selectedFile.getParent(), fileName).run();

            UserAndDocumentService.init();
            try {
                UserAndDocumentService.insertDoc(fileName, description);
                List<Doc> docs = UserAndDocumentService.getAllDoc();
                loadData(docs);
            } catch (IOException | ClassNotFoundException ex) {
                log.error("insertDoc error:{}", ex.getMessage());
            }
        }
    }

    private void downloadDocument() {
        int selectedRow = docTable.getSelectedRow();
        if (selectedRow >= 0) {
            // 获取文件名
            String fileName = (String) docTable.getValueAt(selectedRow, 1);

            // 打开文件夹选择框
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("选择下载文件夹");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 只允许选择文件夹
            fileChooser.setAcceptAllFileFilterUsed(false); // 不显示所有文件过滤器

            // 显示文件夹选择对话框
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // 获取选定的文件夹路径
                String selectedFolderPath = fileChooser.getSelectedFile().getAbsolutePath();

                // 创建 DocDownloadService 并传递文件名和下载路径
                new DownloadService(fileName, selectedFolderPath).run();

                try {
                    // 更新文档的下载次数
                    UserAndDocumentService.init();
                    UserAndDocumentService.updateDoc(fileName);
                    List<Doc> updatedDocs = UserAndDocumentService.getAllDoc();
                    loadData(updatedDocs);
                } catch (IOException | ClassNotFoundException ex) {
                    log.error("Failed to update download count: {}", ex.getMessage());
                }

            } else {
                JOptionPane.showMessageDialog(this, "下载操作被取消", "提示", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "请选择要下载的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "upload" -> uploadDocument();
            case "download" -> downloadDocument();
            case "search" -> searchDocuments();
            case "stats" -> showStatistics();
        }
    }

    private void initTableAppearance() {
        docTable.setFont(new Font("Fira Code", Font.PLAIN, 12));
        docTable.getTableHeader().setBackground(new Color(70, 130, 180));
        docTable.getTableHeader().setForeground(Color.WHITE);
        docTable.getTableHeader().setOpaque(true);
        docTable.getTableHeader().setFont(new Font("Fira Code", Font.BOLD, 14));

        docTable.setRowHeight(25);
        docTable.setGridColor(new Color(220, 220, 220));
        docTable.setSelectionBackground(new Color(173, 216, 230));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < docTable.getColumnCount(); i++) {
            TableColumn column = docTable.getColumnModel().getColumn(i);
            column.setCellRenderer(centerRenderer);
        }
    }
}
