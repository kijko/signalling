package pl.kijko.sectormanager;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
public class MqttSectorMessengerTest {

    private IMqttClient mqttClient;
    private Predicate<SectorMessage> messageFilter;
    private MqttSectorMessenger messenger;

    @BeforeEach
    void setUp() {
        mqttClient = mock(IMqttClient.class);
        messageFilter = mock(Predicate.class);
        messenger = new MqttSectorMessenger(mqttClient, messageFilter);
    }

    @Test
    @DisplayName("should send got-it message")
    void sendMessageCorrectly() throws MqttException {
        // GIVEN
        SectorMessage message = SectorMessage.toSector("A", "2", SectorMessage.SectorCommand.GOT_IT);

        // WHEN
        CommunicationResult result = messenger.send(message);

        // THEN
        assertEquals(CommunicationResult.SUCCESS, result);
        verify(mqttClient, times(1))
                .publish("request-accepted", "A->2_GOTIT".getBytes(), 2, true);
    }

    @Test
    @DisplayName("should return error because of problem with mqtt")
    void returnError() throws MqttException {
        // GIVEN
        SectorMessage message = SectorMessage.toSector("A", "2", SectorMessage.SectorCommand.GOT_IT);

        doThrow(MqttException.class).when(mqttClient)
                .publish("request-accepted", "A->2_GOTIT".getBytes(), 2, true);

        // WHEN
        CommunicationResult result = messenger.send(message);

        // THEN
        assertEquals(CommunicationResult.ERROR, result);
    }

    @Test
    @DisplayName("should add handler for receiving message correctly")
    void addHandler() throws Exception {
        // GIVEN
        Consumer onMessageReceived = mock(Consumer.class);

        AtomicReference<IMqttMessageListener> mqttMessageListener = new AtomicReference<>();
        doAnswer(invocation -> {
            mqttMessageListener.set(invocation.getArgument(2, IMqttMessageListener.class));

            return null;
        }).when(mqttClient).subscribe(eq("help-request"), eq(2), any());

        when(messageFilter.test(any())).thenReturn(true);

        messenger.addMessageHandler(onMessageReceived);

        //WHEN
        mqttMessageListener.get().messageArrived("help-request", new MqttMessage("2->B_HELP".getBytes()));

        verify(messageFilter, times(1))
                .test(eq(SectorMessage.parseFromSector("2->B_HELP").get()));
        verify(onMessageReceived, times(1))
                .accept(eq(SectorMessage.parseFromSector("2->B_HELP").get()));
    }

}
