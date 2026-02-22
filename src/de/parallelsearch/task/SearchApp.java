package de.parallelsearch.task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.List;

public class SearchApp extends JFrame {
    private final StringSearchEngine engine;
    private final DefaultListModel<String> listModel;
    private final JList<String> resultList;
    private final JTextField searchField;
    private final JLabel statusLabel;
    private final JLabel countLabel;

    // SwingWorker to keep GUI responsive
    private SwingWorker<List<String>, Void> currentWorker = null;

    public SearchApp() {
        // Load data once at startup
        List<String> allStrings = new DataLoader().loadStringsInMemory();
        engine = new StringSearchEngine(allStrings);

        // Start creation of GUI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("Parallel 4-Letter String Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 600);
        setLocationRelativeTo(null);

        // Main panel with some padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(mainPanel);

        // === Top: Search field ===
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchField.setToolTipText("Type any letters (case insensitive)");
        searchField.addKeyListener(new SearchKeyListener());

        JLabel hint = new JLabel("Enter string to search (e.g. ABC, aa, xyz, AABB):");
        hint.setForeground(Color.GRAY);

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(hint, BorderLayout.NORTH);
        topPanel.add(searchField, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // === Center: Scrollable results list ===
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setFont(new Font("Menlo", Font.PLAIN, 14)); // monospaced = nice on macOS
        resultList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // === Bottom: Status bar ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        countLabel = new JLabel("Total entries: 456,976");
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        bottomPanel.add(countLabel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        bottomPanel.setPreferredSize(new Dimension(0, 28));
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Initial empty search
        performSearch("");
    }

    // Triggered on every keystroke
    private void performSearch(String query) {
        // Cancel previous search if still running
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
        }

        currentWorker = new SearchWorker(query);
        currentWorker.execute();
    }

    // Inner class — runs search in background
    private class SearchWorker extends SwingWorker<List<String>, Void> {
        private final String query;
        private double timeTaken;

        SearchWorker(String query) {
            this.query = query != null ? query : "";

            // Show "Searching…" only if it takes noticeable time
            Timer delayTimer = new Timer(80, e -> {
                if (!isDone() && !isCancelled()) {
                    SwingUtilities.invokeLater(() -> {
                        if (!isDone() && !isCancelled()) {
                            statusLabel.setText("Searching…");
                            statusLabel.setForeground(Color.DARK_GRAY);
                        }
                    });
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }

        @Override
        protected List<String> doInBackground() throws Exception {
            StringSearchEngine.SearchResult result = engine.search(query);
            timeTaken = result.executionTimeMs;
            return result.matches;
        }

        @Override
        protected void done() {
            if (isCancelled()) return;

            try {
                List<String> matches = get();
                listModel.clear();
                for (String s : matches) {
                    listModel.addElement(s);
                }

                String countText = NumberFormat.getInstance().format(matches.size()) + " matches";
                if (query.isEmpty()) {
                    countLabel.setText("Total entries: 456,976");
                } else {
                    countLabel.setText("Found " + countText);
                }

                // Smooth status update
                statusLabel.setText(String.format("Search completed in %.2f ms", timeTaken));
                if (timeTaken < 5) {
                    statusLabel.setForeground(new Color(0, 140, 0)); // nice green
                } else if (timeTaken < 15) {
                    statusLabel.setForeground(Color.BLUE.darker());
                } else {
                    statusLabel.setForeground(new Color(180, 80, 0)); // orange
                }

            } catch (Exception ex) {
                statusLabel.setText("Error");
                statusLabel.setForeground(Color.RED);
            }
        }
    }

    // Listen to typing — start search when user pauses or presses Enter
    private class SearchKeyListener extends KeyAdapter {
        private final Timer timer = new Timer(250, e -> {
            performSearch(searchField.getText().trim());
            //timer.stop();
        });

        @Override
        public void keyReleased(KeyEvent e) {
            timer.restart();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                timer.stop();
                performSearch(searchField.getText().trim());
            }
        }
    }

    // Graceful shutdown
    @Override
    public void dispose() {
        engine.shutdown();
        super.dispose();
    }
}
