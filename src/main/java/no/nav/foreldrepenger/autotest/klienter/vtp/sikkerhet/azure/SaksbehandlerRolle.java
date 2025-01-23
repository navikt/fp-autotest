package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure;

public enum SaksbehandlerRolle {
    SAKSBEHANDLER("S123456"),
    SAKSBEHANDLER_KODE_6("S666666"),
    SAKSBEHANDLER_KODE_7("S777777"),
    BESLUTTER("B123456"),
    OVERSTYRER("O123456"),
    OPPGAVESTYRER("L123456"),
    KLAGEBEHANDLER("K123456"),
    VEILEDER("V123456");

    final String kode;

    SaksbehandlerRolle(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
