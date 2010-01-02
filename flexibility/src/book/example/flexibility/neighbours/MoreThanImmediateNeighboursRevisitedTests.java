package book.example.flexibility.neighbours;

import book.example.flexibility.neighbours.MoreThanImmediateNeighbours.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;

@RunWith(JMock.class)
public class MoreThanImmediateNeighboursRevisitedTests {
    final Mockery context = new JUnit4Mockery();

    final IssueHandler handler = context.mock(IssueHandler.class);
    final TicketManager ticketManager = context.mock(TicketManager.class);
    final IssueManager issueManager = context.mock(IssueManager.class);
    final IssueQuery query = context.mock(IssueQuery.class);
    final Connections connections;

    final States locking = context.states("locking").startsAs("unlocked");


    public MoreThanImmediateNeighboursRevisitedTests() {
        connections = context.mock(Connections.class);
    }

    ///BEGIN pass-query-to-services-test
    @Test
    public void repositoryPassesUnresolvedIssuesQueryToConnectionServices() throws Failure {
        final ConnectionServices services = context.mock(ConnectionServices.class);

        IssueRepository repository = new IssueRepository(services);

        context.checking(new Expectations() {{
            oneOf(services).queryIssueManager(IssueQuery.UNRESOLVED, handler);
        }});

        repository.allUnresolvedIssues(handler);
    }
///END pass-query-to-services-test

    @Test
    public void lockedConnectionsQueriesIssueManagerWithinTicketManagerLock() throws Failure {
        LockingService service = new LockingService(connections);

        context.checking(new Expectations() {{
            allowing(connections).getTicketManager();
            will(returnValue(ticketManager));

            oneOf(ticketManager).lock();
            then(locking.is("locked"));
            oneOf(ticketManager).getIssueManager();
            when(locking.is("locked"));
            will(returnValue(issueManager));
            oneOf(query).applyTo(issueManager, handler);
            oneOf(ticketManager).unlock();
            when(locking.is("locked"));
            then(locking.is("unlocked"));
        }});

        service.queryIssueManager(query, handler);
    }

    @Test
    public void lockedConnectionsUnlocksTicketManagerAfterFailure() throws Failure {
        LockingService locked = new LockingService(connections);

        context.checking(new Expectations() {{
            allowing(ticketManager).getIssueManager();
            will(throwException(new Failure()));
            allowing(connections).getTicketManager();
            will(returnValue(ticketManager));

            oneOf(ticketManager).lock();
            then(locking.is("locked"));
            oneOf(ticketManager).unlock();
            when(locking.is("locked"));
        }});

        try {
            locked.queryIssueManager(query, handler);
            fail("Should have thrown execption");
        } catch (Failure expected) {
        }
    }


    ///BEGIN pass-query-to-services
    public static class IssueRepository {
        private final ConnectionServices connectionServices;

        public IssueRepository(ConnectionServices connections) {
            this.connectionServices = connections;
        }

        public void allUnresolvedIssues(IssueHandler handler) throws Failure {
            connectionServices.queryIssueManager(IssueQuery.UNRESOLVED, handler);
        }
    }
///END pass-query-to-services

    public interface ConnectionServices {
        void queryIssueManager(IssueQuery query, IssueHandler handler) throws Failure;
    }

    public static class LockingService implements ConnectionServices {
        private final Connections connections;

        public LockingService(Connections connections) {
            this.connections = connections;
        }

        public void queryIssueManager(IssueQuery query, IssueHandler handler) throws Failure {
            TicketManager ticketManager = connections.getTicketManager();
            ticketManager.lock();
            try {
                query.applyTo(ticketManager.getIssueManager(), handler);
            } finally {
                ticketManager.unlock();
            }
        }
    }

    public interface IssueQuery {
        public static final IssueQuery UNRESOLVED = new IssueQuery() {
            public void applyTo(IssueManager issueManager, IssueHandler handler) {
            }
        };

        void applyTo(IssueManager issueManager, IssueHandler handler);
    }
}
