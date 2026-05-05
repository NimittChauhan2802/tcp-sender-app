
package com.example.tcpsender;

import java.io.OutputStream;
import java.net.Socket;

public class TcpSender {

    public static void sendPacket(String ip, int port, String packet) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(ip, port);
                OutputStream os = socket.getOutputStream();
                os.write((packet + "\r\n").getBytes());
                os.flush();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
