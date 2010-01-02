package book.example.flexibility.neighbours;

import book.example.flexibility.neighbours.MoreThanImmediateNeighbours.*;

import java.util.Iterator;

public class MoreThanImmediateNeighbours2 {
    private final Connections connections;

    public MoreThanImmediateNeighbours2(Connections connections) {
        this.connections = connections;
    }

    ///BEGIN extract-method-for-ticket-manager
    public void allUnresolvedIssues(IssueHandler handler) throws Failure {
        TicketManager ticketManager = connections.getTicketManager();

        ticketManager.lock();
        try {
            retrieveUnresolvedIssues(ticketManager.getIssueManager(), handler);
        } finally {
            ticketManager.unlock();
        }
    }

    private void retrieveUnresolvedIssues(IssueManager issueManager, IssueHandler handler) {
        Iterator<Issue> issues = issueManager.allIssuesIterator();

        while (issues.hasNext()) {
            Issue issue = issues.next();

            if (issue.isUnresolved()) {
                handler.accept(issue);
            }
        }
    }
///END extract-method-for-ticket-manager

}
