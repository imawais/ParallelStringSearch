package de.parallelsearch.task;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
// Start the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new SearchApp().setVisible(true);
        });
    }
}