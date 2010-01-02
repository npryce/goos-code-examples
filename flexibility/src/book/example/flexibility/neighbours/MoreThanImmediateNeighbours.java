package book.example.flexibility.neighbours;

import book.example.flexibility.neighbours.MoreThanImmediateNeighboursRevisitedTests.IssueQuery;

import java.util.Iterator;

public class MoreThanImmediateNeighbours {
    private final Connections connections;

    public MoreThanImmediateNeighbours(Connections connections) {
        this.connections = connections;
    }

    public void allUnresolvedIssues(IssueHandler handler) throws Failure {
        TicketManager ticketManager = connections.getTicketManager();

        ticketManager.lock();
        try {
            IssueManager issueManager = ticketManager.getIssueManager();
            Iterator<Issue> issues = issueManager.allIssuesIterator();

            while (issues.hasNext()) {
                Issue issue = issues.next();

                if (issue.isUnresolved()) {
                    handler.accept(issue);
                }
            }
        } finally {
            ticketManager.unlock();
        }
    }

    public interface Issue {
        boolean isUnresolved();
    }

    public interface IssueHandler {
        void accept(Issue issue);
    }

    public interface Connections {
        TicketManager getTicketManager();

        void queryIssueManager(IssueQuery unresolved, IssueHandler handler) throws Failure;
    }

    public interface TicketManager {
        void lock();

        void unlock();

        IssueManager getIssueManager() throws Failure;
    }

    public interface IssueManager {
        Iterator<Issue> allIssuesIterator();
    }

    public static class Failure extends Exception {
    }

    ;
}
