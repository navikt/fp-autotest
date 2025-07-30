package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

public enum HistorikkType {
    BREV_SENDT("Brev er sendt"),
    BEH_STARTET("Behandling er startet"),
    BEH_MAN_GJEN("Behandlingen er gjenopptatt"),
    BEH_GJEN("Køet behandling er gjenopptatt"),
    BEH_VENT("Behandlingen er satt på vent"),
    BYTT_ENHET("Bytt enhet"),
    VEDLEGG_MOTTATT("Vedlegg er mottatt"),
    REVURD_OPPR("Revurdering er opprettet"),
    SPOLT_TILBAKE("Behandlingen er flyttet"),
    AVBRUTT_BEH("Behandling er henlagt"),
    VEDTAK_FATTET("Vedtak er fattet", "VEDTAK"),
    MIN_SIDE_ARBEIDSGIVER("Min side - arbeidsgiver");

    private final String tittel;
    private final String skjermlenke;

    HistorikkType(String tittel) {
        this(tittel, null);
    }

    HistorikkType(String tittel, String skjermlenke) {
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
