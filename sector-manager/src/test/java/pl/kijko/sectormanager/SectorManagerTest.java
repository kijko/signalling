package pl.kijko.sectormanager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class SectorManagerTest {

    @Test
    @DisplayName("should initialize sectors correctly")
    void getAllSectors1() {
        SectorManager sectorManager = new SectorManager(
                "A",
                3,
                mock(SectorMessageSender.class),
                mock(SectorMessageReceiver.class),
                sector -> {}
        );

        Set<Sector> allSectors = sectorManager.getAllSectors();

        assertEquals(3, allSectors.size());
        allSectors.forEach(sector -> {
            boolean hasCorrectId = sector.id.equals("1") ||
                    sector.id.equals("2") ||
                    sector.id.equals("3");

            assertTrue(hasCorrectId);
            assertFalse(sector.needsHelp);
        });
    }

    @Test
    @DisplayName("should send got-it message and return correct state")
    void declareHelpForSector1() {
        Sector sectorInNeed = new Sector("1", true);
        SectorMessageSender sender = mock(SectorMessageSender.class);
        String sectorManagerId = "B";
        SectorManager sectorManager = new SectorManager(
                sectorManagerId,
                3,
                sender,
                mock(SectorMessageReceiver.class),
                sector -> {}
        );

        Sector modifiedSector = sectorManager.declareHelpForSector(sectorInNeed);

        assertNotSame(modifiedSector, sectorInNeed);
        assertEquals(sectorInNeed.id, modifiedSector.id);
        assertFalse(modifiedSector.needsHelp);

        verify(sender, times(1)).send(
                eq(sectorManagerId),
                eq(new SectorMessage(sectorInNeed.id, SectorCommand.GOT_IT))
        );
    }

    @Test
    @DisplayName("should not send got-it message and return correct state")
    void declareHelpForSector2() {
        Sector sectorInNeed = new Sector("1", false);
        SectorMessageSender sender = mock(SectorMessageSender.class);
        String sectorManagerId = "B";
        SectorManager sectorManager = new SectorManager(sectorManagerId, 3, sender, mock(SectorMessageReceiver.class), sector -> {});

        Sector modifiedSector = sectorManager.declareHelpForSector(sectorInNeed);

        assertNotSame(modifiedSector, sectorInNeed);
        assertEquals(sectorInNeed.id, modifiedSector.id);
        assertFalse(modifiedSector.needsHelp);

        verify(sender, times(0)).send(
                eq(sectorManagerId),
                eq(new SectorMessage(sectorInNeed.id, SectorCommand.GOT_IT))
        );
    }

    @Test
    @DisplayName("should change correctly sector state on HELP message")
    void sectorStateChanged1() {
        String sectorManagerId = "B";
        SectorMessageReceiver receiver = messageConsumer -> messageConsumer.accept(new SectorMessage("1", SectorCommand.HELP));
        AtomicReference<Sector> sectorFromCallback = new AtomicReference<>();
        SectorManager sectorManager = new SectorManager(
                sectorManagerId,
                3,
                mock(SectorMessageSender.class),
                receiver,
                sectorFromCallback::set
        );

        Sector modifiedSector = sectorManager.getAllSectors().stream().filter(it -> it.id.equals("1")).findFirst().get();
        assertSame(modifiedSector, sectorFromCallback.get());

        assertTrue(modifiedSector.needsHelp);
    }

    @Test
    @DisplayName("should change correctly sector state on CANCEL message")
    void sectorStateChanged2() {
        String sectorManagerId = "B";
        SectorMessageReceiver receiver = messageConsumer -> {
            messageConsumer.accept(new SectorMessage("1", SectorCommand.HELP));
            messageConsumer.accept(new SectorMessage("1", SectorCommand.CANCEL));
        };
        AtomicReference<Sector> sectorFromCallback = new AtomicReference<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        SectorManager sectorManager = new SectorManager(
                sectorManagerId,
                3,
                mock(SectorMessageSender.class),
                receiver,
                sector -> {
                    sectorFromCallback.set(sector);
                    atomicInteger.incrementAndGet();
                }
        );

        Sector modifiedSector = sectorManager.getAllSectors().stream().filter(it -> it.id.equals("1")).findFirst().get();
        assertSame(modifiedSector, sectorFromCallback.get());
        assertEquals(2, atomicInteger.get());

        assertFalse(modifiedSector.needsHelp);
    }

}

