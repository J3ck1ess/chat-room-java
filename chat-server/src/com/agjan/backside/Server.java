package com.agjan.backside;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static final Map<Socket, String> onLineSockets = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Constant.PORT);
            System.out.println("服务器启动成功...");
            while (true) {
                Socket socket = serverSocket.accept();
                new ServerReaderThread(socket).start();
                System.out.println("客户端连接成功...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
