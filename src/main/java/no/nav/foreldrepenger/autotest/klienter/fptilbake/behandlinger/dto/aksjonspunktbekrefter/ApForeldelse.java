package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

/**
 * Aksjonspunkt 5003 VURDER_FORELDELSE. Utledes av fptilbake når en kravgrunnlagsperiode starter
 * før dagens dato minus foreldelsesfristen (P30M), eller når revurderingen har årsak
 * RE_OPPLYSNINGER_OM_FORELDELSE.
 */
public class ApForeldelse extends AksjonspunktBekreftelse {

    private static final Period FORELDELSESFRIST = Period.ofMonths(30);

    protected final List<ApForeldelseDetaljer> foreldelsePerioder = new ArrayList<>();

    public ApForeldelse() {
        setBegrunnelse("Dette er en begrunnelse dannet av Autotest!");
    }

    public void addForeldelsePeriode(LocalDate fom, LocalDate tom) {
        this.foreldelsePerioder.add(new ApForeldelseDetaljer(fom, tom));
    }

    /**
     * Vurderer perioder som starter før foreldelsesfristen som foreldet, øvrige som ikke foreldet.
     */
    public void addGeneriskVurdering() {
        var frist = LocalDate.now().minus(FORELDELSESFRIST);
        for (ApForeldelseDetaljer periode : foreldelsePerioder) {
            if (periode.getFraDato().isBefore(frist)) {
                periode.settForeldet();
            } else {
                periode.settIkkeForeldet();
            }
        }
    }

    /**
     * Vurderer perioder som starter før foreldelsesfristen med tilleggsfrist (10-årsregelen), slik at
     * de fortsatt går videre til vilkårsvurdering. Øvrige perioder settes til ikke foreldet.
     */
    public void addVurderingMedTilleggsfrist(LocalDate oppdagelsesDato) {
        var frist = LocalDate.now().minus(FORELDELSESFRIST);
        for (ApForeldelseDetaljer periode : foreldelsePerioder) {
            if (periode.getFraDato().isBefore(frist)) {
                periode.settTilleggsfrist(oppdagelsesDato);
            } else {
                periode.settIkkeForeldet();
            }
        }
    }

    @Override
    public String aksjonspunktKode() {
        return "5003";
    }
}
