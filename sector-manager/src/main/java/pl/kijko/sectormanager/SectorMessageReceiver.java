package pl.kijko.sectormanager;

import java.util.function.Consumer;

interface SectorMessageReceiver {

    void onMessageReceived(Consumer<SectorMessage> messageConsumer);

}
