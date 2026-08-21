package gsoo.harness;

public class TestRunner {
    public static TestResult[] run(TestCase[] cases) {
        TestResult[] results = new TestResult[cases.length];
        int passed = 0;

        for (int i = 0; i < cases.length; i++) {
            TestCase tc = cases[i];
            long start = System.nanoTime();
            try {
                tc.run();
                long elapsed = System.nanoTime() - start;
                results[i] = new TestResult(tc.name(), tc.kind(), true, null, elapsed);
                passed++;
            } catch (Throwable t) {
                long elapsed = System.nanoTime() - start;
                results[i] = new TestResult(tc.name(), tc.kind(), false, t.getMessage(), elapsed);
            }
        }

        System.out.println("---- Test run: " + cases.length + " case(s) ----");
        for (TestResult r : results) {
            System.out.println(r);
        }
        System.out.println("---- " + passed + "/" + cases.length + " passed ----");

        return results;
    }
}
