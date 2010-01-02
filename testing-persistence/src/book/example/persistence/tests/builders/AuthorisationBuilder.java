package book.example.persistence.tests.builders;

import book.example.persistence.model.Authorisation;

public class AuthorisationBuilder extends AbstractBuilder<AuthorisationBuilder, Authorisation> {
    private String userName = "user";
    private String password = "pass";

    public Authorisation build() {
        return new Authorisation(userName, password);
    }

    AuthorisationBuilder withUserName(String userName) {
        AuthorisationBuilder other = clone();
        other.userName = userName;
        return other;
    }

    AuthorisationBuilder withPassword(String password) {
        AuthorisationBuilder other = clone();
        other.password = password;
        return other;
    }
}
