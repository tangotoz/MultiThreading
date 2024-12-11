package com.tango.experiment.client.service;

import com.tango.experiment.common.ClientMsg;
import com.tango.experiment.pojo.Doc;
import com.tango.experiment.pojo.User;
import com.tango.experiment.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
public class UserAndDocumentService {
    private static String serverIp;
    private static int serverPort;
    private static Socket client;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static boolean isConnected = false;

    private static boolean connectServer() {
        try {
            log.info("trying to connect server...");
            client = new Socket(serverIp, serverPort);
            client.setSoTimeout(1000);
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());
            oos.write(ClientMsg.DATABASES);
            isConnected = true;
        } catch (IOException ex) {
            log.error("connect server error:{}", ex.getMessage());
        }
        return isConnected;
    }

    public static void init() {
        serverIp = ConfigUtils.getValue("server_ip");
        serverPort = Integer.parseInt(Objects.requireNonNull(ConfigUtils.getValue("server_port")));
        connectServer();
    }

    private static void writeMsg(HashMap<String, Object> msg) throws IOException {
        oos.writeObject(msg);
        oos.flush();
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, Object> readMsg() throws IOException, ClassNotFoundException {
        return (HashMap<String, Object>) ois.readObject();
    }

    private static Object writeAndRead(String type, Object obj) throws IOException, ClassNotFoundException {
        HashMap<String, Object> msg = new HashMap<>();
        msg.put("Type", type);
        msg.put("Data", obj);
        writeMsg(msg);
        HashMap<String, Object> rMsg = readMsg();
        if ((int) rMsg.get("Type") == 1) {
            return rMsg.get("Data");
        }
        return null;
    }

    public static User searchUser(String username) throws IOException, ClassNotFoundException {
        String[] args = {username};
        return (User) writeAndRead(ClientMsg.SEARCH_USER, args);
    }

    public static List<User> getAllUser() throws IOException, ClassNotFoundException {
        String[] args = {};
        return (List<User>) writeAndRead(ClientMsg.GET_ALL_USER, args);
    }

    public static List<User> getUserByLike(String keyword) throws IOException, ClassNotFoundException {
        String[] args = {keyword};
        return (List<User>) writeAndRead(ClientMsg.LIKE_USER, args);
    }

    public static boolean insertUser(String username, String password, String role) throws IOException, ClassNotFoundException {
        String[] args = {username, password, role};
        return ((Integer) writeAndRead(ClientMsg.INSERT_USER, args) != 0);
    }

    public static boolean deleteUser(int userId) throws IOException, ClassNotFoundException {
        String[] args = {String.valueOf(userId)};
        return ((Integer) writeAndRead(ClientMsg.DELETE_USER, args) != 0);
    }

    public static boolean updateUser(String userId, String username, String password, String role) throws IOException, ClassNotFoundException {
        String[] args = {userId, username, password, role};
        return ((Integer) writeAndRead(ClientMsg.UPDATE_USER, args) != 0);
    }

    public static List<Doc> getAllDoc() throws IOException, ClassNotFoundException {
        String[] args = {};
        return ((List<Doc>) writeAndRead(ClientMsg.GET_ALL_DOC, args));
    }

    public static boolean insertDoc(String fileName, String description) throws IOException, ClassNotFoundException {
        String[] args = {fileName, description};
        return ((Integer) writeAndRead(ClientMsg.INSERT_DOC, args) != 0);
    }

    public static boolean updateDoc(String fileName) throws IOException, ClassNotFoundException {
        String[] args = {fileName};
        return ((Integer) writeAndRead(ClientMsg.UPDATE_DOC, args) != 0);
    }

    public static List<Doc> searchDoc(String keyword) throws IOException, ClassNotFoundException {
        String[] args = {keyword};
        return ((List<Doc>) writeAndRead(ClientMsg.SEARCH_DOC, args));
    }
}
