package com.tango.server.service;

import com.tango.experiment.server.service.ServerThread;
import com.tango.experiment.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class Server {
    private static ServerSocket server;
    private static int port;
    private static int backlog;

    static {
        port = Integer.parseInt(Objects.requireNonNull(ConfigUtils.getValue("server_port")));
        backlog = Integer.parseInt(Objects.requireNonNull(ConfigUtils.getValue("server_backlog")));
    }


    public Server() {
    }

    public void start() {
        try{

            server = new ServerSocket(port, backlog); //端口号 8080, 最大连接数 100
            while(true){
                Socket socket = server.accept();
                new ServerThread(socket).start();
            }
        }catch(IOException e){
            log.error("server start error:{}", e.getMessage());
        }
    }
}
