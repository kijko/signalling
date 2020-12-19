package pl.kijko.sectormanager;

import java.util.Objects;
import java.util.Optional;

final class SectorMessage {
    public final String sectorManagerId;
    public final String sectorId;
    public final SectorCommand sectorCommand;
    public final Direction direction;

    private SectorMessage(String sectorManagerId, String sectorId, SectorCommand sectorCommand, Direction direction) {
        this.sectorManagerId = sectorManagerId;
        this.sectorId = sectorId;
        this.sectorCommand = sectorCommand;
        this.direction = direction;
    }

    static SectorMessage toSector(String sectorManagerId, String sectorId, SectorCommand sectorCommand) {
        return new SectorMessage(sectorManagerId, sectorId, sectorCommand, Direction.MANAGER_TO_SECTOR);
    }

    public static Optional<SectorMessage> parseFromSector(String rawMessage) {
        String[] split = rawMessage.split("->");

        if (split.length < 2) {
            return Optional.empty();
        }

        String sectorId = split[0];
        String[] managerIdAndCommand = split[1].split("_");

        if (managerIdAndCommand.length < 2) {
            return Optional.empty();
        }

        String managerId = managerIdAndCommand[0];
        String rawCommand = managerIdAndCommand[1];

        Optional<SectorCommand> command = SectorCommand.fromString(rawCommand);

        if (command.isPresent()) {
            return Optional.of(
                    new SectorMessage(managerId, sectorId, command.get(), Direction.SECTOR_TO_MANAGER)
            );
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return direction == Direction.MANAGER_TO_SECTOR ?
                ( this.sectorManagerId + "->" + this.sectorId + "_" + sectorCommand.stringFormat ) :
                ( this.sectorId + "->" + this.sectorManagerId + "_" + sectorCommand.stringFormat );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorMessage that = (SectorMessage) o;
        return sectorManagerId.equals(that.sectorManagerId) &&
                sectorId.equals(that.sectorId) &&
                sectorCommand == that.sectorCommand &&
                direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectorManagerId, sectorId, sectorCommand, direction);
    }

    private enum Direction { MANAGER_TO_SECTOR, SECTOR_TO_MANAGER }

    public enum SectorCommand {
        HELP("HELP"), CANCEL("CANCEL"), GOT_IT("GOTIT");

        private String stringFormat;

        SectorCommand(String stringFormat) {
            this.stringFormat = stringFormat;
        }

        @Override
        public String toString() {
            return stringFormat;
        }

        public static Optional<SectorCommand> fromString(String str) {
            for (SectorCommand value : values()) {
                if (value.stringFormat.equals(str)) {
                    return Optional.of(value);
                }
            }

            return Optional.empty();
        }
    }
}
