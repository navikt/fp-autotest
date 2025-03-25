package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

public enum DokumentTag {
    FORELDREPENGER_INNVILGET("Innvilgelsesbrev foreldrepenger"),
    FORELDREPENGER_AVSLAG("Avslagsbrev foreldrepenger"),
    FORELDREPENGER_ANNULERING("Annullering av foreldrepenger"),
    FORELDREPENGER_OPPHØR("Opphør foreldrepenger"),
    ENGANGSSTØNAD_INNVILGET("Innvilget engangsstønad"),
    ETTERLYS_INNTEKTSMELDING("Etterlys inntektsmelding"),
    KLAGE_OMGJØRIN("Vedtak om omgjøring av klage"),
    INNTEKSTMELDING("Inntektsmelding"),
    ;

    private String tag;

    DokumentTag(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}
