import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Main class for optimizing traveling routes through permutation generation.
 */
public class FinalRunner {

    // Building class
    static class Building {
        final String id;
        final int[][] timeMatrix;

        /**
         * Constructor for Building class.
         * @param id Building ID
         * @param timeMatrix Time matrix for the building
         */
        public Building(String id, int[][] timeMatrix) {
            this.id = id;
            this.timeMatrix = timeMatrix;
        }

        // Override toString
        @Override
        public String toString() {
            return id;
        }
    }

    // Global route durations map
    public static ConcurrentHashMap<String, Integer> routeDurations = new ConcurrentHashMap<>();

    // Global list of buildings
    public static List<Building> buildings;

    // Global time matrix map
    public static Map<String, Integer> timeMatrixMap;

    /**
     * Main method for optimizing traveling routes.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Read input file
        buildings = readInputFile();

        // Populate times into map
        timeMatrixMap = indexTimes(buildings);

        // Get processors
        int processors = Runtime.getRuntime().availableProcessors();

        // Capture start time
        long startTime1 = System.currentTimeMillis();

        // Create permutation threads
        PermutationThreads(buildings, processors);

        // Capture end time
        long endTime1 = System.currentTimeMillis();
        System.out.println("Permutation generation time: " + (endTime1 - startTime1) + " milliseconds");

        // Print routeDurations map
        printPermutationHashMap();

        // Print minimum route cost
        printMinimumRouteCost();

        // Output the tour to a file
        outputTour();

        // Capture end time
        long endTime2 = System.currentTimeMillis();
        System.out.println("Total run time: " + (endTime2 - startTime1) + " milliseconds");
    }
    /**
     * Read input file and return list of buildings.
     * @return List of buildings
     */
    private static List<Building> readInputFile() {
        List<Building> buildings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("input2.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String id = parts[0].trim();
                String[] timeStrings = parts[1].trim().split("\\s+");
                int[][] timeMatrix = new int[timeStrings.length][timeStrings.length];

                for (int i = 0; i < timeStrings.length; i++) {
                    timeMatrix[i] = Arrays.stream(timeStrings[i].split("\\s+")).mapToInt(Integer::parseInt).toArray();
                }

                buildings.add(new Building(id, timeMatrix));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Building IDs:\t" + buildings);

        return buildings;
    }
    /**
     * Print minima for each routeDurations threads.
     */
    private static void printPermutationHashMap() {
        System.out.println("Minima Permutation HashMap:");

        for (Map.Entry<String, Integer> entry : routeDurations.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
    /**
     * Create threads for permutation generation.
     * @param buildings List of buildings
     * @param processors Number of processors
     */
    private static void PermutationThreads(List<Building> buildings, int processors) {
        // Set 0th element
        Building zerothElement = buildings.get(0);

        // Determine thread count
        int numThreadsForIteration = Math.min(processors, buildings.size() - 1);
        System.out.println("Threads used:\t" + numThreadsForIteration + "\n");

        // Initialize indices
        int startIndex = 1;
        int jmax = Math.min(startIndex + numThreadsForIteration, buildings.size());

        // Iterate through 1st elements
        while (startIndex < buildings.size()) {
            // List of permutation threads
            List<Thread> permutationThreads = new ArrayList<>();

            // Start permutation threads
            for (int j = startIndex; j < jmax; j++) {
                Thread permutationThread = new PermutationGenerator(buildings,
                        zerothElement,
                        j,
                        timeMatrixMap);
                permutationThreads.add(permutationThread);
                permutationThread.start();
            }

            // Wait for threads to finish
            waitForThreads(permutationThreads);

            // Update indices for the next iteration
            startIndex += numThreadsForIteration;
            jmax = Math.min(startIndex + numThreadsForIteration, buildings.size());
        }
    }
    /**
     * Wait for threads to finish.
     * @param threads List of threads
     */
    private static void waitForThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Index times in a map.
     * @param buildings List of buildings
     * @return Map of time matrix
     */
    public static Map<String, Integer> indexTimes(List<Building> buildings) {
        Map<String, Integer> timeMatrixMap = new HashMap<>();

        for (int i = 0; i < buildings.size(); i++) {
            for (int j = 0; j < buildings.size(); j++) {
                String key = buildings.get(i).id + " " + buildings.get(j).id;
                timeMatrixMap.put(key, buildings.get(i).timeMatrix[j][0]); // Corrected indices
            }
        }

        return timeMatrixMap;
    }
    /**
     * Print minimum route cost.
     */
    private static void printMinimumRouteCost() {
        if (!routeDurations.isEmpty()) {
            Map.Entry<String, Integer> minEntry = Collections.min(routeDurations.entrySet(), Map.Entry.comparingByValue());
            System.out.println("\nMinimum Traveling Route\n" + minEntry.getKey() + " " + minEntry.getValue()+"\n");
        }
    }
    /**
     * Output the minimum route to a file.
     */
    private static void outputTour() {
        if (!routeDurations.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output2.txt"))) {
                Map.Entry<String, Integer> minEntry = Collections.min(routeDurations.entrySet(), Map.Entry.comparingByValue());
                String tour = minEntry.getKey();
                writer.write(tour + " " + minEntry.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
