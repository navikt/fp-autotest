package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet;

public enum SaksbehandlerRolle {
    SAKSBEHANDLER("saksbeh"),
    SAKSBEHANDLER_KODE_6("saksbeh6"),
    SAKSBEHANDLER_KODE_7("saksbeh7"),
    BESLUTTER("beslut"),
    OVERSTYRER("oversty"),
    KLAGEBEHANDLER("klageb"),
    VEILEDER("veil");

    final String kode;

    SaksbehandlerRolle(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
