package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;

public class ApForeldelseDetaljer {

    protected String begrunnelse;
    protected LocalDate fraDato;
    protected LocalDate tilDato;
    protected ForeldelseVurderingType foreldelseVurderingType;
    protected LocalDate foreldelsesfrist;
    protected LocalDate oppdagelsesDato;

    public ApForeldelseDetaljer(LocalDate fraDato, LocalDate tilDato) {
        this.begrunnelse = "Dette er en foreldelsesvurdering skrevet av Autotest!";
        this.fraDato = fraDato;
        this.tilDato = tilDato;
        this.foreldelseVurderingType = ForeldelseVurderingType.IKKE_VURDERT;
    }

    public LocalDate getFraDato() {
        return fraDato;
    }

    public void settIkkeForeldet() {
        this.foreldelseVurderingType = ForeldelseVurderingType.IKKE_FORELDET;
        this.foreldelsesfrist = null;
        this.oppdagelsesDato = null;
    }

    public void settForeldet() {
        this.foreldelseVurderingType = ForeldelseVurderingType.FORELDET;
        this.foreldelsesfrist = tilDato.plusYears(3);
        this.oppdagelsesDato = null;
    }

    public void settTilleggsfrist(LocalDate oppdagelsesDato) {
        this.foreldelseVurderingType = ForeldelseVurderingType.TILLEGGSFRIST;
        this.oppdagelsesDato = oppdagelsesDato;
        this.foreldelsesfrist = oppdagelsesDato.plusYears(1);
    }
}
