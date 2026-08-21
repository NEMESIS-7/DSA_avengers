package gsoo.harness;

public class TestResult {
    public final String name;
    public final TestCase.Kind kind;
    public final boolean passed;
    public final String failureMessage; // null if passed
    public final long elapsedNanos;

    public TestResult(String name, TestCase.Kind kind, boolean passed, String failureMessage, long elapsedNanos) {
        this.name = name;
        this.kind = kind;
        this.passed = passed;
        this.failureMessage = failureMessage;
        this.elapsedNanos = elapsedNanos;
    }

    @Override
    public String toString() {
        String status = passed ? "PASS" : "FAIL";
        String base = String.format("[%s] (%s) %s — %.3f ms", status, kind, name, elapsedNanos / 1_000_000.0);
        return passed ? base : base + "\n        reason: " + failureMessage;
    }
}
