package book.example.flexibility.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class MatchingTest {
    Mockery context = new JUnit4Mockery();

    String auditMessage = "crossed market for instrument FGD.430: bid=245.00, ask=240.00.";

    public class CalculationFailure {
        private final String instrumentId;
        private final String message;
        private final int code;

        public CalculationFailure(String instrumentId, int code, String message) {
            this.instrumentId = instrumentId;
            this.code = code;
            this.message = message;
        }

        public String instrumentId() {
            return instrumentId;
        }

        public int code() {
            return code;
        }

        public String message() {
            return message;
        }
    }

    public static int FAILURE_INVALID_MARKET_PARAMETERS = 1001;
    public static int FAILURE_MARKET_CLOSED = 1002;
    public static int FAILURE_CROSSED_MARKET = 1003;

    @Test
    public void assertingAboutSingleFeatureOfObject() {
        String failingInstrument = "FPG.125";

        // request calculation ...

        CalculationFailure failure = (CalculationFailure) receiveResult();

        assertThat("failure reported for correct instrument",
                failure.instrumentId(), equalTo(failingInstrument));
        assertThat("calculation failed because of crossed market",
                failure.code(), equalTo(FAILURE_CROSSED_MARKET));
    }


    private Object receiveResult() {
        return new CalculationFailure("FPG.125", FAILURE_CROSSED_MARKET,
                auditMessage);
    }


    @Test
    public void brittleTest() {
        assertThat(auditMessage, equalTo("crossed market for instrument FGD.430: bid=$245.00, ask=$240.00"));
    }

    @Test
    public void flexibleTest() {
        assertThat("error condition",
                auditMessage, containsString("crossed market"));
        assertThat("instrument ID",
                auditMessage, containsString("FGD.430"));
        assertThat("bid price",
                auditMessage, containsString("245.00"));
        assertThat("ask price",
                auditMessage, containsString("240.00"));
    }

    public interface AuditTrail {
        void audit(String auditMessage);
    }

    AuditTrail auditTrail = context.mock(AuditTrail.class);

    @Test
    public void brittleMockObjectTest() {
        context.checking(new Expectations() {{
            oneOf(auditTrail).audit("market crossed for instrument FGD.430: bid=245.00, ask=240.00");
        }});

        auditTrail.audit(auditMessage);
    }

    @Test
    public void flexibleMockObjectTest() {
        context.checking(new Expectations() {{
            oneOf(auditTrail).audit(with(stringContainingAllOf("crossed market", "FGD.430", "245.00", "240.00")));
        }});

        auditTrail.audit(auditMessage);
    }


    protected Matcher<String> stringContainingAllOf(final String... substrings) {
        return new TypeSafeDiagnosingMatcher<String>() {
            @Override
            protected boolean matchesSafely(String matchedString, Description mismatchDescription) {
                boolean matches = true;

                for (String substring : substrings) {
                    if (!matchedString.contains(substring)) {
                        mismatchDescription.appendText(matches ? "does not contain " : ", ")
                                .appendValue(substring);
                        matches = false;
                    }
                }

                return matches;
            }

            public void describeTo(Description description) {
                description.appendText("a string containing all of ")
                        .appendValueList("{", ", ", "}", Arrays.asList(substrings));
            }
        };
    }
}
