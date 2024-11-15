package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

public enum HistorikkTyper {
    BREV_SENT("Brev sendt"),
    BREV_BESTILT("Brev bestilt"),
    BEH_STARTET("Behandling startet"),
    BEH_MAN_GJEN("Gjenoppta behandling"),
    BEH_GJEN("Behandling gjenopptatt"),
    BEH_VENT("Behandling på vent"),
    VEDLEGG_MOTTATT("Vedlegg mottatt"),
    REVURD_OPPR("Revurdering opprettet"),
    SPOLT_TILBAKE("Behandlingen er flyttet"),
    AVBRUTT_BEH("Behandling er henlagt"),
    VEDTAK_FATTET("Vedtak fattet", "VEDTAK");

    private final String tittel;
    private final String skjermlenke;

    HistorikkTyper(String tittel) {
        this(tittel, null);
    }

    HistorikkTyper(String tittel, String skjermlenke) {
        this.tittel = tittel;
        this.skjermlenke = skjermlenke;
    }

    public String tittel() {
        return tittel;
    }

    public String skjermlenke() {
        return skjermlenke;
    }

}
