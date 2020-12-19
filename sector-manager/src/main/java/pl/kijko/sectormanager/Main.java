package pl.kijko.sectormanager;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws MqttException {
        IMqttClient mqttClient = new MqttClient("tcp://localhost:1883", "managersClient");
        mqttClient.connect();

        LOG.info("Managers client connected");

//        SectorManager x = new QualitySectorManager(
//                4,
//                new MqttSectorMessagesReceiver(mqttClient),
//                new MqttSectorMessagesSender(mqttClient)
//        );
    }

}
