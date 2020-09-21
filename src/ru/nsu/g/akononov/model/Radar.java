package ru.nsu.g.akononov.model;

import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Radar extends Thread {
    private static final int INTERVAL = 1000;
    private static final Long TTL = 10000L;

    HashMap<SocketAddress, Long> activeCopies = new HashMap<>();

    private MulticastSocket socket;
    private final String password;

    public Radar(String password, InetAddress groupAddress, int multicastPort) {
        this.password = password;
        try {
            socket = new MulticastSocket(multicastPort);
            socket.joinGroup(groupAddress);
            socket.setSoTimeout(INTERVAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() throws IOException {
        searchCopies();
        filterActiveCopies();
    }


    @Override
    public void run() {
        while (true) {
            try {
                searchCopies();
            } catch (IOException e) {
                e.printStackTrace();
            }

            filterActiveCopies();
        }
    }

    private void searchCopies() throws IOException {
        byte[] buf = new byte[password.getBytes().length];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException ignored){}

        String message = new String(packet.getData(), 0, packet.getLength());

        if (message.equals(password)) {
            SocketAddress socketAddress = packet.getSocketAddress();
            if (!activeCopies.containsKey(socketAddress)) {
                activeCopies.put(packet.getSocketAddress(), new Date().getTime());
                printActiveCopies();
            } else {
                activeCopies.put(packet.getSocketAddress(), new Date().getTime());
            }
        }
    }

    private void filterActiveCopies() {
        long currentTime = new Date().getTime();

        List<SocketAddress> unreachableCopies = activeCopies.entrySet().stream()
                .filter(e -> ((currentTime - e.getValue())) >= TTL)
                .map(Map.Entry::getKey).collect(Collectors.toList());

        if (!unreachableCopies.isEmpty()) {
            activeCopies.keySet().removeAll(unreachableCopies);
            printActiveCopies();
        }
    }

    private void printActiveCopies() {
        System.out.println("Active program copies list:");
        activeCopies.keySet().forEach(System.out::println);
        System.out.println();
    }
}
