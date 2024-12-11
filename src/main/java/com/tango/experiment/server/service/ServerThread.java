package com.tango.experiment.server.service;

import com.tango.experiment.common.*;
import com.tango.experiment.server.mapper.*;
import com.tango.experiment.pojo.User;
import com.tango.experiment.utils.ConfigUtils;
import com.tango.experiment.utils.SqlSessionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class ServerThread extends Thread {
    private static ConcurrentHashMap<String, Consumer<String[]>> actionMap = new ConcurrentHashMap<>();
    private static Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static boolean exit;
    private static final String serverFilepath;
    private static UserMapper userMapper = null;
    private static DocMapper docMapper = null;

    static {
        userMapper = SqlSessionUtils.getUserMapper();
        docMapper = SqlSessionUtils.getDocMapper();
        serverFilepath = ConfigUtils.getValue("server_filepath");
    }

    public ServerThread(Socket socket) {
        this.socket = socket;
        // 初始化流
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            log.error("Stream Init Error:{}", ex.getMessage());
        }

        // actionMap 客户端信息 -> 消息处理
        try {
            actionMap.put(ClientMsg.SEARCH_USER, (String[] args) ->  sendSuccessMsg(userMapper.getUserByUsername(args[0])));
            actionMap.put(ClientMsg.GET_ALL_USER, (String[] args) -> sendSuccessMsg(userMapper.getAllUser()));
            actionMap.put(ClientMsg.INSERT_USER, (String[] args) -> sendSuccessMsg(userMapper.insertUser(args[0], args[1], args[2])));
            actionMap.put(ClientMsg.DELETE_USER, (String[] args) -> sendSuccessMsg(userMapper.deleteUser(Integer.parseInt(args[0]))));
            actionMap.put(ClientMsg.UPDATE_USER, (String[] args) -> sendSuccessMsg(userMapper.updateUser(Integer.parseInt(args[0]), args[1], args[2], args[3])));
            actionMap.put(ClientMsg.LIKE_USER, (String[] args) -> sendSuccessMsg(userMapper.getUserByLike(args[0])));
            actionMap.put(ClientMsg.GET_ALL_DOC, (String[] args) -> sendSuccessMsg(docMapper.getAllDoc()));
//            actionMap.put(ClientMsg.INSERT_DOC, (String[] args) -> sendSuccessMsg(docDao.insertDoc(args[0], args[1], args[2])));
//            actionMap.put(ClientMsg.UPDATE_DOC, (String[] args) -> sendSuccessMsg(docDao.updateDoc(args[0])));
            actionMap.put(ClientMsg.EXIT, (String[] args) -> exit());
        } catch(ArrayIndexOutOfBoundsException ex) {
            log.error("actionMap Init Error:{}", ex.getMessage());
        }
    }

    private void sendSuccessMsg(Object msg){
        sendMsg(ServerMsg.SUCCESS, msg);
    }

    private void sendMsg(int type, Object msg){
        try {
            HashMap<String, Object> rmsg = new HashMap<>();
            rmsg.put("Type", type);
            rmsg.put("Data", msg);
            oos.writeObject(rmsg);
        } catch (IOException e) {
            log.error("sendMsg Error:{}", e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            int mod = ois.read();
            switch(mod) {
                case ClientMsg.DATABASES -> database();
                case ClientMsg.UPLOAD -> upload();
                case ClientMsg.DOWNLOAD -> download();
            }
        } catch(IOException e) {
            log.error("Server Thread Error:{}", e.getMessage());
        }
    }

    private void exit() {
        exit = true;
        try {
            sendMsg(ServerMsg.SUCCESS, ServerMsg.EXIT);
            oos.close();
            ois.close();
            socket.close();
        } catch(IOException ex) {
            log.error("Exit Error:{}", ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void database(){
        try{
            log.info("database operating...");
            do{
                HashMap<String, Object> msg = (HashMap<String, Object>) ois.readObject();
                String type = (String) msg.get("Type");
                String[] args = (String[]) msg.get("Data");
                actionMap.get(type).accept(args);
            }while(!exit);
            log.info("log out...");
        }catch(IOException | ClassNotFoundException e){
            log.error("database operating error:{}", e.getMessage());
        }
    }

    public void upload() {
        log.info("upload operating...");

        try {
            String uploadFileName = (String) ois.readObject();

            File file = new File(serverFilepath, uploadFileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while((len = ois.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            ois.close();
            fos.close();
        } catch (IOException | ClassNotFoundException ex) {
            log.error("upload operating error:{}", ex.getMessage());
        }
    }

    private void download() {
        log.info("download operating...");

        try {
            String downloadFileName = (String) ois.readObject();

            File file = new File(serverFilepath, downloadFileName);
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];

            oos.writeObject(in.available());
            oos.flush();

            int len;
            while((len = in.read(buffer)) != -1) {
                oos.write(buffer, 0, len);
                oos.flush();
            }
            in.close();
            oos.close();
            ois.close();
            log.info("download file successfully:{}", downloadFileName);
        } catch (IOException | ClassNotFoundException ex) {
            log.error("download operating error:{}", ex.getMessage());
        }
    }
}
