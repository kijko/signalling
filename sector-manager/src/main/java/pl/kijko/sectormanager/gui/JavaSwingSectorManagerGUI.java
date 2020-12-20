package pl.kijko.sectormanager.gui;

import pl.kijko.sectormanager.Sector;
import pl.kijko.sectormanager.SectorManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JavaSwingSectorManagerGUI implements SectorManagerGUI {

    private final SectorManager sectorManager;
    private final JFrame frame;
    private final List<JavaSwingSectorButton> sectorButtons;

    public JavaSwingSectorManagerGUI(SectorManager sectorManager) {
        this.sectorManager = sectorManager;

        this.frame = new JFrame("Sector Manager - " + sectorManager.getName());
        sectorButtons = new ArrayList<>();
        buildUI(sectorManager.getSectors());
        configureFrame();


        this.sectorManager.addSectorChangeListener(sector -> {
            sectorButtons.stream()
                    .filter(it -> it.getSectorId().equals(sector.id))
                    .findFirst()
                    .ifPresent(JavaSwingSectorButton::refresh);
        });
    }

    private void buildUI(List<Sector> sectors) {
        int numberOfSectors = sectors.size();
        if (numberOfSectors < 1) {
            throw new IllegalArgumentException("Cannot build UI. Number of sectors < 1");
        }

        JComponent panel = new JPanel();

        int numOfCols = 2;
        int numOfRows = numberOfSectors % numOfCols == 0 ?
                (numberOfSectors / numOfCols) :
                (numberOfSectors / numOfCols) + 1;

        panel.setLayout(new GridLayout(numOfRows, numOfCols));

        sectors.stream()
                .map(JavaSwingSectorButton::new)
                .forEachOrdered(sectorButton -> {
                    sectorButton.addActionListener(sectorManager::resolve);
                    sectorButtons.add(sectorButton);
                    panel.add(sectorButton);
                });

        frame.add(panel);
    }

    private void configureFrame() {
        frame.setSize(800, 600);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        frame.setVisible(true);
    }
}
