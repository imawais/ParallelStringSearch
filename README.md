Parallel 4-Letter String Search
==============================

How to run:
1. Make sure JDK 17+ is installed I have used OpenJDK 23
2. Open terminal in this folder
3. Run: javac -d out src/de/parallelsearch/task/*.java
4. Run: java -cp out de.parallelsearch.task.main

Or open the project in IntelliJ IDEA and run main.java directly.

Features:
• Parallel search using all CPU cores (change MAX_THREADS in StringSearchEngine.java)
• Data loaded once from data/strings.json and shuffled
• Fully responsive Swing GUI (no freezing)
• Case-insensitive "contains" search

Tested on macOS Apple Silicon (M3 Pro)
