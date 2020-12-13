package pl.kijko.sectors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sectors {
    private static final Logger LOG = LoggerFactory.getLogger(Sectors.class);

    public static void main(String[] args) throws MqttException {
        IMqttClient mqttClient = new MqttClient("tcp://localhost:1883", "sectorsClient");
        mqttClient.connect();

        LOG.info("Sectors client connected");
    }

}
