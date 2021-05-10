package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;

public class AnnenForelderDto {

    private Fødselsnummer foedselsnummer;

    private boolean kanIkkeOppgiAnnenForelder = true;

    private KanIkkeOppgiBegrunnelse kanIkkeOppgiBegrunnelse = new KanIkkeOppgiBegrunnelse();

    private boolean sokerHarAleneomsorg = false;

    private boolean denAndreForelderenHarRettPaForeldrepenger = true;


    public static AnnenForelderDto of(Fødselsnummer fnr, boolean sokerHarAleneomsorg) {
        var annenForelderDto = new AnnenForelderDto();
        annenForelderDto.setFoedselsnummer(fnr);
        annenForelderDto.setKanIkkeOppgiAnnenForelder(false);
        annenForelderDto.setKanIkkeOppgiBegrunnelse(null);
        annenForelderDto.setSokerHarAleneomsorg(sokerHarAleneomsorg);
        return annenForelderDto;
    }

    public Fødselsnummer getFoedselsnummer() {
        return foedselsnummer;
    }

    public void setFoedselsnummer(Fødselsnummer foedselsnummer) {
        this.foedselsnummer = foedselsnummer;
    }

    public boolean isKanIkkeOppgiAnnenForelder() {
        return kanIkkeOppgiAnnenForelder;
    }

    public void setKanIkkeOppgiAnnenForelder(boolean kanIkkeOppgiAnnenForelder) {
        this.kanIkkeOppgiAnnenForelder = kanIkkeOppgiAnnenForelder;
    }

    public KanIkkeOppgiBegrunnelse getKanIkkeOppgiBegrunnelse() {
        return kanIkkeOppgiBegrunnelse;
    }

    public void setKanIkkeOppgiBegrunnelse(KanIkkeOppgiBegrunnelse kanIkkeOppgiBegrunnelse) {
        this.kanIkkeOppgiBegrunnelse = kanIkkeOppgiBegrunnelse;
    }

    public boolean isSokerHarAleneomsorg() {
        return sokerHarAleneomsorg;
    }

    public void setSokerHarAleneomsorg(boolean sokerHarAleneomsorg) {
        this.sokerHarAleneomsorg = sokerHarAleneomsorg;
    }

    public boolean isDenAndreForelderenHarRettPaForeldrepenger() {
        return denAndreForelderenHarRettPaForeldrepenger;
    }

    public void setDenAndreForelderenHarRettPaForeldrepenger(boolean denAndreForelderenHarRettPaForeldrepenger) {
        this.denAndreForelderenHarRettPaForeldrepenger = denAndreForelderenHarRettPaForeldrepenger;
    }


    public static class KanIkkeOppgiBegrunnelse {

        public String arsak = "UKJENT_FORELDER";
    }
}
