package pl.kijko;

import org.eclipse.paho.client.mqttv3.MqttException;
import pl.kijko.sectormanager.Main;
import pl.kijko.sectors.Sectors;

import java.io.IOException;

public class Signalling {

    public static void main(String[] args) throws MqttException, IOException {
        Main.main(new String[]{});
        Sectors.main(new String[]{});
    }

}
