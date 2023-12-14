import java.util.*;
import java.util.stream.Collectors;

/**
 * Thread class for generating permutations and tracking the best route and its duration.
 */
public class PermutationGenerator extends Thread {
    private final List<FinalRunner.Building> buildings;
    private final FinalRunner.Building zerothElement;
    private final FinalRunner.Building firstElement;
    private final Map<String, Integer> timeMatrixMap;

    // Best route tracking
    private String bestRoute;
    private int minDuration = Integer.MAX_VALUE;

    /**
     * Constructor for PermutationGenerator.
     * @param buildings List of buildings
     * @param zerothElement The 0th element building
     * @param firstElementIndex Index of the first element building
     * @param timeMatrixMap Time matrix map
     */
    PermutationGenerator(List<FinalRunner.Building> buildings,
                         FinalRunner.Building zerothElement,
                         int firstElementIndex,
                         Map<String, Integer> timeMatrixMap) {
        this.buildings = buildings;
        this.zerothElement = zerothElement;
        this.timeMatrixMap = timeMatrixMap;
        this.firstElement = buildings.get(firstElementIndex);
    }

    @Override
    public void run() {
        // Convert buildings
        List<String> currentPermutation = buildingsToStringArray(buildings);

        // Exclude IDs
        List<String> filteredPermutation = currentPermutation.stream()
                .filter(id -> !id.equals(zerothElement.id) && !id.equals(firstElement.id))
                .collect(Collectors.toList());

        // Generate permutations
        heapPermutation(filteredPermutation,
                zerothElement.id,
                firstElement.id,
                filteredPermutation.size());

        // Store best route
        storeBestRoute(bestRoute);
    }
    /**
     * Convert buildings to a string array.
     * @param buildings List of buildings
     * @return List of building IDs as strings
     */
    private List<String> buildingsToStringArray(List<FinalRunner.Building> buildings) {
        List<String> stringArray = new ArrayList<>();
        for (FinalRunner.Building building : buildings) {
            stringArray.add(building.id);
        }
        return stringArray;
    }
    /**
     * Generate permutations using Heap's algorithm.
     * @param buildingIds List of building IDs
     * @param zerothElement ID of the zeroth element building
     * @param firstElement ID of the first element building
     * @param size Size of the permutation
     */
    private void heapPermutation(List<String> buildingIds,
                                 String zerothElement,
                                 String firstElement,
                                 int size) {
        if (size == 1) {
            // Construct permutation string
            String someRoute = getPermutationString(zerothElement, firstElement, buildingIds);

            // Calculate duration
            int subTourDuration = calculateSubTourDuration(someRoute);

            // Update min duration
            if (subTourDuration < minDuration) {
                minDuration = subTourDuration;
                bestRoute = someRoute;
            }
        } else {
            for (int i = 0; i < size; i++) {
                // Recursively generate permutations
                heapPermutation(buildingIds, zerothElement, firstElement, size - 1);

                // Swap based on parity
                if (size % 2 == 1) {
                    Collections.swap(buildingIds, 0, size - 1);
                } else {
                    Collections.swap(buildingIds, i, size - 1);
                }
            }
        }
    }
    /**
     * Helper method to construct a permutation string.
     * @param zerothElement ID of the zeroth element building
     * @param firstElement ID of the first element building
     * @param permutation List of building IDs in the permutation
     * @return Constructed permutation string
     */
    private String getPermutationString(String zerothElement,
                                        String firstElement,
                                        List<String> permutation) {
        StringBuilder permutationBuilder = new StringBuilder(zerothElement + " " + firstElement + " ");
        for (String building : permutation) {
            permutationBuilder.append(building).append(" ");
        }
        permutationBuilder.append(zerothElement);
        return permutationBuilder.toString();
    }
    /**
     * Calculate the total duration of a sub-tour.
     * @param permutation Permutation string
     * @return Total duration of the sub-tour
     */
    private int calculateSubTourDuration(String permutation) {
        int duration = 0;
        String[] buildingIds = permutation.split("\\s+");
        Map<String, Integer> timeMatrixMap = this.timeMatrixMap;

        // Traverse buildings, accumulate durations
        for (int i = 0; i < buildingIds.length - 1; i++) {
            String key = buildingIds[i] + " " + buildingIds[i + 1];
            try {
                duration += timeMatrixMap.get(key);
            } catch (NullPointerException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return duration;
    }
    /**
     * Store the best route and its duration in the routeDurations map.
     * @param permutationString Permutation string
     */
    private void storeBestRoute(String permutationString) {
        FinalRunner.routeDurations.put(permutationString, minDuration);
    }
}
