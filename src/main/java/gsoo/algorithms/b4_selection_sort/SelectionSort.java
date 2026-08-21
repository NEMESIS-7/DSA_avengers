package gsoo.algorithms.b4_selection_sort;

class ServiceRequest {
    String requestId;
    String category;
    String sourceLocationId;
    String destinationLocationId;
    int urgency;
    String status;

    public ServiceRequest(String requestId, String category, String sourceLocationId,
                           String destinationLocationId, int urgency, String status) {
        this.requestId = requestId;
        this.category = category;
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.urgency = urgency;
        this.status = status;
    }

    public String toString() {
        return requestId + " | " + category + " | urgency " + urgency;
    }
}

public class SelectionSort {

    // Sorts the array in place, by requestId, smallest to largest and returns how many comparisons were made, useful for the efficiency
    public static int sort(ServiceRequest[] array) {
        int comparisons = 0;

        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < array.length; j++) {
                comparisons++;
                if (array[j].requestId.compareTo(array[minIndex].requestId) < 0) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                ServiceRequest temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
            }
        }

        return comparisons;
    }
}