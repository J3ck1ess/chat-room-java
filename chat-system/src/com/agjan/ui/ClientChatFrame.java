package com.agjan.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientChatFrame extends JFrame {
    public JTextArea smsContent = new JTextArea(23, 50);
    private JTextArea smsSend = new JTextArea(4, 40);
    public JList<String> onLineUsers = new JList<>();
    private JButton sendBn = new JButton("发送");
    private Socket socket;

    public ClientChatFrame() {
        initView();
        this.setVisible(true);
    }

    public ClientChatFrame(String nickname, Socket socket) {
        this();
        this.setTitle(nickname + "的聊天窗口");
        this.socket = socket;

        new ClientReaderThread(socket, this).start();

    }

    private void initView() {
        this.setSize(700, 600);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口，退出程序
        this.setLocationRelativeTo(null); // 窗口居中

        // 设置窗口背景色
        this.getContentPane().setBackground(new Color(0xf0, 0xf0, 0xf0));

        // 设置字体
        Font font = new Font("SimKai", Font.PLAIN, 14);

        // 消息内容框
        smsContent.setFont(font);
        smsContent.setBackground(new Color(0xdd, 0xdd, 0xdd));
        smsContent.setEditable(false);

        // 发送消息框
        smsSend.setFont(font);
        smsSend.setWrapStyleWord(true);
        smsSend.setLineWrap(true);

        // 在线用户列表
        onLineUsers.setFont(font);
        onLineUsers.setFixedCellWidth(120);
        onLineUsers.setVisibleRowCount(13);

        // 创建底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(0xf0, 0xf0, 0xf0));

        // 消息输入框
        JScrollPane smsSendScrollPane = new JScrollPane(smsSend);
        smsSendScrollPane.setBorder(BorderFactory.createEmptyBorder());
        smsSendScrollPane.setPreferredSize(new Dimension(500, 50));

        // 发送按钮
        sendBn.setFont(font);
        sendBn.setBackground(Color.decode("#009688"));
        sendBn.setForeground(Color.WHITE);

        // 给发送按钮绑定一个事件监听器
        sendBn.addActionListener(e -> {
            String msg = smsSend.getText();
            sendMsgToServer(msg);
        });

        // 按钮面板
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btns.setBackground(new Color(0xf0, 0xf0, 0xf0));
        btns.add(sendBn);

        // 添加组件
        bottomPanel.add(smsSendScrollPane, BorderLayout.CENTER);
        bottomPanel.add(btns, BorderLayout.EAST);

        // 用户列表面板
        JScrollPane userListScrollPane = new JScrollPane(onLineUsers);
        userListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        userListScrollPane.setPreferredSize(new Dimension(120, 500));

        // 中心消息面板
        JScrollPane smsContentScrollPane = new JScrollPane(smsContent);
        smsContentScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 添加所有组件
        this.add(smsContentScrollPane, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.add(userListScrollPane, BorderLayout.EAST);
    }

    private void sendMsgToServer(String msg) {
        if (msg == null || msg.trim().isEmpty()) return;

        try {
            // 1. 创建数据输出流
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            // 2. 判断是私聊还是群聊
            if (msg.startsWith("@")) {
                // 私聊
                int index = msg.indexOf(" ");

                if (index > 1) {
                    String toUser = msg.substring(1, index);
                    String privateMsg = msg.substring(index + 1);

                    dos.writeInt(messageType.PRIVATE_MSG);
                    dos.writeUTF(toUser);
                    dos.writeUTF(privateMsg);
                } else {
                    // 格式错误
                    JOptionPane.showMessageDialog(this, "请输入正确的格式：@用户名 消息内容");
                    return;
                }
            } else {
                // 群聊
                dos.writeInt(messageType.GROUP_MSG);
                dos.writeUTF(msg);
            }
            dos.flush();
            smsSend.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new ClientChatFrame();
    }

    public void updateOnLineUsers(String[] names) {
        onLineUsers.setListData(names);
    }

    public void setMsgToWin(String msg) {
        SwingUtilities.invokeLater(() -> {
            if (msg.contains("[私聊]")) {
                smsContent.append("🔒 " + msg + "\n");
            } else {
                smsContent.append(msg + "\n");
            }
            smsContent.setCaretPosition(smsContent.getDocument().getLength());
        });
    }
}

