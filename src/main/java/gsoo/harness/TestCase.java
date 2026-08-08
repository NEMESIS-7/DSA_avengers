package gsoo.harness;

public interface TestCase {
    enum Kind { NORMAL, BOUNDARY, INVALID }
    String name();
    Kind kind();
    void run() throws Exception;
}
