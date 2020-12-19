package pl.kijko.sectors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Sectors {
    private static final Logger LOG = LoggerFactory.getLogger(Sectors.class);

    public static void main(String[] args) throws MqttException, IOException {
        IMqttClient mqttClient = new MqttClient("tcp://localhost:1883", "sectorsClient");
        mqttClient.connect();

        mqttClient.subscribe("request-accepted", (topic, message) -> {
            LOG.info(new String(message.getPayload(), Charset.defaultCharset()));
        });


        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        String input = "";
        while (!input.equalsIgnoreCase("n")) {
            input = reader.readLine();

            mqttClient.publish("help-request", input.getBytes(), 2, true);
        }


    }

}
