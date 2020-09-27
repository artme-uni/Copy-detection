package ru.nsu.g.akononov.model;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

public class Beacon {
    private static final long INTERVAL = 1000L;
    private long lastSignalTime = 0;

    private MulticastSocket socket;
    private DatagramPacket packet;

    public Beacon(String password, InetAddress groupAddress, int multicastPort) {
        try {
            socket = new MulticastSocket();
            packet = new DatagramPacket(password.getBytes(), password.getBytes().length, groupAddress, multicastPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signal() throws IOException {
        long now = new Date().getTime();
        if (now - lastSignalTime > INTERVAL) {

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netIf : Collections.list(nets)) {
                if (netIf.isUp() && !netIf.isLoopback()) {
                    socket.setNetworkInterface(netIf);
                    socket.send(packet);
                }
            }
            lastSignalTime = now;
        }
    }
}