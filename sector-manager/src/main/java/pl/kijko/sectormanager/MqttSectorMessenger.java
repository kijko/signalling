package pl.kijko.sectormanager;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

class MqttSectorMessenger implements SectorMessageSender, SectorMessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(MqttSectorMessenger.class);

    private final IMqttClient mqttClient;
    private final Predicate<SectorMessage>[] filters;

    MqttSectorMessenger(IMqttClient mqttClient, Predicate<SectorMessage>... filters) {
        this.mqttClient = mqttClient;
        this.filters = filters;

        try {
            mqttClient.connect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommunicationResult addMessageHandler(Consumer<SectorMessage> messageConsumer) {

        try {
            mqttClient.subscribe("help-request", 2, ((topic, message) -> {
                String rawMessage = new String(message.getPayload(), Charset.defaultCharset());
                Optional<SectorMessage> parsedMessage = SectorMessage.parseFromSector(rawMessage);

                if (parsedMessage.isPresent() && Stream.of(filters).allMatch(it -> it.test(parsedMessage.get()))) {
                    messageConsumer.accept(parsedMessage.get());
                }

                if (parsedMessage.isEmpty()) {
                    LOG.error("Received malformed message - " + rawMessage);
                }
            }));

            return CommunicationResult.SUCCESS;
        } catch (MqttException e) {
            LOG.error("Error during subscribing to help-request topic", e);

            return CommunicationResult.ERROR;
        }
    }

    @Override
    public CommunicationResult send(SectorMessage message) {
        try {
            mqttClient.publish("request-accepted", message.toString().getBytes(), 2, true);

            return CommunicationResult.SUCCESS;
        } catch (MqttException e) {
            LOG.error("Error during publishing message to mqtt", e);

            return CommunicationResult.ERROR;
        }
    }
}
