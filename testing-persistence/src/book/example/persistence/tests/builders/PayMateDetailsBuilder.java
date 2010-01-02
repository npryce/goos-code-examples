package book.example.persistence.tests.builders;

import book.example.persistence.model.PayMateDetails;

public class PayMateDetailsBuilder extends AbstractBuilder<PayMateDetailsBuilder, PayMateDetails> {
    private AuthorisationBuilder authorisationBuilder = new AuthorisationBuilder();

    public PayMateDetails build() {
        return new PayMateDetails(authorisationBuilder.build());
    }

    public PayMateDetailsBuilder withAuthorisation(AuthorisationBuilder authorisationBuilder) {
        PayMateDetailsBuilder other = clone();
        other.authorisationBuilder = authorisationBuilder;
        return other;
    }
}
