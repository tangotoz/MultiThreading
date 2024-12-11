package com.tango.experiment.client.service;


import com.tango.experiment.client.GUI.utils.ProgressMonitorStream;
import com.tango.experiment.common.ClientMsg;
import com.tango.experiment.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class DownloadService extends Thread {
    private Socket socket;
    private String downloadFileName;
    private String downloadFilePath;

    public DownloadService(String fileName, String downloadFilePath) {
        this.downloadFileName = fileName;
        this.downloadFilePath = downloadFilePath;
    }

    @Override
    public void run() {
        try {
            String serverIp = ConfigUtils.getValue("server_ip");
            int serverPort = Integer.parseInt(Objects.requireNonNull(ConfigUtils.getValue("server_port")));
            socket = new Socket(serverIp, serverPort);
            socket.setSoTimeout(1000);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.write(ClientMsg.DOWNLOAD);
            oos.writeObject(downloadFileName);
            oos.flush();

            File file = new File(downloadFilePath, downloadFileName);
            FileOutputStream fos = new FileOutputStream(file);
            log.info("connect server to download file:{}", downloadFileName);

            int size = (int) ois.readObject();
            ProgressMonitorStream progressMonitorStream = new ProgressMonitorStream("下载" + downloadFileName, size);
            progressMonitorStream.getProgressMonitor().setMillisToDecideToPopup(0);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = progressMonitorStream.setProgress(ois.read(buffer))) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }

            log.info("download file successfully:{}", file.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "下载文件" + downloadFileName + "成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            fos.close();
            ois.close();
            socket.close();
            this.interrupt();
        } catch (IOException | ClassNotFoundException ex) {
            log.error("connect server error:{}", ex.getMessage());
        }
    }
}