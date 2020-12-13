package pl.kijko.sectormanager;

import java.util.Objects;

public final class Sector {

    public final String id;
    public final boolean needsHelp;

    public Sector(String id, boolean needsHelp) {
        this.id = id;
        this.needsHelp = needsHelp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sector sector = (Sector) o;
        return needsHelp == sector.needsHelp &&
                id.equals(sector.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, needsHelp);
    }
}
