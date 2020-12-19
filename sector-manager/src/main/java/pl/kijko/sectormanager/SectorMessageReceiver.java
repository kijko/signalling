package pl.kijko.sectormanager;

import java.util.function.Consumer;

interface SectorMessageReceiver {

    void addMessageHandler(Consumer<SectorMessage> messageConsumer);

}
