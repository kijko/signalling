package pl.kijko.sectormanager.gui;

import pl.kijko.sectormanager.Sector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

class JavaSwingSectorButton extends JButton {
    private final Sector sector;
    private final Color defaultBackgroundColor;
    private final Color defaultForegroundColor;
    private final List<Consumer<Sector>> actionListeners = new ArrayList<>();

    public JavaSwingSectorButton(Sector sector) {
        super("Sektor " + sector.id);

        this.sector = sector;
        this.defaultBackgroundColor = getBackground();
        this.defaultForegroundColor = getForeground();

        configureButton();
    }

    private void configureButton() {
        setFont(new Font("Arial", Font.BOLD, 40));
        refresh();
        addActionListener((ActionEvent actionListener) -> actionListeners.forEach(it -> it.accept(sector)));
    }

    public String getSectorId() {
        return sector.id;
    }

    public void refresh() {
        if (sector.isInNeed()) {
            setInNeedStyle();
        } else {
            setNormalStyle();
        }
    }

    private void setInNeedStyle() {
        setBackground(Color.GREEN);
        setForeground(Color.WHITE);
    }

    private void setNormalStyle() {
        setBackground(defaultBackgroundColor);
        setForeground(defaultForegroundColor);
    }

    public void addActionListener(Consumer<Sector> listener) {
        actionListeners.add(listener);
    }
}
