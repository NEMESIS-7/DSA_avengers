package gsoo.algorithms.c5_dp_knapsack;

import gsoo.app.Config;
import gsoo.db.ServiceRequest;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class KnapsackTest {

    /** NORMAL CASE: A + B has the best value within five minutes. */
    @Test
    void normalCase_selectsOptimalCombinationAndReconstructsIt() {
        Knapsack.Item[] items = {
            new Knapsack.Item("A", 2, 3),
            new Knapsack.Item("B", 3, 4),
            new Knapsack.Item("C", 4, 5)
        };

        Knapsack.Result result =
                new Knapsack().solve(items, 5);

        assertEquals(7, result.totalValue);
        assertEquals(5, result.totalMinutes);
        assertEquals(0, result.remainingMinutes());
        assertEquals(2, result.selectedItems.length);
        assertEquals("A", result.selectedItems[0].requestId);
        assertEquals("B", result.selectedItems[1].requestId);
        assertEquals(4, result.table.length);
        assertEquals(6, result.table[0].length);
    }

    /** BOUNDARY CASE: zero capacity and an empty input both return no work. */
    @Test
    void boundaryCase_zeroCapacityOrNoItems_returnsEmptySelection() {
        Knapsack.Item[] oneItem = {
            new Knapsack.Item("A", 10, 5)
        };

        Knapsack.Result zeroCapacity =
                new Knapsack().solve(oneItem, 0);

        assertEquals(0, zeroCapacity.totalValue);
        assertEquals(0, zeroCapacity.totalMinutes);
        assertEquals(0, zeroCapacity.selectedItems.length);

        Knapsack.Result noItems =
                new Knapsack().solve(new Knapsack.Item[0], 20);

        assertEquals(0, noItems.totalValue);
        assertEquals(0, noItems.selectedItems.length);
        assertEquals(20, noItems.remainingMinutes());
    }

    /** INVALID INPUT: rejects malformed items, capacities and duplicates. */
    @Test
    void invalidInput_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Knapsack().solve(null, 10)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Knapsack().solve(
                        new Knapsack.Item[0],
                        -1
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Knapsack.Item("", 10, 2)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Knapsack.Item("A", 0, 2)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Knapsack.Item("A", 10, 0)
        );

        Knapsack.Item[] duplicateIds = {
            new Knapsack.Item("REQ-1", 10, 2),
            new Knapsack.Item("REQ-1", 20, 3)
        };

        assertThrows(
                IllegalArgumentException.class,
                () -> new Knapsack().solve(duplicateIds, 30)
        );
    }

    /** Confirms the adapter uses real ServiceRequest fields and Config values. */
    @Test
    void hospitalPlanner_usesPendingRequestsAndProjectParameters() {
        ServiceRequest pendingOne = request(
                "REQ-1",
                5,
                "PENDING",
                "2026-08-21 08:00:00",
                "2026-08-21 09:00:00"
        );

        ServiceRequest completed = request(
                "REQ-2",
                5,
                "COMPLETED",
                "2026-08-21 08:00:00",
                "2026-08-21 09:00:00"
        );

        ServiceRequest pendingTwo = request(
                "REQ-3",
                4,
                "PENDING",
                "2026-08-21 08:00:00",
                "2026-08-21 10:00:00"
        );

        ServiceRequest[] requests = {
            pendingOne,
            completed,
            pendingTwo
        };

        HospitalKnapsackPlanner planner =
                new HospitalKnapsackPlanner();

        Knapsack.Item[] items =
                planner.buildPendingItems(requests);

        assertEquals(2, items.length);
        assertEquals("REQ-1", items[0].requestId);
        assertEquals(60, items[0].requiredMinutes);
        assertEquals(
                Config.URGENCY_WEIGHT * 5,
                items[0].priorityValue
        );

        assertEquals("REQ-3", items[1].requestId);
        assertEquals(120, items[1].requiredMinutes);

        Knapsack.Result result =
                planner.planOneShift(requests);

        assertEquals(Config.SHIFT_BUDGET_MINUTES,
                result.capacityMinutes);
        assertEquals(180, result.totalMinutes);
        assertEquals(2, result.selectedItems.length);
    }

    private ServiceRequest request(
            String id,
            int urgency,
            String status,
            String submitted,
            String deadline
    ) {
        return new ServiceRequest(
                id,
                "PATIENT_TRANSFER",
                "PAT-TEST",
                "INT-01",
                "INT-02",
                urgency,
                status,
                Timestamp.valueOf(submitted),
                Timestamp.valueOf(deadline),
                null
        );
    }
}
