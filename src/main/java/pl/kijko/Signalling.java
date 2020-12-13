package pl.kijko;

import org.eclipse.paho.client.mqttv3.MqttException;
import pl.kijko.managers.Managers;
import pl.kijko.sectors.Sectors;

public class Signalling {

    public static void main(String[] args) throws MqttException {
        Managers.main(new String[]{});
        Sectors.main(new String[]{});
    }

}
