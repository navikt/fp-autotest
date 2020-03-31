package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OmsorgsovertakelseÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OverføringÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVilkaarForSykdomBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.*;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadOmsorg;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.*;


@Tag("util")
public class Aksjonspunkter  extends ForeldrepengerTestBase {

    @Test
    public void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_papir() throws Exception{
        var testscenario = opprettTestscenario("500");
        var søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        // Fødselsdato er for 2 uker siden, ved bruk av "500"
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(null, søkerAktørIdent, søkerIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        //Mor Starter uttak ved fødsel
        var inntektsmelding = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                søkerIdent,
                testscenario.getPersonopplysninger().getFødselsdato(),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørIdent, søkerIdent, saksnummer);
    }

    @Test
    @DisplayName("AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE")
    public void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5031() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("172");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE);
    }

    @Test
    @DisplayName("AVKLAR_ADOPSJONSDOKUMENTAJON")
    public void aksjonspunkt_ADOPSJONSSOKNAD_FORELDREPENGER_5004() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("172");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato().minusWeeks(3);

        Adopsjon adopsjon = new Adopsjon();
        adopsjon.setAntallBarn(1);
        adopsjon.setAdopsjonAvEktefellesBarn(false);
        adopsjon.getFoedselsdato().add(fødselsdato);
        adopsjon.setAnkomstdato(fødselsdato);
        adopsjon.setOmsorgsovertakelsesdato(fødselsdato);

        Fordeling fordeling = FordelingErketyper.generiskFordeling(
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepenger(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medRelasjonTilBarnet(adopsjon);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_ADOPSJONSDOKUMENTAJON);
    }

    @Test
    @DisplayName("MANUELL_VURDERING_AV_OMSORGSVILKÅRET")
    public void aksjonspunkt_ADOPSJONSSOKNAD_ENGANGSSTONAD_5011() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID,
                SøkersRolle.MOR, OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_TILLEGGSOPPLYSNINGER);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_VILKÅR_FOR_OMSORGSOVERTAKELSE);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse.setVilkårType(
                saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET);
    }
    @Test
    @DisplayName("VURDER_OPPTJENINGSVILKÅRET")
    public void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5089() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("01");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();

        saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarArbeidsforholdBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_OPPTJENINGSVILKÅRET);
    }
    @Test
    @DisplayName("VURDER_FAKTA_FOR_ATFL_SN")
    public void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("74");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(3);

        Fordeling fordeling = FordelingErketyper.generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL,
                        fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE,
                        fødselsdato, fødselsdato.plusWeeks(10)),
                FordelingErketyper.utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_SØKER,
                        fødselsdato.plusWeeks(10).plusDays(1), fødselsdato.plusWeeks(20).minusDays(1)),
                uttaksperiode(FEDREKVOTE,
                        fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(30))
        ) ;
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);


        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
        saksbehandler.gjenopptaBehandling();

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        AvklarArbeidsforholdBekreftelse arbeidsforholdBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        arbeidsforholdBekreftelse.bekreftArbeidsforholdErRelevant("BEDRIFT AS", true);
        saksbehandler.bekreftAksjonspunkt(arbeidsforholdBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL);
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse.leggTilMottarYtelse(Collections.emptyList());
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);


    }
    @Test
    @DisplayName("VURDER_OM_VILKÅR_FOR_SYKDOM_OPPFYLT")
    public void aksjonspunkt_FAR_FOEDSELSSOKNAD_FORELDREPENGER_5044() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("60");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate termindato = LocalDate.now().plusWeeks(2);

        Fordeling fordeling = FordelingErketyper.generiskFordeling(
                FordelingErketyper.overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, MØDREKVOTE,
                        termindato, termindato.plusWeeks(10)),
                FordelingErketyper.utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_SØKER,
                        termindato.plusWeeks(10).plusDays(1), termindato.plusWeeks(20).minusDays(1)),
                uttaksperiode(FEDREKVOTE,
                        termindato.plusWeeks(20), termindato.plusWeeks(30))

        ) ;
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, søkerAktørIdent, SøkersRolle.FAR)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørIdent, søkerFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                søkerFnr, termindato,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr()
        );
        fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørIdent, søkerFnr, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);


        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_TERMINBEKREFTELSE);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse.setTermindato(termindato);
        avklarFaktaTerminBekreftelse.setAntallBarn(1);
        avklarFaktaTerminBekreftelse.setUtstedtdato(termindato.minusWeeks(3));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_OM_VILKÅR_FOR_SYKDOM_OPPFYLT);
        VurderVilkaarForSykdomBekreftelse vurderVilkaarForSykdomBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderVilkaarForSykdomBekreftelse.class);
        vurderVilkaarForSykdomBekreftelse.setErMorForSykVedFodsel(true);
        saksbehandler.bekreftAksjonspunkt(vurderVilkaarForSykdomBekreftelse);

    }
    @DisplayName("AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE")
    @Test
    public void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_() throws Exception{
        TestscenarioDto testscenario = opprettTestscenario("75");
        var søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(
                LocalDate.now().plusWeeks(3),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR)
                .medSpesiellOpptjening(
                        OpptjeningErketyper.medUtenlandskArbeidsforhold("1222", "NOR")
                );
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE);

        var inntektbeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgnummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var im = lagInntektsmelding(inntektbeløp, testscenario.getPersonopplysninger().getSøkerIdent(), LocalDate.now(),
                orgnummer);
        fordel.sendInnInntektsmelding(im, søkerAktørIdent, søkerIdent, saksnummer);
    }
    @DisplayName("SJEKK_MANGLENDE_FØDSEL")
    @Test
    public void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_5027() throws Exception{
        var testscenario = opprettTestscenario("501");

        var søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørIdent, søkerIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        var inntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgnummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var im = lagInntektsmelding(inntekt, søkerIdent, fødselsdato.minusWeeks(3), orgnummer);
        fordel.sendInnInntektsmelding(im, søkerAktørIdent, søkerIdent, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL);
    }


}
