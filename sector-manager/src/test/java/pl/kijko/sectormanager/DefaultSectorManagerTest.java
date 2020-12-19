package pl.kijko.sectormanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
public class DefaultSectorManagerTest {

    private String managerName = "A";
    private int numOfSectors = 3;
    private SectorMessageSender sender = mock(SectorMessageSender.class);
    private SectorMessageReceiver receiver = mock(SectorMessageReceiver.class);
    private SectorManager manager;

    @BeforeEach
    void setUp() {
        manager = new DefaultSectorManager(managerName, numOfSectors, sender, receiver);
    }

    @Test
    @DisplayName("should initialize sectors correctly")
    void getSectorsCorrectly() {
        // GIVEN-WHEN
        List<Sector> sectors = manager.getSectors();

        // THEN
        assertEquals(3, sectors.size());
        assertEquals("1", sectors.get(0).id);
        assertEquals("2", sectors.get(1).id);
        assertEquals("3", sectors.get(2).id);
        sectors.forEach(sector -> assertFalse(sector.isInNeed()));
    }

    @Test
    @DisplayName("should behave correctly on incoming help message")
    void behaveCorrectlyOnHelpMessage() {
        // GIVEN
        AtomicReference<Consumer<SectorMessage>> incomingMessageEvent = new AtomicReference<>();
        SectorMessageReceiver customReceiver = newValue -> {
            incomingMessageEvent.set(newValue);

            return CommunicationResult.SUCCESS;
        };

        manager = new DefaultSectorManager(managerName, numOfSectors, sender, customReceiver);
        SectorMessage incomingMessage = SectorMessage.toSector(managerName, "1", SectorMessage.SectorCommand.HELP);

        var onSectorChange = mock(Consumer.class);
        manager.addSectorChangeListener(onSectorChange);

        // WHEN
        incomingMessageEvent.get().accept(incomingMessage); // message received

        // THEN
        Sector sector = manager.getSectors().stream().filter(it -> it.id.equals("1")).findFirst().get();
        assertEquals("1", sector.id);
        assertTrue(sector.isInNeed());

        verify(receiver, times(1)).addMessageHandler(any());
        verify(onSectorChange, times(1)).accept(sector);
    }

    @Test
    @DisplayName("should behave correctly on incoming cancel message")
    void behaveCorrectlyOnCancelMessage() {
        // GIVEN
        AtomicReference<Consumer<SectorMessage>> incomingMessageEvent = new AtomicReference<>();
        SectorMessageReceiver customReceiver = newValue -> {
            incomingMessageEvent.set(newValue);

            return CommunicationResult.SUCCESS;
        };

        manager = new DefaultSectorManager(managerName, numOfSectors, sender, customReceiver);
        SectorMessage incomingMessage = SectorMessage.toSector(managerName, "3", SectorMessage.SectorCommand.CANCEL);

        var onSectorChange = mock(Consumer.class);
        manager.addSectorChangeListener(onSectorChange);

        Sector sectorNeedsHelp = manager.getSectors().stream().filter(sector -> sector.id.equals("3")).findFirst().get();
        sectorNeedsHelp.needsHelp();

        assertTrue(manager.getSectors().stream().filter(sector -> sector.id.equals("3")).findFirst().get().isInNeed());

        // WHEN
        incomingMessageEvent.get().accept(incomingMessage); // message received

        // THEN
        Sector sector = manager.getSectors().stream().filter(it -> it.id.equals("3")).findFirst().get();
        assertEquals("3", sector.id);
        assertFalse(sector.isInNeed());

        verify(receiver, times(1)).addMessageHandler(any());
        verify(onSectorChange, times(1)).accept(sector);
    }

    @Test
    @DisplayName("should send got-it message and set sector as resolved")
    void resolveSector() {
        // GIVEN
        Sector sectorInNeed = manager.getSectors().stream().filter(it -> it.id.equals("3")).findFirst().get();
        sectorInNeed.needsHelp();
        assertTrue(sectorInNeed.isInNeed());

        var onSectorChange = mock(Consumer.class);
        manager.addSectorChangeListener(onSectorChange);

        when(sender.send(any())).thenReturn(CommunicationResult.SUCCESS);

        // WHEN
        Sector modifiedSector = manager.resolve(sectorInNeed);

        // THEN
        assertSame(modifiedSector, sectorInNeed);
        assertFalse(sectorInNeed.isInNeed());

        verify(sender, times(1))
                .send(eq(SectorMessage.toSector(managerName, "3", SectorMessage.SectorCommand.GOT_IT)));
        verify(onSectorChange, times(1)).accept(sectorInNeed);
    }

    @Test
    @DisplayName("should not resolve sector because of sending error")
    void doNotResolve1() {
        // GIVEN
        Sector sectorInNeed = manager.getSectors().stream().filter(it -> it.id.equals("3")).findFirst().get();
        sectorInNeed.needsHelp();
        assertTrue(sectorInNeed.isInNeed());

        var onSectorChange = mock(Consumer.class);
        manager.addSectorChangeListener(onSectorChange);

        when(sender.send(any())).thenReturn(CommunicationResult.ERROR);

        // WHEN
        Sector modifiedSector = manager.resolve(sectorInNeed);

        // THEN
        assertSame(modifiedSector, sectorInNeed);
        assertTrue(sectorInNeed.isInNeed());

        verify(sender, times(1))
                .send(eq(SectorMessage.toSector(managerName, "3", SectorMessage.SectorCommand.GOT_IT)));
        verify(onSectorChange, times(0)).accept(sectorInNeed);
    }

    @Test
    @DisplayName("should not send got-it message - change nothing")
    void doNotResolve2() {
        // GIVEN
        Sector sectorInNeed = manager.getSectors().stream().filter(it -> it.id.equals("3")).findFirst().get();
        assertFalse(sectorInNeed.isInNeed());

        var onSectorChange = mock(Consumer.class);
        manager.addSectorChangeListener(onSectorChange);

        // WHEN
        Sector modifiedSector = manager.resolve(sectorInNeed);

        // THEN
        assertSame(modifiedSector, sectorInNeed);
        assertFalse(sectorInNeed.isInNeed());

        verify(sender, times(0))
                .send(eq(SectorMessage.toSector(managerName, "3", SectorMessage.SectorCommand.GOT_IT)));
        verify(onSectorChange, times(0)).accept(sectorInNeed);
    }
}
