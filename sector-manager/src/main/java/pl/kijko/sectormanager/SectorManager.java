package pl.kijko.sectormanager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class SectorManager {

    private Set<Sector> sectors;
    private final String managerId;
    private final SectorMessageSender msgSender;
    private Consumer<Sector> onSectorChanged;

    public SectorManager(
            String managerId,
            int numOfSectors,
            SectorMessageSender msgSender,
            SectorMessageReceiver msgReceiver,
            Consumer<Sector> onSectorChanged
    ) {
        this.managerId = managerId;
        this.msgSender = msgSender;
        this.onSectorChanged = onSectorChanged;

        sectors = new HashSet<>();

        for (int i = 1; i <= numOfSectors; i++) // todo intstream
            sectors.add(new Sector(String.valueOf(i), false));

        msgReceiver.onMessageReceived(this::handleSectorMessage);
    }

    private synchronized void handleSectorMessage(SectorMessage msg) {
        if (msg.sectorCommand == SectorCommand.HELP) {
            Optional<Sector> sector = sectors.stream().filter(it -> it.id.equals(msg.sectorId)).findFirst();

            if (sector.isPresent()) {
                Set<Sector> newSectors = sectors.stream().filter(it -> !it.id.equals(msg.sectorId)).collect(toSet());
                Sector newSector = new Sector(msg.sectorId, true);
                newSectors.add(newSector);

                sectors = newSectors;

                if (onSectorChanged != null) {
                    onSectorChanged.accept(newSector);
                }
            }
        }

        if (msg.sectorCommand == SectorCommand.CANCEL) {
            Optional<Sector> sector = sectors.stream().filter(it -> it.id.equals(msg.sectorId)).findFirst();

            if (sector.isPresent()) {
                Set<Sector> newSectors = sectors.stream().filter(it -> !it.id.equals(msg.sectorId)).collect(toSet());
                Sector newSector = new Sector(msg.sectorId, false);
                newSectors.add(newSector);

                sectors = newSectors;

                if (onSectorChanged != null) {
                    onSectorChanged.accept(newSector);
                }
            }
        }
    }

    public Set<Sector> getAllSectors() {
        return new HashSet<>(sectors);
    }

    public Sector declareHelpForSector(Sector sector) {
        if (sector.needsHelp)
            msgSender.send(managerId, new SectorMessage(sector.id, SectorCommand.GOT_IT));

        return new Sector(sector.id, false);
    }

    public void onSectorStateChanged(Consumer<Sector> onSectorChanged) {
        this.onSectorChanged = onSectorChanged;
    }

}
