package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadOmsorg;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepenger;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.overføringsperiode;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaresignalerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVilkaarForSykdomBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;

@Tag("util")
public class Aksjonspunkter extends ForeldrepengerTestBase {

    @Test
    @DisplayName("REGISTRER_PAPIRSØKNAD_FORELDREPENGER")
    public void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5040() {
        var testscenario = opprettTestscenario("500"); // Fødselsdato er for 2 uker siden, ved bruk av "500"
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var saksnummer = fordel.sendInnSøknad(null, søkerAktørIdent, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, null);

        var inntektsmelding = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                søkerIdent,
                testscenario.personopplysninger().fødselsdato(), // Mor Starter uttak ved fødsel
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørIdent, søkerIdent, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.REGISTRER_PAPIRSØKNAD_FORELDREPENGER);
    }

    @Test
    @DisplayName("AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE")
    public void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5031() {
        TestscenarioDto testscenario = opprettTestscenario("172");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE);
    }

    @Test
    @DisplayName("AVKLAR_ADOPSJONSDOKUMENTAJON")
    public void aksjonspunkt_ADOPSJONSSOKNAD_FORELDREPENGER_5004() {
        TestscenarioDto testscenario = opprettTestscenario("172");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato().minusWeeks(3);

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
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_ADOPSJONSDOKUMENTAJON);
    }

    @Test
    @DisplayName("MANUELL_VURDERING_AV_OMSORGSVILKÅRET")
    public void aksjonspunkt_ADOPSJONSSOKNAD_ENGANGSSTONAD_5011() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        String søkerAktørID = testscenario.personopplysninger().søkerAktørIdent();
        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID,
                SøkersRolle.MOR, OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);

        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse.setVilkårType(
                saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET);
    }

    @Test
    @DisplayName("VURDER_OPPTJENINGSVILKÅRET")
    public void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5089() {
        TestscenarioDto testscenario = opprettTestscenario("01");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarArbeidsforholdBekreftelse.class);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_OPPTJENINGSVILKÅRET);
    }

    @Test
    @DisplayName("VURDER_FAKTA_FOR_ATFL_SN")
    public void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER() {
        TestscenarioDto testscenario = opprettTestscenario("501");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(3);

        Fordeling fordeling = FordelingErketyper.generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL,
                        fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE,
                        fødselsdato, fødselsdato.plusWeeks(10)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_SØKER,
                        fødselsdato.plusWeeks(10).plusDays(1), fødselsdato.plusWeeks(20).minusDays(1)),
                uttaksperiode(FEDREKVOTE,
                        fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(30)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordeling);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
        saksbehandler.gjenopptaBehandling();

        AvklarArbeidsforholdBekreftelse arbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        arbeidsforholdBekreftelse.bekreftArbeidsforholdErAktivt("910909088", true);
        saksbehandler.bekreftAksjonspunkt(arbeidsforholdBekreftelse);

        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse.leggTilMottarYtelse(Collections.emptyList());
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

    }

    @Test
    @DisplayName("VURDER_OM_VILKÅR_FOR_SYKDOM_OPPFYLT")
    public void aksjonspunkt_FAR_FOEDSELSSOKNAD_FORELDREPENGER_5044() {
        TestscenarioDto testscenario = opprettTestscenario("86");
        String søkerAktørIdent = testscenario.personopplysninger().annenpartAktørIdent();
        String søkerFnr = testscenario.personopplysninger().annenpartIdent();
        LocalDate termindato = LocalDate.now().plusWeeks(2);

        Fordeling fordeling = FordelingErketyper.generiskFordeling(
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, MØDREKVOTE,
                        termindato, termindato.plusWeeks(10)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_SØKER,
                        termindato.plusWeeks(10).plusDays(1), termindato.plusWeeks(20).minusDays(1)),
                uttaksperiode(FEDREKVOTE,
                        termindato.plusWeeks(20), termindato.plusWeeks(30))

        );
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, søkerAktørIdent, SøkersRolle.FAR)
                .medFordeling(fordeling);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørIdent, søkerFnr,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(
                testscenario.scenariodataAnnenpartDto().inntektskomponentModell().inntektsperioder().get(0)
                        .beløp(),
                søkerFnr, termindato,
                testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørIdent, søkerFnr, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse.setUtstedtdato(termindato.minusWeeks(3));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        VurderVilkaarForSykdomBekreftelse vurderVilkaarForSykdomBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderVilkaarForSykdomBekreftelse.class);
        vurderVilkaarForSykdomBekreftelse.setErMorForSykVedFodsel(true);
        saksbehandler.bekreftAksjonspunkt(vurderVilkaarForSykdomBekreftelse);

    }

    @DisplayName("AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE")
    @Test
    public void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_() {
        TestscenarioDto testscenario = opprettTestscenario("75");
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(
                LocalDate.now().plusWeeks(3),
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR)
                        .medSpesiellOpptjening(
                                OpptjeningErketyper.medUtenlandskArbeidsforhold("1222", "NOR"));
        var saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, null);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE);

        var inntektbeløp = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgnummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var im = lagInntektsmelding(inntektbeløp, testscenario.personopplysninger().søkerIdent(), LocalDate.now(),
                orgnummer);
        fordel.sendInnInntektsmelding(im, søkerAktørIdent, søkerIdent, saksnummer);
    }

    @DisplayName("SJEKK_MANGLENDE_FØDSEL")
    @Test
    public void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_5027() {
        var testscenario = opprettTestscenario("501");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, null);

        var inntekt = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgnummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var im = lagInntektsmelding(inntekt, søkerIdent, fødselsdato.minusWeeks(3), orgnummer);
        fordel.sendInnInntektsmelding(im, søkerAktørIdent, søkerIdent, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL);
    }

    // Denne testen er avhengig av at fprisk kjører!
    @Test
    @DisplayName("5095 – VURDER_FARESIGNALER_KODE")
    public void aksjonspunkt_VURDER_FARESIGNALER_KODE_5095() {
        var testscenario = opprettTestscenario("522");
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var søkerAktørId = testscenario.personopplysninger().søkerAktørIdent();
        var søkerFnr = testscenario.personopplysninger().søkerIdent();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørId, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var månedsinntekt = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        var orgNummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(månedsinntekt, søkerFnr, fpStartdato, orgNummer);
        fordel.sendInnInntektsmelding(
                inntektsmelding,
                søkerAktørId,
                søkerFnr,
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse.leggTilInntekt(800_000, 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var vurderFaresignalerDto = saksbehandler.hentAksjonspunktbekreftelse(VurderFaresignalerDto.class);
//        vurderFaresignalerDto.setHarInnvirketBehandlingen(true);
//        vurderFaresignalerDto.setBegrunnelse("HELLO");
//        saksbehandler.bekreftAksjonspunkt(vurderFaresignalerDto);
    }
}
