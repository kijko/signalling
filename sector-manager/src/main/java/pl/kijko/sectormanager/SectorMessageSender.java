package pl.kijko.sectormanager;

interface SectorMessageSender {

    void send(String senderId, SectorMessage message);

}
