package ru.nsu.g.akononov.model;

import java.io.IOException;
import java.net.*;
import java.util.Date;

public class Beacon {
    private static final long INTERVAL = 1000L;
    private long lastSignalTime = 0;

    private DatagramSocket socket;
    private DatagramPacket packet;

    public Beacon(String password, InetAddress groupAddress, int multicastPort) {
        try {
            socket = new DatagramSocket();
            packet = new DatagramPacket(password.getBytes(), password.getBytes().length, groupAddress, multicastPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void signal() throws IOException {
        long now = new Date().getTime();
        if (now - lastSignalTime > INTERVAL) {
            socket.send(packet);
            lastSignalTime = now;
        }
    }
}