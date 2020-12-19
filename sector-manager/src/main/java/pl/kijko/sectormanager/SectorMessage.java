package pl.kijko.sectormanager;

import java.util.Objects;

final class SectorMessage {
    public final String sectorManagerId;
    public final String sectorId;
    public final SectorCommand sectorCommand;

    SectorMessage(String sectorManagerId, String sectorId, SectorCommand sectorCommand) {
        this.sectorManagerId = sectorManagerId;
        this.sectorId = sectorId;
        this.sectorCommand = sectorCommand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorMessage that = (SectorMessage) o;
        return sectorManagerId.equals(that.sectorManagerId) &&
                sectorId.equals(that.sectorId) &&
                sectorCommand == that.sectorCommand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectorManagerId, sectorId, sectorCommand);
    }
}
