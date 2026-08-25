package gsoo.structures.c1_set;

public class HashSetTest {

    public static void main(String[] args) {

        HashSet set = new HashSet();

        // =========================
        // TEST 1: NORMAL CASE
        // =========================
        System.out.println("TEST 1 - Normal Case");

        boolean added = set.add("EXT-C01");

        if (added && set.contains("EXT-C01") && set.size() == 1) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }

        // =========================
        // TEST 2: BOUNDARY CASE
        // =========================
        System.out.println("\nTEST 2 - Boundary Case");

        for (int i = 1; i <= 20; i++) {
            set.add("INT-" + i);
        }

        if (set.size() == 21) {
            System.out.println("PASS - resize handled correctly");
        } else {
            System.out.println("FAIL");
        }

        // =========================
        // TEST 3: INVALID INPUT
        // =========================
        System.out.println("\nTEST 3 - Invalid Input");

        boolean invalidHandled = false;

        try {
            set.add(null);
        } catch (IllegalArgumentException e) {
            invalidHandled = true;
        }

        if (invalidHandled) {
            System.out.println("PASS - null input rejected");
        } else {
            System.out.println("FAIL");
        }

        // =========================
        // EXTRA CHECKS
        // =========================
        System.out.println("\nEXTRA CHECKS");

        System.out.println("Contains EXT-C01: " + set.contains("EXT-C01"));

        System.out.println("Remove EXT-C01: " + set.remove("EXT-C01"));

        System.out.println("Contains EXT-C01 after removal: "
                + set.contains("EXT-C01"));

        System.out.println("Final size: " + set.size());

        System.out.println("\nC1 HashSet tests completed.");
    }
}