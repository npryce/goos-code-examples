package book.example.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class StringStartsWithMatcher extends TypeSafeMatcher<String> {
    private final String expectedPrefix;

    public StringStartsWithMatcher(String expectedPrefix) {
        this.expectedPrefix = expectedPrefix;
    }

    @Override
    protected boolean matchesSafely(String actual) {
        return actual.startsWith(expectedPrefix);
    }

    public void describeTo(Description matchDescription) {
        matchDescription.appendText("a string starting with ")
                .appendValue(expectedPrefix);
    }

    @Override
    protected void describeMismatchSafely(String actual, Description mismatchDescription) {
        String actualPrefix = actual.substring(0, Math.min(actual.length(), expectedPrefix.length()));
        mismatchDescription.appendText("started with ")
                .appendValue(actualPrefix);
    }

    public static Matcher<String> startsWith(String prefix) {
        return new StringStartsWithMatcher(prefix);
    }
}
