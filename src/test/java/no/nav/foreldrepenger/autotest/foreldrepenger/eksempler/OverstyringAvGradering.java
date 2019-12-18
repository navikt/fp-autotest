package no.nav.foreldrepenger.autotest.foreldrepenger.eksempler;

import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrUttaksperioder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

import java.time.LocalDate;

public class OverstyringAvGradering extends FpsakTestBase {

    public void skalKunneOverstyreGradering() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        SøknadBuilder søknad = SøknadErketyper.engangstønadsøknadTerminErketype(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                1,
                LocalDate.now().plusWeeks(3));

        fordel.erLoggetInnUtenRolle();
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);


        saksbehandler.hentFagsak(saksnummer);
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(0).getGraderingInnvilget(), "Gradering var ikke invilget. forventet invilget");

        saksbehandler.aksjonspunktBekreftelse(OverstyrUttaksperioder.class)
                .bekreftPeriodeGraderingErIkkeOppfylt(saksbehandler.valgtBehandling.hentUttaksperiode(0), Kode.lagBlankKode());
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(OverstyrUttaksperioder.class);

        verifiser(!saksbehandler.valgtBehandling.hentUttaksperiode(0).getGraderingInnvilget(), "Gradering var invilget. forventet ikke invilget");
        verifiserLikhet(saksbehandler.valgtBehandling.hentUttaksperiode(0).getGradertArbeidsprosent(), 75);
        verifiserLikhet(saksbehandler.valgtBehandling.hentUttaksperiode(0).getGradertArbeidsprosent(), 25);
    }
}
