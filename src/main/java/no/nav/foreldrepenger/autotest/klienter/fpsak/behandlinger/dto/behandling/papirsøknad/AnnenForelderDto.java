package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

public class AnnenForelderDto {

    public boolean kanIkkeOppgiAnnenForelder = true;

    public KanIkkeOppgiBegrunnelse kanIkkeOppgiBegrunnelse = new KanIkkeOppgiBegrunnelse();

    public boolean søkerHarAleneomsorg = false;

    public boolean denAndreForelderenHarRettPåForeldrepenger = true;

    public static class KanIkkeOppgiBegrunnelse {

        public String årsak = "UKJENT_FORELDER";
    }
}
