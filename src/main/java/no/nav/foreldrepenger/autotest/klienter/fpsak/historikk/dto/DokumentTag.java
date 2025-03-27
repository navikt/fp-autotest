package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

public enum DokumentTag {
    ENGANGSSTØNAD_INNVILGET("Innvilget engangsstønad"),

    FORELDREPENGER_INNVILGET("Innvilgelsesbrev foreldrepenger"),
    FORELDREPENGER_AVSLAG("Avslagsbrev foreldrepenger"),
    FORELDREPENGER_ANNULERING("Annullering av foreldrepenger"),
    FORELDREPENGER_OPPHØR("Opphør foreldrepenger"),

    SVANGERSKAPSPENGER_INNVILGET("Innvilgelsesbrev svangerskapspenger"),

    INNTEKSTMELDING("Inntektsmelding"),

    ETTERLYS_INNTEKTSMELDING("Etterlys inntektsmelding"),
    KLAGE_OMGJØRIN("Vedtak om omgjøring av klage"),
    ;

    private String tag;

    DokumentTag(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}
