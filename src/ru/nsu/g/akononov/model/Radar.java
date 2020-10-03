package ru.nsu.g.akononov.model;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Radar {
    private static final int UPDATE_INTERVAL = 1000;
    private static final Long TTL = 5000L;

    HashMap<SocketAddress, Long> activeCopies = new HashMap<>();

    private MulticastSocket socket;
    private final String password;

    public Radar(String password, InetAddress groupAddress, int multicastPort) {
        this.password = password;
        try {
            socket = new MulticastSocket(multicastPort);
            socket.setSoTimeout(UPDATE_INTERVAL);

            SocketAddress socketAddress = new InetSocketAddress(groupAddress, multicastPort);

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netIf : Collections.list(nets)) {
                if(netIf.isUp() && !netIf.isLoopback() && netIf.supportsMulticast()) {
                    try {
                        socket.joinGroup(socketAddress, netIf);
                        System.out.println("Listening interface: " + netIf.getName() + "\n");
                    } catch (IOException e){
                        //System.out.println("Cannot join group: " +  e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() throws IOException {
        searchCopies();
        filterActiveCopies();
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
        System.out.println("Active copies list:");
        activeCopies.keySet().forEach(System.out::println);
        System.out.println();
    }
}