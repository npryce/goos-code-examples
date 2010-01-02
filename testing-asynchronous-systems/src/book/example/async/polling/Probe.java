package book.example.async.polling;

import org.hamcrest.Description;

public interface Probe {
    boolean isSatisfied();

    void sample();

    void describeAcceptanceCriteriaTo(Description d);

    void describeFailureTo(Description d);
}
