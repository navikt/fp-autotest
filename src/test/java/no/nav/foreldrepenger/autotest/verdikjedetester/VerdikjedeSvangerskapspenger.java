package no.nav.foreldrepenger.autotest.verdikjedetester;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadSvangerskapspengerErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.TilretteleggingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Tilretteleggingsdato;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.OpptjeningErketyper;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand;

@Tag("verdikjede")
class VerdikjedeSvangerskapspenger extends VerdikjedeTestBase {

    @Test
    @DisplayName("1: Mor søker fullt uttak med inntekt under 6G")
    @Description("Mor søker ingen tilrettelegging for en 100% stilling med inntekt over 6G.")
    void morSøkerIngenTilretteleggingInntektOver6GTest() {
        var familie = new Familie("501");
        var mor = familie.mor();
        var tilrettelegginsprosent = 0;
        var termindato = LocalDate.now().plusMonths(3);
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.arbeidsgiverIdentifikasjon();
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(((Orgnummer) orgnummer)));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        int beregnetDagsats = regnUtForventetDagsats(mor.månedsinntekt(), tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen beregnes ut i fra årsinntekten og 100% utbetalingsgrad")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();
    }

    @Test
    @DisplayName("2: Mor søker gradert uttak med inntekt over 6G")
    @Description("Mor søker delvis tilrettelegging for en 100% stilling hvor hun har inntekt over 6G.")
    void morSøkerDelvisTilretteleggingMedInntektOver6GTest() {
        var familie = new Familie("502");
        var mor = familie.mor();
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.arbeidsgiverIdentifikasjon();
        var tilrettelegginsprosent = 40;
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) orgnummer),
                BigDecimal.valueOf(tilrettelegginsprosent));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("En begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var beregnetDagsats = regnUtForventetDagsats(mor.månedsinntekt(), tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();
    }

    @Test
    @DisplayName("3: Mor søk fullt uttak for ett av to arbeidsforhold i samme virksomhet")
    @Description("Mor søker ingen tilrettelegging for ett av to arbeidsforhold i samme virksomhet. Arbeidsgiver leverer to" +
            "inntektsmeldinger med forskjellig arbeidsforholdID, med ulik lønn.")
    void morSøkerFulltUttakForEttAvToArbeidsforholdTest() {
        var familie = new Familie("503");
        var mor = familie.mor();
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0);
        var orgnummer1 = arbeidsforhold1.arbeidsgiverIdentifikasjon();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) orgnummer1));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setSkalBrukesTilFalseForArbeidsforhold(arbeidsforholdene.get(1).arbeidsforholdId());
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var månedsinntekt1 = mor.månedsinntekt((Orgnummer) orgnummer1, arbeidsforhold1.arbeidsforholdId());
        var årsinntekt = (double) månedsinntekt1 * 12;
        var utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt1, tilrettelegginsprosent);
        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        assertThat(beregningsgrunnlagPeriode.getRedusertPrAar())
                .as("Forventer at redusertPrAar er det samme som årsinntekten for den gjeldende arbeidsforhold x utbetalingsgrad")
                .isEqualTo(årsinntekt * utbetalingProsentFaktor);
        assertThat(beregningsgrunnlagPeriode.getDagsats())
                .as("Forventer at dagsatsen bare beregnes ut i fra årsinntekten til det ene arbeidsforholdet og dens utbetalingsgrad!")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer1, 0))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();

    }

    @Test
    @DisplayName("4: Mor kombinert AT/SN søker i to omganger")
    @Description("Mor søker i første omgang bare for AF, hvor AG ønsker full refusjon av innekt over 6G." +
            "To måneder senere sender mor inn ny søknad for SN")
    void morSøkerFørstForATOgSenereForSNTest() {
        var familie = new Familie("511");
        var mor = familie.mor();
        var arbeidsforhold = mor.arbeidsforhold();
        var arbeidsgiverIdentifikator = arbeidsforhold.arbeidsgiverIdentifikasjon();
        var næringsinntekt = mor.næringsinntekt(2018);
        var opptjening = OpptjeningErketyper.egenNaeringOpptjening(
                arbeidsgiverIdentifikator.value(),
                false,
                næringsinntekt,
                false);
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging1 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now().minusMonths(2),
                LocalDate.now().minusMonths(2),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) arbeidsgiverIdentifikator));
        var søknad1 = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging1))
                .medOpptjening(opptjening);
        var saksnummer1 = mor.søk(søknad1.build());

        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingSVP()
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        arbeidsgiver.sendInntektsmeldinger(saksnummer1, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer1);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var månedsinntekt = mor.månedsinntekt();
        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(arbeidsgiverIdentifikator, 100))
                .as("Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!")
                .isTrue();

        /* SØKNAD 2 */
        var tilrettelegging2 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.selvstendigNæringsdrivende());
        var søknad2 = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging2))
                .medOpptjening(opptjening);
        var saksnummer2 = mor.søk(søknad2.build());

        saksbehandler.hentFagsak(saksnummer2);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var avklarFaktaFødselOgTilrettelegging2 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging2.setBegrunnelse("Begrunnelse");
        avklarFaktaFødselOgTilrettelegging2.getBekreftetSvpArbeidsforholdList().get(0).setTilretteleggingBehovFom(LocalDate.now().minusDays(7));
        avklarFaktaFødselOgTilrettelegging2.getBekreftetSvpArbeidsforholdList().get(0).setTilretteleggingDatoer(
                List.of(new Tilretteleggingsdato(LocalDate.now().minusDays(7), TilretteleggingType.INGEN_TILRETTELEGGING, BigDecimal.valueOf(100))));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging2);

        var bekreftSvangerskapspengervilkår2 = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår2.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer2, true);

        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(arbeidsgiverIdentifikator, 100))
                .as("Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!")
                .isTrue();
        var beregningsresultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        assertThat(beregningsresultatPerioder.get(0).getAndeler())
                .as("Andeler for første periode i tilkjent ytelse")
                .hasSize(1);
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(0), AktivitetStatus.ARBEIDSTAKER))
                .as("Forventer aktivitetsstatus for første andel for første periode er AT")
                .isTrue();
        assertThat(beregningsresultatPerioder.get(1).getAndeler())
                .as("Andeler for andre periode i tilkjent ytelse")
                .hasSize(2);
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1),  AktivitetStatus.ARBEIDSTAKER))
                .as("Forventer aktivitetsstatus for første andel for andre periode er AT")
                .isTrue();
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1), AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .as("Forventer aktivitetsstatus for andre andel for andre periode er SN")
                .isTrue();


        var beregningsresultatPeriodeAndeler = saksbehandler.hentBeregningsresultatPerioderMedAndelISN();
        assertThat(beregningsresultatPeriodeAndeler.get(0).getRefusjon())
                .as("Dagsats til refusjon")
                .isZero();
        assertThat(beregningsresultatPeriodeAndeler.get(0).getTilSoker())
                .as("Dagsats til søker")
                .isZero();
    }

    @Test
    @DisplayName("5: Mor har flere AG og søker fullt uttak for begge AFene")
    @Description("Mor søker ingen tilrettelegging for begge arbeidsforholdene. Begge arbeidsgiverene ønsker 100% reufsjon." +
            "Inntekten i disse to arbeidsforholdene er samlet over 6G hvor fordelingen er 2/3 og 1/3 av inntekten.")
    void morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest() {
        var familie = new Familie("504");
        var mor = familie.mor();
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0);
        var orgnummer1 = arbeidsforhold1.arbeidsgiverIdentifikasjon();
        var arbeidsforhold2 = arbeidsforholdene.get(1);
        var orgnummer2 = arbeidsforhold2.arbeidsgiverIdentifikasjon();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging1 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) orgnummer1));
        var tilrettelegging2 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) orgnummer2));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging1, tilrettelegging2));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere().toList();
        var arbeidsgiver1 = arbeidsgivere.get(0);
        var inntektsmedling1 = arbeidsgiver1.lagInntektsmeldingSVP()
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        arbeidsgiver1.sendInntektsmeldinger(saksnummer, inntektsmedling1);
        var arbeidsgiver2 = arbeidsgivere.get(1);
        var inntektsmedling2 = arbeidsgiver2.lagInntektsmeldingSVP()
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        arbeidsgiver2.sendInntektsmeldinger(saksnummer, inntektsmedling2);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);


        var månedsinntekt1 = mor.månedsinntekt(orgnummer1);
        var månedsinntekt2 = mor.månedsinntekt(orgnummer2);
        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt1 + månedsinntekt2, tilrettelegginsprosent);
        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        assertThat(beregningsgrunnlagPeriode.getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!")
                .isEqualTo(beregnetDagsats);

        var månedsinntekt = månedsinntekt1 + månedsinntekt2;
        var prosentTilArbeidsgiver1 = ((double) månedsinntekt1 / månedsinntekt) * 100;
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer1, prosentTilArbeidsgiver1))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();

        var prosentTilArbeidforhold2 = 100 - prosentTilArbeidsgiver1;
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer2, prosentTilArbeidforhold2))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();
    }

    @Test
    @DisplayName("6: Verifiser innsyn har korrekt data")
    @Description("Verifiserer at innsyn har korrekt data og sammenligner med vedtaket med det saksbehandlerene ser")
    void mor_innsyn_verifsere() {
        var familie = new Familie("502");
        var mor = familie.mor();
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.arbeidsgiverIdentifikasjon();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) orgnummer),
                BigDecimal.valueOf(40));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);

        var svpSaker = mor.innsyn().hentSaker().svangerskapspenger();
        assertThat(svpSaker).hasSize(1);
        var svpSak = svpSaker.stream().findFirst().orElseThrow();
        assertThat(svpSak.saksnummer().value()).isEqualTo(saksnummer.value());
        assertThat(svpSak.sakAvsluttet()).isFalse();
        assertThat(svpSak.åpenBehandling().tilstand()).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
        assertThat(svpSak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(svpSak.familiehendelse().fødselsdato()).isNull();
        assertThat(svpSak.familiehendelse().antallBarn()).isZero();
        assertThat(svpSak.familiehendelse().omsorgsovertakelse()).isNull();

        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("En begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var svpSakerEtterVedtak = mor.innsyn().hentSaker().svangerskapspenger();
        assertThat(svpSakerEtterVedtak).hasSize(1);
        var svpSakEtterVedtak = svpSakerEtterVedtak.stream().findFirst().orElseThrow();
        assertThat(svpSakEtterVedtak.saksnummer().value()).isEqualTo(saksnummer.value());
        assertThat(svpSakEtterVedtak.sakAvsluttet()).isFalse();
        assertThat(svpSakEtterVedtak.åpenBehandling()).isNull();
        assertThat(svpSakEtterVedtak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(svpSakEtterVedtak.familiehendelse().fødselsdato()).isNull();
        assertThat(svpSakEtterVedtak.familiehendelse().antallBarn()).isZero();
        assertThat(svpSakEtterVedtak.familiehendelse().omsorgsovertakelse()).isNull();
    }

    private Integer regnUtForventetDagsats(Integer samletMånedsbeløp, Integer tilrettelegginsprosent) {
        var årsinntekt = Double.valueOf(samletMånedsbeløp) * 12;
        var seksG = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG() * 2 * 6;
        var utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        if (årsinntekt > seksG) {
            årsinntekt = seksG;
        }
        return ((int) Math.round((årsinntekt * utbetalingProsentFaktor) / 260));
    }
}
