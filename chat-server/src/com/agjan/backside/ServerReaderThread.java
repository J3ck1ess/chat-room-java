package com.agjan.backside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;

public class ServerReaderThread extends Thread {
    private Socket socket;

    public ServerReaderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 接收客户端发送来的数据
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                int type = dis.readInt(); // 1:代表登录消息 2:代表群聊消息

                switch (type) {
                    case messageType.LOGIN:
                        // 1. 获取昵称
                        String nickname = dis.readUTF();
                        Server.onLineSockets.put(socket, nickname);
                        // 2. 添加到在线列表
                        updateOnLineUserList();
                        sendSystemMsgToALL(nickname + " 加入聊天室");
                        break;

                    case messageType.GROUP_MSG:
                        String msg = dis.readUTF();
                        sentMsgToAll(msg);
                        break;

                    case messageType.PRIVATE_MSG:
                        // 给其中一个在线用户发消息
                        String toUser = dis.readUTF();
                        String privateMsg = dis.readUTF();
                        sendMsgToPrivate(toUser, privateMsg);
                        break;

                    default:
                        System.out.println("服务端收到消息类型:" + type +
                                " (" + messageType.getName(type) + ")");
                        break;
                }
            }
        } catch (EOFException e) {
            System.out.println("客户端正常断开连接");
        } catch (SocketException e) {
            System.out.println("连接被重置");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            String name = Server.onLineSockets.get(socket);
            Server.onLineSockets.remove(socket);
            updateOnLineUserList();
            if (name != null) {
                sendSystemMsgToALL(name + " 离开聊天室");
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    // 给指定在线用户私聊消息
    private void sendMsgToPrivate(String toUser, String privateMsg) {
        String fromUser = Server.onLineSockets.get(socket);

        for (Socket sk : Server.onLineSockets.keySet()) {
            String name = Server.onLineSockets.get(sk);

            if (name.equals(toUser)) {
                try {
                    DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                    dos.writeInt(messageType.PRIVATE_MSG);
                    dos.writeUTF("[私聊] " + fromUser + " → 你: " + privateMsg + "\r\n");

                    dos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            DataOutputStream dosSelf = new DataOutputStream(socket.getOutputStream());

            dosSelf.writeInt(messageType.PRIVATE_MSG);
            dosSelf.writeUTF("[私聊] 你 → " + toUser + ": " + privateMsg + "\r\n");

            dosSelf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void sendSystemMsgToALL(String msg) {
        try {
            for (Socket sk : Server.onLineSockets.keySet()) {
                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                dos.writeInt(messageType.SYSTEM_MSG);
                dos.writeUTF("[系统] " + msg);
                dos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sentMsgToAll(String msg) {
        StringBuilder sb = new StringBuilder();
        String name = Server.onLineSockets.get(socket);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE a");
        String nowStr = dtf.format(now);

        String msgResult = sb.append(name)
                .append(" ")
                .append(nowStr)
                .append("\r\n")
                .append(msg)
                .append("\r\n").toString();

        for (Socket sk : Server.onLineSockets.keySet()) {
            try {
                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                dos.writeInt(messageType.GROUP_MSG);
                dos.writeUTF(msgResult);
                dos.flush();
            } catch (IOException e) {
                // 客户端挂了的时候移除管道
                Server.onLineSockets.remove(sk);
            }
        }
    }

    private void updateOnLineUserList() {
        Collection<String> onLineUsers = Server.onLineSockets.values();
        for (Socket sk : Server.onLineSockets.keySet()) {
            try {
                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                dos.writeInt(messageType.ONLINE_LIST); // 3:代表在线人数列表信息
                dos.writeInt(onLineUsers.size());
                for (String onLineUser : onLineUsers) {
                    dos.writeUTF(onLineUser);
                }
                dos.flush();
            } catch (IOException e) {
                // 客户端挂了的时候移除管道
                Server.onLineSockets.remove(sk);
            }
        }

    }

}
