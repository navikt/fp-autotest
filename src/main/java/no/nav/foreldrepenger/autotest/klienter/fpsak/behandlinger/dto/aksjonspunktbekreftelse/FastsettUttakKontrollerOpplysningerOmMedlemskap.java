package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5074")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettUttakKontrollerOpplysningerOmMedlemskap extends AksjonspunktBekreftelse {

    private List<UttakResultatPeriode> perioder = new ArrayList<>();

    public FastsettUttakKontrollerOpplysningerOmMedlemskap() {
        super();
    }

    public void leggTilUttakPeriode(UttakResultatPeriode uttakPeriode) {
        perioder.add(uttakPeriode);
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        for (UttakResultatPeriode uttakPeriode : behandling.hentUttaksperioder()) {
            uttakPeriode.setBegrunnelse("Begrunnelse");
            leggTilUttakPeriode(uttakPeriode);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettUttakKontrollerOpplysningerOmMedlemskap that = (FastsettUttakKontrollerOpplysningerOmMedlemskap) o;
        return Objects.equals(perioder, that.perioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perioder);
    }

    @Override
    public String toString() {
        return "FastsettUttakKontrollerOpplysningerOmMedlemskap{" +
                "perioder=" + perioder +
                "} " + super.toString();
    }
}
