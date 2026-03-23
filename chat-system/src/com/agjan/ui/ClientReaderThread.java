package com.agjan.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientReaderThread extends Thread {
    private Socket socket;
    private ClientChatFrame win;
    private DataInputStream dis;

    public ClientReaderThread(Socket socket, ClientChatFrame win) {
        this.socket = socket;
        this.win = win;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();
                switch (type) {
                    case messageType.ONLINE_LIST:
                        updateClientOnLineUserList();
                        break;

                    case messageType.GROUP_MSG:
                        getMsgToWin();
                        break;

                    case messageType.SYSTEM_MSG:
                        String sysMsg = dis.readUTF();
                        win.setMsgToWin(sysMsg);
                        break;

                    case messageType.PRIVATE_MSG:
                        String privateMsg = dis.readUTF();
                        win.setMsgToWin(privateMsg);
                        break;

                    default:
                        System.out.println("客户端收到消息类型:" + type +
                                " (" + messageType.getName(type) + ")");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMsgToWin() throws Exception {
        String msg = dis.readUTF();
        win.setMsgToWin(msg);
    }

    private void updateClientOnLineUserList() throws Exception {
        int count = dis.readInt();

        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            String nickname = dis.readUTF();
            names[i] = nickname;
        }

        win.updateOnLineUsers(names);
    }
}
