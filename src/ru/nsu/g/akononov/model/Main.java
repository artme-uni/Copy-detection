package ru.nsu.g.akononov.model;

import java.io.IOException;
import java.net.InetAddress;

class Main {
    private static final int PORT = 18201;
    private static final String ADDRESS = "224.0.0.1";
    private static final String PASSWORD = "hummingbird";

    public static void main(String[] args) {

        try {
            final InetAddress inetAddress = InetAddress.getByName(ADDRESS);
            Beacon beacon = new Beacon(PASSWORD, inetAddress, PORT);
            Radar radar = new Radar(PASSWORD, inetAddress, PORT);

            while (true){
                beacon.signal();
                radar.listen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

