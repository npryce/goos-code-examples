package book.example.diagnostics;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

// These are meant to fail!
public class DiagnosticsExamples {
    @Test
    public void bug1432() {
        assertNotNull(null);
    }

    @Test
    public void exampleWithoutMessage() {
        assertThat("Xyzzy", equalTo("Foo"));
    }

    @Test
    public void exampleWithMessage() {
        assertThat("customer's first name", "Xyzzy", equalTo("Foo"));
    }

    @Test
    public void exampleDates() {
        assertThat("payment date", new Date(2000), equalTo(new Date(1000)));
    }

    @Test
    public void exampleSelfDescribingDates() {
        assertThat("payment date", namedDate(2000, "endDate"), equalTo(namedDate(1000, "startDate")));
    }

    Date namedDate(long timeValue, final String name) {
        return new Date(timeValue) {
            public String toString() {
                return name;
            }
        };
    }
}
