package no.nav.foreldrepenger.autotest.verdikjedetester;

import static java.util.function.Predicate.not;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_4;
import static no.nav.foreldrepenger.autotest.brev.BrevFormateringUtils.formaterKroner;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.AvklartOpphold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.TilretteleggingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Tilretteleggingsdato;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.svangerskapspenger.AvtaltFerieDto;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.innsyn.BehandlingTilstand;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;
import no.nav.foreldrepenger.generator.soknad.builder.TilretteleggingBehovBuilder;
import no.nav.foreldrepenger.generator.soknad.maler.ArbeidsforholdMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadSvangerskapspengerMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("verdikjede")
class VerdikjedeSvangerskapspenger extends VerdikjedeTestBase {

    protected static final Prosent HUNDRE_PROSENT_AV_BEREGNET_INNTEKT = Prosent.valueOf(100);

    @Test
    @DisplayName("1: Mor søker fullt uttak med inntekt under 6G")
    @Description("Mor søker ingen tilrettelegging for en 100% stilling med inntekt over 6G.")
    void morSøkerIngenTilretteleggingInntektOver6GTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var mor = familie.mor();
        var tilrettelegginsprosent = 0.0;
        var termindato = LocalDate.now().plusMonths(3);
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.arbeidsgiverIdentifikasjon();
        var tilrettelegging = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet(((Orgnummer) orgnummer)),
                LocalDate.now()).ingen(LocalDate.now()).build();
        var søknad = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato, List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerSVP(saksnummer);

        var harRefusjon = false;
        validerInnsendtInntektsmeldingSvangerskapspenger(mor.fødselsnummer(), LocalDate.now(), mor.månedsinntekt(), harRefusjon);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        int beregnetDagsats = regnUtForventetDagsats(mor.månedsinntekt(), tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats()).as(
                "Forventer at dagsatsen beregnes ut i fra årsinntekten og 100% utbetalingsgrad").isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Foventer at hele den utbetalte dagsatsen går til søker!").isTrue();

        var snittPerMånedUtbetaltDirekte = Math.min(SEKS_G_2025, mor.månedsinntekt() * 12) / 260 * 260 / 12;
        sjekkInnvilgetBrev(mor.fødselsnummer(), mor.månedsinntekt(), harRefusjon, false, snittPerMånedUtbetaltDirekte, false);
    }

    @Test
    @DisplayName("2: Mor søker gradert uttak med inntekt over 6G")
    @Description("Mor søker delvis tilrettelegging for en 100% stilling hvor hun har inntekt over 6G. Hun har en periode med ferie midt i. "
            + "Endrer tilretteleggingen med ny fra dato og arbeidsprosent. Ferien skal beholdes.")
    void morSøkerDelvisTilretteleggingMedInntektOver6GTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().frilans(LocalDate.now().minusYears(2)).arbeidMedOpptjeningOver6G().build()).build())
                .build();

        var mor = familie.mor();
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.arbeidsgiverIdentifikasjon();
        var tilrettelegginsprosent = 40.0;
        var termindato = LocalDate.now().plusMonths(3);
        var arbeidsforholdDto = ArbeidsforholdMaler.virksomhet(((Orgnummer) orgnummer));
        var tilrettelegging = new TilretteleggingBehovBuilder(arbeidsforholdDto, LocalDate.now()).delvis(LocalDate.now(),
                tilrettelegginsprosent).build();
        var søknad = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato, List.of(tilrettelegging));
        //Legger til opphold som ferie
        var startDatoFerie = LocalDate.now().plusMonths(2);
        var sluttdatoFerie = startDatoFerie.plusWeeks(1);
        var avtaltFerie = new AvtaltFerieDto(arbeidsforholdDto, startDatoFerie, sluttdatoFerie);
        var saksnummer = mor.søk(søknad.medAvtaltFerie(List.of(avtaltFerie)));

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerSVP(saksnummer);

        var harRefusjon = false;
        validerInnsendtInntektsmeldingSvangerskapspenger(mor.fødselsnummer(), LocalDate.now(), mor.månedsinntekt(), harRefusjon);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("En begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var beregnetDagsats = regnUtForventetDagsats(mor.månedsinntekt(), tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats()).as(
                        "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Foventer at hele den utbetalte dagsatsen går til søker!").isTrue();
        //Verifiserer at ferie er lagt til i avklarte opphold
        var avklarteOppholdsperioder = avklarFaktaFødselOgTilrettelegging.getBekreftetSvpArbeidsforholdList()
                .getFirst()
                .getAvklarteOppholdPerioder();
        assertThat(avklarteOppholdsperioder).first()
                .matches(not(AvklartOpphold::forVisning), "avklart opphold skal være redigerbart, dvs ikke for visning")
                .matches(l -> l.oppholdKilde() == AvklartOpphold.SvpOppholdKilde.SØKNAD, "søknad er kilde til oppholdet")
                .matches(l -> Objects.equals(l.fom(), startDatoFerie), "fom datoer er like")
                .matches(l -> Objects.equals(l.tom(), sluttdatoFerie), "tom datoer er like");

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        int dagsats = BigDecimal.valueOf(Math.min(SEKS_G_2025, mor.månedsinntekt() * 12))
                .divide(BigDecimal.valueOf(260), RoundingMode.HALF_UP)
                .intValue(); //3004
        int dagsatsAvkortet = (int) (dagsats * (100 - tilrettelegginsprosent) / 100);
        int snittPerMånedAvkortet = dagsatsAvkortet * 260 / 12;

        sjekkInnvilgetBrev(mor.fødselsnummer(), mor.månedsinntekt(), harRefusjon, false, snittPerMånedAvkortet, true);

        /* SØKNAD 2 */
        var tilretteleggingeNyPeriode = new TilretteleggingBehovBuilder(arbeidsforholdDto, LocalDate.now()).delvis(
                sluttdatoFerie.minusDays(1), 30.0).build();
        var søknad2 = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato, List.of(tilretteleggingeNyPeriode));
        var saksnummer2 = mor.søk(søknad2);

        saksbehandler.hentFagsak(saksnummer2);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var avklarFaktaFødselOgTilrettelegging2 = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging2.setBegrunnelse("Ny Periode og ferie skal være lik som forrige behandling");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging2);

        var bekreftSvangerskapspengervilkår2 = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår2.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår2);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        //Sjekker at ferie fortsatt er lik som på første behandling, men med kile tidligere vedtak
        var avklarteOppholdsperioderBeh2 = avklarFaktaFødselOgTilrettelegging2.getBekreftetSvpArbeidsforholdList()
                .getFirst()
                .getAvklarteOppholdPerioder();
        assertThat(avklarteOppholdsperioderBeh2).first()
                .matches(not(AvklartOpphold::forVisning), "avklart opphold skal være redigerbart, dvs ikke for visning")
                .matches(l -> l.oppholdKilde() == AvklartOpphold.SvpOppholdKilde.TIDLIGERE_VEDTAK, "søknad er kilde til oppholdet")
                .matches(l -> Objects.equals(l.fom(), startDatoFerie), "fom datoer er like")
                .matches(l -> Objects.equals(l.tom(), avtaltFerie.tom()), "tom datoer er like");
    }

    @Test
    @DisplayName("3: Mor søk fullt uttak for ett av to arbeidsforhold i samme virksomhet")
    @Description("Mor søker ingen tilrettelegging for ett av to arbeidsforhold i samme virksomhet. Arbeidsgiver leverer to"
            + "inntektsmeldinger med forskjellig arbeidsforholdID, med ulik lønn.")
    @Disabled
    void morSøkerFulltUttakForEttAvToArbeidsforholdTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", 40, LocalDate.now().minusYears(2), 480_000)
                        .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", 60, LocalDate.now().minusYears(4), null)
                        .build()).build())
                .build();

        var mor = familie.mor();
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.getFirst();
        var orgnummer1 = arbeidsforhold1.arbeidsgiverIdentifikasjon();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0.0;
        var tilrettelegging = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet((Orgnummer) orgnummer1),
                LocalDate.now()).ingen(LocalDate.now()).build();
        var søknad = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato, List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad);

        var arbeidsgivere = mor.arbeidsgivere();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging.setSkalBrukesTilFalseForArbeidsforhold(arbeidsforholdene.get(1).arbeidsforholdId());
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, true, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var månedsinntekt1 = mor.månedsinntekt((Orgnummer) orgnummer1, arbeidsforhold1.arbeidsforholdId());
        var årsinntekt = (double) månedsinntekt1 * 12;
        var utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt1, tilrettelegginsprosent);
        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0);
        assertThat(beregningsgrunnlagPeriode.getRedusertPrAar()).as(
                        "Forventer at redusertPrAar er det samme som årsinntekten for den gjeldende arbeidsforhold x utbetalingsgrad")
                .isEqualTo(årsinntekt * utbetalingProsentFaktor);
        assertThat(beregningsgrunnlagPeriode.getDagsats()).as(
                        "Forventer at dagsatsen bare beregnes ut i fra årsinntekten til det ene arbeidsforholdet og dens utbetalingsgrad!")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer1, 0)).as(
                "Foventer at hele den utbetalte dagsatsen går til søker!").isTrue();

    }

    @Test
    @DisplayName("4: Mor kombinert AT/SN søker i to omganger")
    @Description("Mor søker i første omgang bare for AF, hvor AG ønsker full refusjon av innekt over 6G."
            + "To måneder senere sender mor inn ny søknad for SN")
    void morSøkerFørstForATOgSenereForSNTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .selvstendigNæringsdrivende(1_000_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(12), 720_000)
                        .build()).build())
                .barn(LocalDate.now().minusWeeks(8))
                .build();

        var mor = familie.mor();
        var arbeidsforhold = mor.arbeidsforhold();
        var arbeidsgiverIdentifikator = arbeidsforhold.arbeidsgiverIdentifikasjon();
        var næringsinntekt = mor.næringsinntekt();
        var næring = OpptjeningMaler.egenNaeringOpptjening(arbeidsgiverIdentifikator.value(), mor.næringStartdato(), LocalDate.now(),
                false, næringsinntekt, false);
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0.0;
        var tilrettelegging1 = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsgiverIdentifikator),
                LocalDate.now().minusMonths(2)).ingen(LocalDate.now().minusMonths(2)).build();
        var søknad1 = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato, List.of(tilrettelegging1));
        var saksnummer1 = mor.søk(søknad1);

        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingSVP().medRefusjonBeløpPerMnd(HUNDRE_PROSENT_AV_BEREGNET_INNTEKT);
        ventPåInntektsmeldingForespørsel(saksnummer1);
        arbeidsgiver.sendInntektsmelding(saksnummer1, inntektsmelding);

        var harRefusjon = true;
        validerInnsendtInntektsmeldingSvangerskapspenger(mor.fødselsnummer(), LocalDate.now().minusMonths(2), mor.månedsinntekt(),
                harRefusjon);

        saksbehandler.hentFagsak(saksnummer1);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var månedsinntekt = mor.månedsinntekt();
        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats()).as(
                        "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!")
                .isEqualTo(beregnetDagsats);
        assertThat(
                saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(arbeidsgiverIdentifikator, 100)).as(
                "Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        int dagsats = Math.min(SEKS_G_2025, mor.månedsinntekt() * 12) / 260;
        int dagsatsAvkortet = (int) (dagsats * (100 - tilrettelegginsprosent) / 100);
        int snittPerMånedAvkortet = dagsatsAvkortet * 260 / 12;
        sjekkInnvilgetBrev(mor.fødselsnummer(), mor.månedsinntekt(), harRefusjon, false, snittPerMånedAvkortet, false);

        /* SØKNAD 2 */
        var tilrettelegging2 = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.selvstendigNæringsdrivende(),
                LocalDate.now()).ingen(LocalDate.now()).build();
        var søknad2 = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato, List.of(tilrettelegging2))
                .medSelvstendigNæringsdrivendeInformasjon(næring);
        var saksnummer2 = mor.søk(søknad2);

        saksbehandler.hentFagsak(saksnummer2);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var avklarFaktaFødselOgTilrettelegging2 = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging2.setBegrunnelse("Begrunnelse");
        avklarFaktaFødselOgTilrettelegging2.getBekreftetSvpArbeidsforholdList()
                .get(0)
                .setTilretteleggingBehovFom(LocalDate.now().minusDays(7));
        avklarFaktaFødselOgTilrettelegging2.getBekreftetSvpArbeidsforholdList()
                .get(0)
                .setTilretteleggingDatoer(
                        List.of(new Tilretteleggingsdato(LocalDate.now().minusDays(7), TilretteleggingType.INGEN_TILRETTELEGGING,
                                BigDecimal.valueOf(100))));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging2);

        var bekreftSvangerskapspengervilkår2 = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår2.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer2, true, false);

        assertThat(
                saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(arbeidsgiverIdentifikator, 100)).as(
                "Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!").isTrue();
        var beregningsresultatPerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder();
        assertThat(beregningsresultatPerioder.get(0).getAndeler()).as("Andeler for første periode i tilkjent ytelse").hasSize(1);
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(0),
                AktivitetStatus.ARBEIDSTAKER)).as("Forventer aktivitetsstatus for første andel for første periode er AT").isTrue();
        assertThat(beregningsresultatPerioder.get(1).getAndeler()).as("Andeler for andre periode i tilkjent ytelse").hasSize(2);
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1),
                AktivitetStatus.ARBEIDSTAKER)).as("Forventer aktivitetsstatus for første andel for andre periode er AT").isTrue();
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1),
                AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)).as("Forventer aktivitetsstatus for andre andel for andre periode er SN")
                .isTrue();


        var beregningsresultatPeriodeAndeler = saksbehandler.hentBeregningsresultatPerioderMedAndelISN();
        assertThat(beregningsresultatPeriodeAndeler.get(0).getRefusjon()).as("Dagsats til refusjon").isZero();
        assertThat(beregningsresultatPeriodeAndeler.get(0).getTilSoker()).as("Dagsats til søker").isZero();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = alleYtelserFellesAssertionsBuilder(familie.mor().fødselsnummer(),
                saksbehandler.valgtFagsak.saksnummer()).medEgenndefinertAssertion("Nav har endret svangerskapspengene dine")
                .medEgenndefinertAssertion(
                        "Vi utbetaler svangerskapspengene til arbeidsgiveren din fordi du får lønn mens du er borte fra jobb.")
                .medEgenndefinertAssertion("I disse periodene får du svangerskapspenger")
                .medEgenndefinertAssertion("Som næringsdrivende")
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf(P_14_4)
                .medKapittelDuMåMeldeOmEndringer()
                .medKapittelDuHarRettTilKlage();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.SVANGERSKAPSPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("5: Mor har flere AG og søker fullt uttak for begge AFene")
    @Description("Mor søker ingen tilrettelegging for begge arbeidsforholdene. Begge arbeidsgiverene ønsker 100% reufsjon."
            + "Inntekten i disse to arbeidsforholdene er samlet over 6G hvor fordelingen er 2/3 og 1/3 av inntekten.")
    void morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), 720_000)
                        .arbeidsforhold(50, LocalDate.now().minusYears(2), 360_000)
                        .build()).build())
                .build();

        var mor = familie.mor();
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0);
        var orgnummer1 = arbeidsforhold1.arbeidsgiverIdentifikasjon();
        var arbeidsforhold2 = arbeidsforholdene.get(1);
        var orgnummer2 = arbeidsforhold2.arbeidsgiverIdentifikasjon();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging1 = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet((Orgnummer) orgnummer1),
                LocalDate.now()).ingen(LocalDate.now()).build();
        var tilrettelegging2 = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet((Orgnummer) orgnummer2),
                LocalDate.now()).ingen(LocalDate.now()).build();
        var søknad = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato,
                List.of(tilrettelegging1, tilrettelegging2));
        var saksnummer = mor.søk(søknad);
        ventPåInntektsmeldingForespørsel(saksnummer);

        var arbeidsgivere = mor.arbeidsgivere().toList();
        var arbeidsgiver1 = arbeidsgivere.getFirst();
        var inntektsmedling1 = arbeidsgiver1.lagInntektsmeldingSVP().medRefusjonBeløpPerMnd(HUNDRE_PROSENT_AV_BEREGNET_INNTEKT);
        arbeidsgiver1.sendInntektsmelding(saksnummer, inntektsmedling1);

        saksbehandler.hentFagsak(saksnummer);
        var månedsinntekt1 = mor.månedsinntekt(orgnummer1);
        var harRefusjon = true;
        validerInnsendtInntektsmeldingSvangerskapspenger(mor.fødselsnummer(), LocalDate.now(), månedsinntekt1, harRefusjon, 0);

        var arbeidsgiver2 = arbeidsgivere.get(1);
        var inntektsmedling2 = arbeidsgiver2.lagInntektsmeldingSVP().medRefusjonBeløpPerMnd(HUNDRE_PROSENT_AV_BEREGNET_INNTEKT);
        arbeidsgiver2.sendInntektsmelding(saksnummer, inntektsmedling2);

        var månedsinntekt2 = mor.månedsinntekt(orgnummer2);
        validerInnsendtInntektsmeldingSvangerskapspenger(mor.fødselsnummer(), LocalDate.now(), månedsinntekt2, harRefusjon, 1);

        var avklarFaktaFødselOgTilrettelegging = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // var beregnetDagsats = regnUtForventetDagsats(månedsinntekt1 + månedsinntekt2, tilrettelegginsprosent);  Pga 2 andeler med akkurat 1/3 og 2/3 av grunnlaget vil vanlig "enkel" utregning gi 1 krone avvik med årets (2025) G. Setter derfor dagsats manuelt
        var beregnetDagsats = 3003; // "Skulle" blitt 3004
        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0);
        assertThat(beregningsgrunnlagPeriode.getDagsats()).as(
                        "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!")
                .isEqualTo(beregnetDagsats);

        var månedsinntekt = månedsinntekt1 + månedsinntekt2;
        var prosentTilArbeidsgiver1 = ((double) månedsinntekt1 / månedsinntekt) * 100;
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer1,
                prosentTilArbeidsgiver1)).as("Foventer at hele den utbetalte dagsatsen går til søker!").isTrue();

        var prosentTilArbeidforhold2 = 100 - prosentTilArbeidsgiver1;
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer2,
                prosentTilArbeidforhold2)).as("Foventer at hele den utbetalte dagsatsen går til søker!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        sjekkInnvilgetBrev(mor.fødselsnummer(), månedsinntekt, harRefusjon, true, 0, false);
    }

    @Test
    @DisplayName("6: Verifiser innsyn har korrekt data")
    @Description("Verifiserer at innsyn har korrekt data og sammenligner med vedtaket med det saksbehandlerene ser")
    void mor_innsyn_verifsere() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .build();

        var mor = familie.mor();
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.arbeidsgiverIdentifikasjon();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 40.0;
        var tilrettelegging = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet((Orgnummer) orgnummer),
                LocalDate.now()).delvis(LocalDate.now(), tilrettelegginsprosent).build();
        var søknad = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termindato, List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var månedsinntektMor = mor.månedsinntekt();
        var harRefusjon = false;
        validerInnsendtInntektsmeldingSvangerskapspenger(mor.fødselsnummer(), LocalDate.now(), månedsinntektMor, harRefusjon);

        var avklarFaktaFødselOgTilrettelegging = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        var svpSak = mor.innsyn().hentSvpSakMedÅpenBehandlingTilstand(saksnummer, BehandlingTilstand.UNDER_BEHANDLING);
        assertThat(svpSak.saksnummer().value()).isEqualTo(saksnummer.value());
        assertThat(svpSak.sakAvsluttet()).isFalse();
        assertThat(svpSak.åpenBehandling().tilstand()).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
        assertThat(svpSak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(svpSak.familiehendelse().fødselsdato()).isNull();
        assertThat(svpSak.familiehendelse().antallBarn()).isZero();
        assertThat(svpSak.familiehendelse().omsorgsovertakelse()).isNull();

        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("En begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var svpSakEtterVedtak = mor.innsyn().hentSvpSakUtenÅpenBehandling(saksnummer);
        assertThat(svpSakEtterVedtak.saksnummer().value()).isEqualTo(saksnummer.value());
        assertThat(svpSakEtterVedtak.sakAvsluttet()).isFalse();
        assertThat(svpSakEtterVedtak.åpenBehandling()).isNull();
        assertThat(svpSakEtterVedtak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(svpSakEtterVedtak.familiehendelse().fødselsdato()).isNull();
        assertThat(svpSakEtterVedtak.familiehendelse().antallBarn()).isZero();
        assertThat(svpSakEtterVedtak.familiehendelse().omsorgsovertakelse()).isNull();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        int dagsats = Math.min(SEKS_G_2025, månedsinntektMor * 12) / 260;
        int dagsatsAvkortet = (int) Math.round(dagsats * (100 - tilrettelegginsprosent) / 100);
        int snittPerMånedAvkortet = dagsatsAvkortet * 260 / 12;
        sjekkInnvilgetBrev(mor.fødselsnummer(), mor.månedsinntekt(), harRefusjon, false, snittPerMånedAvkortet, false);
    }

    private Integer regnUtForventetDagsats(Integer samletMånedsbeløp, Double tilrettelegginsprosent) {
        var årsinntekt = Double.valueOf(samletMånedsbeløp) * 12;
        var seksG = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG() * 2 * 6;
        var utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        if (årsinntekt > seksG) {
            årsinntekt = seksG;
        }
        return ((int) Math.round((årsinntekt * utbetalingProsentFaktor) / 260));
    }

    private void sjekkInnvilgetBrev(Fødselsnummer fnrMor,
                                    int månedsinntektMor,
                                    boolean harRefusjon,
                                    boolean harFlereArbeidsgivere,
                                    int snittUtbetalingPerMåned,
                                    boolean harFlereUtbetalingsperioder) {
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = svangerskapspengerInnvilgetAssertionsBuilder(fnrMor, saksbehandler.valgtFagsak.saksnummer(),
                harRefusjon, harFlereArbeidsgivere).medEgenndefinertAssertion(
                "Vi har brukt %s kroner i året før skatt i beregningen av svangerskapspengene dine".formatted(
                        formaterKroner(månedsinntektMor * 12)));
        if (!harFlereArbeidsgivere && !harFlereUtbetalingsperioder) {
            brevAssertionsBuilder.medTekstOmDuFårIGjennomsnittXKronerIMånedenFørSkatt(snittUtbetalingPerMåned);
        }
        if (månedsinntektMor * 12 > SEKS_G_2025) {
            brevAssertionsBuilder.medEgenndefinertAssertion(
                    "Svangerskapspengene dine er fastsatt til %s kroner i året, som er seks ganger grunnbeløpet i ".formatted(
                            formaterKroner(SEKS_G_2025))
                            + "folketrygden. Du tjener mer enn dette, men du får ikke svangerskapspenger for den delen av "
                            + "inntekten som overstiger seks ganger grunnbeløpet.");
        }
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.SVANGERSKAPSPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }
}
