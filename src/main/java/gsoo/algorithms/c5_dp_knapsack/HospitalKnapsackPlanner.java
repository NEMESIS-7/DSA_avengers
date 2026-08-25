package gsoo.algorithms.c5_dp_knapsack;

import gsoo.app.Config;
import gsoo.db.ServiceRequest;


public final class HospitalKnapsackPlanner {

    public Knapsack.Item[] buildPendingItems(
            ServiceRequest[] requests
    ) {
        if (requests == null) {
            throw new IllegalArgumentException(
                    "Requests array cannot be null"
            );
        }

        int pendingCount = 0;

        for (int i = 0; i < requests.length; i++) {
            ServiceRequest request = requests[i];

            if (request == null) {
                throw new IllegalArgumentException(
                        "Request at index " + i + " cannot be null"
                );
            }

            if ("PENDING".equals(request.status)) {
                pendingCount++;
            }
        }

        Knapsack.Item[] items =
                new Knapsack.Item[pendingCount];

        int itemIndex = 0;

        for (ServiceRequest request : requests) {
            if (!"PENDING".equals(request.status)) {
                continue;
            }

            int minutes = planningMinutes(request);
            int value = priorityValue(request);

            items[itemIndex] = new Knapsack.Item(
                    request.requestId,
                    minutes,
                    value
            );
            itemIndex++;
        }

        return items;
    }

    public Knapsack.Result planOneShift(
            ServiceRequest[] requests
    ) {
        Knapsack.Item[] items = buildPendingItems(requests);

        return new Knapsack().solve(
                items,
                Config.SHIFT_BUDGET_MINUTES
        );
    }

    private int planningMinutes(ServiceRequest request) {
        if (request.submittedAt == null
                || request.deadlineAt == null) {
            throw new IllegalArgumentException(
                    "Pending request " + request.requestId
                            + " needs submitted and deadline times"
            );
        }

        long differenceMillis =
                request.deadlineAt.getTime()
                        - request.submittedAt.getTime();

        if (differenceMillis <= 0) {
            throw new IllegalArgumentException(
                    "Pending request " + request.requestId
                            + " has an invalid deadline"
            );
        }

        long minutes =
                (differenceMillis + 59_999L) / 60_000L;

        if (minutes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Planning time is too large for request "
                            + request.requestId
            );
        }

        return (int) minutes;
    }

    private int priorityValue(ServiceRequest request) {
        if (request.urgency < 1 || request.urgency > 5) {
            throw new IllegalArgumentException(
                    "Urgency must be between 1 and 5 for request "
                            + request.requestId
            );
        }

        return Config.URGENCY_WEIGHT * request.urgency;
    }
}
