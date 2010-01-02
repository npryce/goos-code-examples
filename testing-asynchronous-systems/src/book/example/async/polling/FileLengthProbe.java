package book.example.async.polling;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.File;

public class FileLengthProbe {

    private static final int NOT_SET = -1;

    public static Probe fileLength(String path, final Matcher<Integer> matcher) {
        final File file = new File(path);
        return new Probe() {
            private long lastFileLength = NOT_SET;

            public void sample() {
                lastFileLength = file.length();
            }

            public boolean isSatisfied() {
                return lastFileLength != NOT_SET && matcher.matches(lastFileLength);
            }

            public void describeAcceptanceCriteriaTo(Description d) {
                d.appendText(file.getAbsolutePath())
                        .appendText(" has length ")
                        .appendDescriptionOf(matcher);
            }

            public void describeFailureTo(Description d) {
                d.appendText("length was ").appendValue(lastFileLength);
            }

        };
    }
}
