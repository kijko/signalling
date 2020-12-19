package pl.kijko.sectormanager;

import java.util.Objects;

public final class Sector {

    public final String id;
    private boolean needsHelp;

    public Sector(String id) {
        this.id = id;
        this.needsHelp = false;
    }

    public boolean isInNeed() {
        return this.needsHelp;
    }

    public synchronized void needsHelp() {
        this.needsHelp = true;
    }

    public synchronized void resolved() {
        this.needsHelp = false;
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
