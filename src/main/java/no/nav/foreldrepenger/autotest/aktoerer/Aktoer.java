package no.nav.foreldrepenger.autotest.aktoerer;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.OpenamJerseyKlient;
import no.nav.foreldrepenger.autotest.util.junit.FpsakTestBaseKlientInstansiererExtension;

public abstract class Aktoer {

    public OpenamJerseyKlient openamJerseyKlient = FpsakTestBaseKlientInstansiererExtension.openamJerseyKlient;

    public void erLoggetInnUtenRolle() {
        erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
    }

    @Step("Logger inn med rolle: {rolle}")
    public void erLoggetInnMedRolle(Rolle rolle) {
        openamJerseyKlient.logInnMedRolle(rolle.getKode());
    }

    public enum Rolle {
        SAKSBEHANDLER("saksbeh"),
        SAKSBEHANDLER_KODE_6("saksbeh6"),
        SAKSBEHANDLER_KODE_7("saksbeh7"),
        BESLUTTER("beslut"),
        OVERSTYRER("oversty"),
        KLAGEBEHANDLER("klageb"),
        VEILEDER("veil");

        String kode;

        Rolle(String kode) {
            this.kode = kode;
        }

        public String getKode() {
            return kode;
        }
    }
}
