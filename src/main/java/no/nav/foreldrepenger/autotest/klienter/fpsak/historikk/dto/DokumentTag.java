package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

public enum DokumentTag {
    FORELDREPENGER_INNVILGET("Innvilgelsesbrev foreldrepenger"),
    FORELDREPENGER_ANNULERING("Annullering av foreldrepenger"),
    ENGANGSSTØNAD_INNVILGET("Innvilget engangsstønad"),
    ;

    private String tag;

    DokumentTag(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}
