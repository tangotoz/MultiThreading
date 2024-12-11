package com.tango.experiment.client.service;

import com.tango.experiment.client.GUI.utils.ProgressMonitorStream;
import com.tango.experiment.common.ClientMsg;
import com.tango.experiment.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class UploadService extends Thread {
    private Socket socket;
    private String uploadFilePath;
    private String uploadFileName;

    public UploadService(String uploadFilePath, String uploadFileName) {
        this.uploadFilePath = uploadFilePath;
        this.uploadFileName = uploadFileName;
    }

    @Override
    public void run() {
        try {
            String serverIp = ConfigUtils.getValue("server_ip");
            int serverPort = Integer.parseInt(ConfigUtils.getValue("server_port"));
            socket = new Socket(serverIp, serverPort);
            socket.setSoTimeout(1000);
            File uploadFile = new File(uploadFilePath, uploadFileName);
            FileInputStream in = new FileInputStream(uploadFile);
            ProgressMonitorStream stream = new ProgressMonitorStream("上传" + uploadFile.getName(), in.available());
            stream.getProgressMonitor().setMillisToDecideToPopup(0);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.write(ClientMsg.UPLOAD);
            oos.writeObject(uploadFileName);
            oos.flush();
            log.info("connect server to upload file:{}", uploadFile.getAbsolutePath());

            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                oos.write(bytes, 0, len);
                oos.flush();
            }

            log.info("upload file successfully:{}", uploadFile.getAbsolutePath());
            in.close();
            JOptionPane.showMessageDialog(null, "upload file successfully:" + uploadFile.getName(), "提示", JOptionPane.INFORMATION_MESSAGE);

            socket.close();
            this.interrupt();
        } catch (IOException ex) {
            log.error("connect server error:{}", ex.getMessage());
        }
    }
}
