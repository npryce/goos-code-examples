package book.example.hamcrest;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static book.example.hamcrest.StringStartsWithMatcher.startsWith;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class StringStartsWithMatcherTest extends AbstractMatcherTest {
    static final String PREFIX = "PREFIX";
    Matcher<String> stringStartsWith = startsWith(PREFIX);

    @Override
    protected Matcher<?> createMatcher() {
        return stringStartsWith;
    }

    @Test
    public void evaluatesToTrueIfArgumentStartsWithSpecifiedSubstring() {
        assertTrue("should be true if excerpt at beginning", stringStartsWith.matches(PREFIX + "END"));
        assertFalse("should be false if excerpt at end", stringStartsWith.matches("START" + PREFIX));
        assertFalse("should be false if excerpt in middle", stringStartsWith.matches("START" + PREFIX + "END"));
        assertTrue("should be true if excerpt is at beginning and repeated", stringStartsWith.matches(PREFIX + PREFIX));

        assertFalse("should be false if excerpt is not in string", stringStartsWith.matches("Something else"));
        assertFalse("should be false if part of excerpt is at start of string", stringStartsWith.matches(PREFIX.substring(1)));
    }

    @Test
    public void evaluatesToTrueIfArgumentIsEqualToSubstring() {
        assertTrue("should be true if excerpt is entire string", stringStartsWith.matches(PREFIX));
    }

    @Test
    public void hasAReadableDescription() {
        assertDescription("a string starting with \"PREFIX\"", stringStartsWith);
    }

    @Test
    public void describesMismatch() {
        StringDescription d = new StringDescription();
        stringStartsWith.describeMismatch("Bananas", d);
        assertThat(d.toString(), equalTo("started with \"Banana\""));
    }

    @Test
    public void describesMismatchAgainstShortString() {
        StringDescription d = new StringDescription();
        stringStartsWith.describeMismatch("Foo", d);
        assertThat(d.toString(), equalTo("started with \"Foo\""));
    }

    @Test
    public void describesMismatchAgainstNull() {
        StringDescription d = new StringDescription();
        stringStartsWith.describeMismatch(null, d);
        assertThat(d.toString(), equalTo("was null"));
    }

    @Test
    public void describesMismatchAgainstUnknownObjectType() {
        StringDescription d = new StringDescription();
        stringStartsWith.describeMismatch(new UnknownType(), d);
        assertThat(d.toString(), containsString(UnknownType.class.getName()));
    }
}
