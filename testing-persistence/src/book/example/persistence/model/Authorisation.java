package book.example.persistence.model;

import javax.persistence.Embeddable;

@Embeddable
public class Authorisation {
    private String userName;
    private String password;

    public Authorisation(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    protected Authorisation() {
        // For JPA
    }
}
