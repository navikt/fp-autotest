package no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto;

public class RisikovurderingResponse {
    protected String risikoklasse;
    protected String medlFaresignaler;
    protected String iayFaresignaler;

    public String getRisikoklasse() {
        return this.risikoklasse;
    }
}
