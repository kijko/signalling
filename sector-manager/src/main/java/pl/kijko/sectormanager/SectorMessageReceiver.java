package pl.kijko.sectormanager;

import java.util.function.Consumer;

interface SectorMessageReceiver {

    CommunicationResult addMessageHandler(Consumer<SectorMessage> messageConsumer);

}
