package pl.kijko.sectormanager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

class DefaultSectorManager implements SectorManager {

    private final String managerName;
    private final List<Sector> sectors;
    private final SectorMessageSender sender;
    private final Set<Consumer<Sector>> sectorListeners;

    DefaultSectorManager(String managerName,
                         int numOfSectors,
                         SectorMessageSender sender,
                         SectorMessageReceiver receiver) {
        this.managerName = managerName;
        this.sectors = createSectors(numOfSectors);
        this.sectorListeners = new HashSet<>();
        this.sender = sender;

        receiver.addMessageHandler(this::handleSectorMessage);
    }

    private List<Sector> createSectors(int numOfSectors) {
        return IntStream.range(1, numOfSectors + 1)
                .mapToObj(num -> new Sector(String.valueOf(num)))
                .sorted(Comparator.comparing(o -> o.id))
                .collect(toList());
    }

    private Optional<Sector> getSector(String sectorId) {
        return this.sectors.stream()
                .filter(it -> it.id.equals(sectorId))
                .findFirst();
    }

    private synchronized void handleSectorMessage(SectorMessage msg) {
        getSector(msg.sectorId).ifPresent(sector -> {
            switch (msg.sectorCommand) {
                case HELP:
                    sector.needsHelp();
                    sectorListeners.forEach(listener -> listener.accept(sector));

                    break;
                case CANCEL:
                    sector.resolved();
                    sectorListeners.forEach(listener -> listener.accept(sector));

                    break;
            }
        });
    }

    @Override
    public String getName() {
        return managerName;
    }

    @Override
    public List<Sector> getSectors() {
        return new ArrayList<>(sectors);
    }

    @Override
    public Sector resolve(Sector sector) {
        if (sector.isInNeed()) {
            sector.resolved();
            sender.send(new SectorMessage(getName(), sector.id, SectorCommand.GOT_IT));
            sectorListeners.forEach(listener -> listener.accept(sector));
        }

        return sector;
    }

    @Override
    public void addSectorChangeListener(Consumer<Sector> listener) {
        this.sectorListeners.add(listener);
    }
}
