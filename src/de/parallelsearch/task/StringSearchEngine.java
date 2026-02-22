package de.parallelsearch.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class StringSearchEngine {
    // Change this number to limit CPU usage. Set to 0 or negative to use all available cores.
    private static final int MAX_THREADS = 8; // 0 = use all cores

    private final List<String> data;
    private final ExecutorService executor;

    public StringSearchEngine(List<String> data) {
        this.data = data;

        int threadCount = MAX_THREADS > 0 ? MAX_THREADS : Runtime.getRuntime().availableProcessors();
        System.out.println("Search engine initialized with " + threadCount + " threads");
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    // Public method that will be called from GUI
    public SearchResult search(String query) {
        if (query == null || query.isEmpty()) { // Empty query = return all (as many apps do)
            long start = System.nanoTime();
            return new SearchResult(new ArrayList<>(data), (System.nanoTime() - start) / 1_000_000.0);
        }

        String lowerQuery = query.toLowerCase();
        long start = System.nanoTime();

        List<String> results = parallelSearch(lowerQuery);

        double timeMs = (System.nanoTime() - start) / 1_000_000.0;
        return new SearchResult(results, timeMs);
    }

    // Parallel search implementation
    private List<String> parallelSearch(String lowerQuery) {
        int threadCount = ((ThreadPoolExecutor) executor).getCorePoolSize();
        int chunkSize = Math.max(1, data.size() / threadCount);

        List<Future<List<String>>> futures = new ArrayList<>();

        // Split data into chunks
        for (int i = 0; i < data.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, data.size());
            futures.add(executor.submit(() -> searchChunk(start, end, lowerQuery)));
        }

        // Collect results while preserving original order
        List<String> results = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            try {
                results.addAll(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    // Worker method for each thread
    private List<String> searchChunk(int from, int to, String lowerQuery) {
        List<String> localResults = new ArrayList<>();
        for (int i = from; i < to; i++) {
            String s = data.get(i);
            if (s.toLowerCase().contains(lowerQuery)) {
                localResults.add(s);
            }
        }
        return localResults;
    }

    // Call this when closing the app
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    // Simple container class to get results + execution time for the searching string
    public static class SearchResult {
        public final List<String> matches;
        public final double executionTimeMs;

        public SearchResult(List<String> matches, double executionTimeMs) {
            this.matches = matches;
            this.executionTimeMs = executionTimeMs;
        }
    }
}
