package gsoo.algorithms.c5_dp_knapsack;


public final class Knapsack {

    public static final class Item {
        public final String requestId;
        public final int requiredMinutes;
        public final int priorityValue;

        public Item(
                String requestId,
                int requiredMinutes,
                int priorityValue
        ) {
            if (requestId == null || requestId.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Request id cannot be null or blank"
                );
            }
            if (requiredMinutes <= 0) {
                throw new IllegalArgumentException(
                        "Required minutes must be positive"
                );
            }
            if (priorityValue <= 0) {
                throw new IllegalArgumentException(
                        "Priority value must be positive"
                );
            }

            this.requestId = requestId;
            this.requiredMinutes = requiredMinutes;
            this.priorityValue = priorityValue;
        }
    }

    public static final class Result {
        public final Item[] selectedItems;
        public final int totalMinutes;
        public final int totalValue;
        public final int capacityMinutes;
        public final int[][] table;

        private Result(
                Item[] selectedItems,
                int totalMinutes,
                int totalValue,
                int capacityMinutes,
                int[][] table
        ) {
            this.selectedItems = selectedItems;
            this.totalMinutes = totalMinutes;
            this.totalValue = totalValue;
            this.capacityMinutes = capacityMinutes;
            this.table = table;
        }

        public int remainingMinutes() {
            return capacityMinutes - totalMinutes;
        }
    }

   
    public Result solve(Item[] items, int capacityMinutes) {
        validateInput(items, capacityMinutes);

        int itemCount = items.length;
        int[][] table =
                new int[itemCount + 1][capacityMinutes + 1];

        for (int itemNumber = 1;
             itemNumber <= itemCount;
             itemNumber++) {

            Item item = items[itemNumber - 1];

            for (int minutes = 0;
                 minutes <= capacityMinutes;
                 minutes++) {

                int valueWithoutItem =
                        table[itemNumber - 1][minutes];

                table[itemNumber][minutes] = valueWithoutItem;

                if (item.requiredMinutes <= minutes) {
                    int valueWithItem =
                            item.priorityValue
                                    + table[itemNumber - 1]
                                    [minutes - item.requiredMinutes];

                    if (valueWithItem > valueWithoutItem) {
                        table[itemNumber][minutes] = valueWithItem;
                    }
                }
            }
        }

        return reconstructSelection(
                items,
                capacityMinutes,
                table
        );
    }

    private Result reconstructSelection(
            Item[] items,
            int capacityMinutes,
            int[][] table
    ) {
        Item[] reverseSelection = new Item[items.length];
        int selectedCount = 0;
        int minutesLeft = capacityMinutes;

        for (int itemNumber = items.length;
             itemNumber > 0;
             itemNumber--) {

            if (table[itemNumber][minutesLeft]
                    != table[itemNumber - 1][minutesLeft]) {

                Item selected = items[itemNumber - 1];
                reverseSelection[selectedCount] = selected;
                selectedCount++;
                minutesLeft -= selected.requiredMinutes;
            }
        }

        Item[] selectedItems = new Item[selectedCount];
        int totalMinutes = 0;

        for (int i = 0; i < selectedCount; i++) {
            Item selected =
                    reverseSelection[selectedCount - 1 - i];

            selectedItems[i] = selected;
            totalMinutes += selected.requiredMinutes;
        }

        int totalValue =
                table[items.length][capacityMinutes];

        return new Result(
                selectedItems,
                totalMinutes,
                totalValue,
                capacityMinutes,
                table
        );
    }

    private void validateInput(
            Item[] items,
            int capacityMinutes
    ) {
        if (items == null) {
            throw new IllegalArgumentException(
                    "Items array cannot be null"
            );
        }
        if (capacityMinutes < 0) {
            throw new IllegalArgumentException(
                    "Capacity cannot be negative"
            );
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                throw new IllegalArgumentException(
                        "Item at index " + i + " cannot be null"
                );
            }

            for (int j = i + 1; j < items.length; j++) {
                if (items[j] != null
                        && items[i].requestId.equals(
                        items[j].requestId)) {
                    throw new IllegalArgumentException(
                            "Duplicate request id: "
                                    + items[i].requestId
                    );
                }
            }
        }
    }
}
