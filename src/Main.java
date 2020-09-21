import ru.nsu.g.akononov.model.Radar;
import ru.nsu.g.akononov.model.Beacon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

class Main {
    private static final int PORT = 18201;
    private static final String ADDRESS = "224.0.0.1";
    private static final String PASSWORD = "hummingbird";

    public static void main(String[] args) {

        try {
            InetAddress inetAddress = InetAddress.getByName(ADDRESS);

            Beacon beacon = new Beacon(PASSWORD, inetAddress, PORT);
            Radar radar = new Radar(PASSWORD, inetAddress, PORT);

            beacon.start();
            radar.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

