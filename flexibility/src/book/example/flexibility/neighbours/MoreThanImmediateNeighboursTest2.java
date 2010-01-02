package book.example.flexibility.neighbours;

import book.example.flexibility.neighbours.MoreThanImmediateNeighbours.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(JMock.class)
public class MoreThanImmediateNeighboursTest2 {
    final Mockery context = new JUnit4Mockery();

    final Connections connections = context.mock(Connections.class);
    final TicketManager ticketManager = context.mock(TicketManager.class);
    final IssueManager issueManager = context.mock(IssueManager.class);
    final IssueHandler issueHandler = context.mock(IssueHandler.class);
    final Issue issue1Unresolved = issue(true);
    final Issue issue2 = issue(false);
    final Issue issue3Unresolved = issue(true);

    final MoreThanImmediateNeighbours2 moreThanImmediateNeighbours = new MoreThanImmediateNeighbours2(connections);

    ///BEGIN more-than-immediate-neighbours-test
    @Test
    public void filtersUnresolvedIssuesFromIssueManager() throws Failure {
        final ArrayList<Issue> allIssues = new ArrayList<Issue>() {{
            add(issue1Unresolved);
            add(issue2);
            add(issue3Unresolved);
        }};

        final States locking = context.states("locking").startsAs("unlocked");

        context.checking(new Expectations() {{
            allowing(connections).getTicketManager();
            will(returnValue(ticketManager));
            allowing(issueManager).allIssuesIterator();
            will(returnIterator(allIssues));

            oneOf(ticketManager).lock();
            when(locking.is("unlocked"));
            then(locking.is("locked"));

            oneOf(ticketManager).getIssueManager();
            when(locking.is("locked"));
            will(returnValue(issueManager));

            oneOf(issueHandler).accept(issue1Unresolved);
            when(locking.is("locked"));
            oneOf(issueHandler).accept(issue3Unresolved);
            when(locking.is("locked"));

            oneOf(ticketManager).unlock();
            when(locking.is("locked"));
            then(locking.is("unlocked"));
        }});

        moreThanImmediateNeighbours.allUnresolvedIssues(issueHandler);
    }
///END more-than-immediate-neighbours-test

    private static Issue issue(final boolean isUnresolved) {
        return new Issue() {
            public boolean isUnresolved() {
                return isUnresolved;
            }
        };
    }
}
