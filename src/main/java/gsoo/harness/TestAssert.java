package gsoo.harness;

public final class TestAssert {

    private TestAssert() { }

    public static void assertEquals(Object expected, Object actual, String message) {
        boolean equal = (expected == null) ? (actual == null) : expected.equals(actual);
        if (!equal) {
            throw new AssertionError(message + " — expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                return; // expected exception, test passes
            }
            throw new AssertionError(message + " — expected " + expectedType.getSimpleName()
                    + " but got " + t.getClass().getSimpleName());
        }
        throw new AssertionError(message + " — expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }
}
