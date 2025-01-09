package no.nav.foreldrepenger.autotest.klienter.fptilbake.historikk;

public enum HistorikkTypeFptilbake {
    BEH_MAN_GJEN("Gjenoppta behandling"),
    BEH_GJEN("Behandling gjenopptatt"),
    BEH_VENT("Behandling på vent");

    private final String tittel;
    private final String skjermlenke;

    HistorikkTypeFptilbake(String tittel) {
        this(tittel, null);
    }

    HistorikkTypeFptilbake(String tittel, String skjermlenke) {
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
