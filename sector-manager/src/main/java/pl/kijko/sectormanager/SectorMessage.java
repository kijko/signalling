package pl.kijko.sectormanager;

import java.util.Objects;

final class SectorMessage {
    public final String sectorId;
    public final SectorCommand sectorCommand;

    SectorMessage(String sectorId, SectorCommand sectorCommand) {
        this.sectorId = sectorId;
        this.sectorCommand = sectorCommand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorMessage that = (SectorMessage) o;
        return sectorId.equals(that.sectorId) &&
                sectorCommand == that.sectorCommand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectorId, sectorCommand);
    }
}
