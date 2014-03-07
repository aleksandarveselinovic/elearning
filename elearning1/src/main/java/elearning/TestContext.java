package elearning;

import java.io.Serializable;

public class TestContext implements Serializable {

    private long testId;

    public TestContext(long testId) {
        this.testId = testId;
    }

    public long getTestId() {
        return testId;
    }

}
