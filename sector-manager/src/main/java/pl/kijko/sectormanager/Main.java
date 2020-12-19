package pl.kijko.sectormanager;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Predicate;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws MqttException, IOException {
        if (args.length < 3) {
            throw new IllegalArgumentException("server uri, manager name, number of sectors args required");
        }

        String serverURI = args[0];
        String managerName = args[1];
        int numOfSectors = Integer.parseInt(args[2]);

        IMqttClient mqttClient = new MqttClient(serverURI, managerName + "-client");

        Predicate<SectorMessage> messageFilter = sectorMessage -> sectorMessage.sectorManagerId.equals(managerName);
        MqttSectorMessenger messenger = new MqttSectorMessenger(mqttClient, messageFilter);
        SectorManager sectorManager = new DefaultSectorManager(managerName, numOfSectors, messenger, messenger);

        sectorManager.addSectorChangeListener(sector -> {
            sectorManager.getSectors().forEach(it -> LOG.info(it.toString()));
        });

        sectorManager.getSectors().forEach(it -> LOG.info(it.toString()));

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        String input = "";
        while (!input.equalsIgnoreCase("n")) {
            input = reader.readLine();

            if (input.equals("1")) {
                sectorManager.getSectors().forEach(it -> {
                    if (it.id.equals("1")) {
                        sectorManager.resolve(it);
                    }
                });
            }
        }
    }

}
