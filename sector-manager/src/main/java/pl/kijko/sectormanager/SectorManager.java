package pl.kijko.sectormanager;

import java.util.List;
import java.util.function.Consumer;

public interface SectorManager {

    String getName();
    List<Sector> getSectors();
    Sector resolve(Sector sector);
    void addSectorChangeListener(Consumer<Sector> listener);

}
