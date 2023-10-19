package no.nav.foreldrepenger.autotest.fpsak.svangerskapspenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.TilretteleggingBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.ArbeidsforholdMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("svangerskapspenger")
class Førstegangsbehandling extends FpsakTestBase {

    // Sjekk VerdikjedeSvangeskapsenger.java om det finnes en eksisterende test før du lager en ny her.

    @Test
    @DisplayName("Mor søker SVP med to arbeidsforhold - hel tilrettelegging")
    @Description("Mor søker SVP med to arbeidsforhold, fire uke før termin, hel tilrettelegging")
    void morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(2))
                                .arbeidsforhold(LocalDate.now().minusYears(4))
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(4);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var arbeidsforhold2 = arbeidsforholdene.get(1).arbeidsgiverIdentifikasjon();
        var forsteTilrettelegging = TilretteleggingBuilder.hel(
                LocalDate.now().minusWeeks(1),
                LocalDate.now().plusWeeks(2),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforhold1))
                .build();
        var andreTilrettelegging2 = TilretteleggingBuilder.hel(
                LocalDate.now(),
                LocalDate.now().plusWeeks(3),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforhold2))
                .build();
        var søknad = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato, List.of(forsteTilrettelegging, andreTilrettelegging2));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(tilkjentYtelsePerioder)
                .as("Antall tilkjent ytelses peridoer")
                .hasSize(2);
        assertThat(tilkjentYtelsePerioder.get(0).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(LocalDate.now().minusWeeks(1));
        assertThat(tilkjentYtelsePerioder.get(0).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(LocalDate.now().minusDays(1));
        assertThat(tilkjentYtelsePerioder.get(1).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(LocalDate.now());
        assertThat(tilkjentYtelsePerioder.get(1).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(termindato.minusWeeks(3).minusDays(1));

        assertThat(tilkjentYtelsePerioder.get(0).getAndeler())
                .as("Antall andeler i tilkjent ytelsesperiode 1")
                .hasSize(1);
        assertThat(tilkjentYtelsePerioder.get(1).getAndeler())
                .as("Antall andeler i tilkjent ytelsesperiode 2")
                .hasSize(2);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele den utbetalte dagsatsen går til søker")
                .isTrue();
    }

    @Test
    @DisplayName("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    @Description("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    void mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(2))
                                .arbeidsforhold(LocalDate.now().minusYears(4))
                                .arbeidsforhold(LocalDate.now().minusYears(4))
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var termindato = LocalDate.now().plusMonths(3);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforholdIdentifikator1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var arbeidsforholdIdentifikator2 = arbeidsforholdene.get(1).arbeidsgiverIdentifikasjon();
        var arbeidsforholdIdentifikator3 = arbeidsforholdene.get(2).arbeidsgiverIdentifikasjon();
        final var helTilrettelegging = TilretteleggingBuilder.hel(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforholdIdentifikator1))
                .build();
        final var delvisTilrettelegging = TilretteleggingBuilder.delvis(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforholdIdentifikator2),
                40.0)
                .build();
        final var ingenTilrettelegging = TilretteleggingBuilder.ingen(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforholdIdentifikator3))
                .build();
        var søknad = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato,
                List.of(helTilrettelegging, delvisTilrettelegging, ingenTilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        var arbeidsgiver1 = arbeidsgivere.toList().get(0);
        var inntektsmelding1 = arbeidsgiver1.lagInntektsmeldingSVP()
                .medBeregnetInntekt(20_833);
        arbeidsgiver1.sendInntektsmeldinger(saksnummer, inntektsmelding1);

        var arbeidsgiver2 = arbeidsgivere.toList().get(1);
        var inntektsmelding2 = arbeidsgiver2.lagInntektsmeldingSVP()
                .medBeregnetInntekt(62_500)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(27_778));
        arbeidsgiver2.sendInntektsmeldinger(saksnummer, inntektsmelding2);

        var arbeidsgiver3 = arbeidsgivere.toList().get(2);
        var inntektsmelding3 = arbeidsgiver3.lagInntektsmeldingSVP()
                .medBeregnetInntekt(50_000)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(46_667));
        arbeidsgiver3.sendInntektsmeldinger(saksnummer, inntektsmelding3);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Verifisering av Beregning
        var bgPerioder = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPerioder()
                .stream()
                .sorted(Comparator.comparing(BeregningsgrunnlagPeriodeDto::getBeregningsgrunnlagPeriodeFom))
                .collect(Collectors.toList());
        assertThat(bgPerioder).hasSize(3);

        var førstePeriode = bgPerioder.get(0);
        assertThat(førstePeriode.getDagsats()).isZero();

        var andrePeriode = bgPerioder.get(1);
        assertThat(andrePeriode.getDagsats()).isPositive();

        var tredjePeriode = bgPerioder.get(2);
        assertThat(tredjePeriode.getDagsats()).isZero();

        // Verifisering av Tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(tilkjentYtelsePerioder)
                .as("Antall tilkjent ytelses peridoer")
                .hasSize(1);
        assertThat(tilkjentYtelsePerioder.get(0).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(termindato.minusMonths(2));
        assertThat(tilkjentYtelsePerioder.get(0).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(termindato.minusWeeks(3).minusDays(1));
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100))
                .as("Forventer at hele den utbetalte dagsatsen går til arbeidsgiver")
                .isTrue();
    }

    @Test
    @DisplayName("Mor søker SVP med ett arbeidsforhold - halv og så endring til ingen tilrettelegging. Full refusjon")
    @Description("Mor søker SVP med ett arbeidsforhold - halv og så endring til ingen tilrettelegging. Full refusjon")
    void mor_søker_svp_ett_arbeidsforhold_endrer_ingen_tilrettelegging() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(6);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var forsteTilrettelegging = TilretteleggingBuilder.delvis(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforhold1),
                50.0)
                .build();
        var søknad = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato, List.of(forsteTilrettelegging));
        var saksnummerSVP = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerSVP);

        saksbehandler.hentFagsak(saksnummerSVP);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var andreTilrettelegging = TilretteleggingBuilder.ingen(
                termindato.minusMonths(3),
                termindato.minusMonths(1),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforhold1))
                .build();
        var søknad2 = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato, List.of(andreTilrettelegging));
        mor.søk(søknad2.build(), saksnummerSVP);

        saksbehandler.hentFagsak(saksnummerSVP);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Verifisering av Beregning
        var bgPerioder = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPerioder()
                .stream()
                .sorted(Comparator.comparing(BeregningsgrunnlagPeriodeDto::getBeregningsgrunnlagPeriodeFom))
                .collect(Collectors.toList());
        assertThat(bgPerioder).hasSize(3);

        // Verifisering av Tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(tilkjentYtelsePerioder)
                .as("Antall tilkjent ytelses peridoer")
                .hasSize(2);
        assertThat(tilkjentYtelsePerioder.get(0).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(termindato.minusMonths(3));
        assertThat(tilkjentYtelsePerioder.get(0).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(termindato.minusMonths(1).minusDays(1));
        assertThat(tilkjentYtelsePerioder.get(1).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(termindato.minusMonths(1));
        assertThat(tilkjentYtelsePerioder.get(1).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(termindato.minusWeeks(3).minusDays(1));

        // Valider at ny og identisk inntektsmelding revuderes automatisk
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerSVP);

        saksbehandler.hentFagsak(saksnummerSVP);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }

    @Test
    @DisplayName("Mor søker SVP og FP - revurder SVP")
    @Description("Mor søker SVP og FP - revurder SVP, SVP seks uker før termin, FP tre uker før tidligere termin")
    void revurder_svp_pga_innvilget_fp() {
        // Innvilg SVP fra nå til Termin-3uker - tom fredag
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(6);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var forsteTilrettelegging = TilretteleggingBuilder.hel(
                LocalDate.now().minusWeeks(1),
                termindato.minusWeeks(3).minusDays(3),
                ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforhold1))
                .build();
        var søknad = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato, List.of(forsteTilrettelegging));
        var saksnummerSVP = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerSVP);

        saksbehandler.hentFagsak(saksnummerSVP);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();


        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // Innvilg FP fom Termin-4uker
        var søknadFP = lagSøknadForeldrepengerTermin(termindato.minusWeeks(1), BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummerFP = mor.søk(søknadFP.build());
        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFP, termindato.minusWeeks(4));
        saksbehandler.hentFagsak(saksnummerFP);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Revurder SVP - siste periode skal bli avslått i uttak og tilkjent dagsats = 0
        overstyrer.hentFagsak(saksnummerSVP);
        overstyrer.ventPåOgVelgRevurderingBehandling();
        overstyrer.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.hentFagsak(saksnummerSVP);

        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker())
                .map(BehandlingÅrsak::behandlingArsakType)
                .containsExactly(BehandlingÅrsakType.OPPHØR_YTELSE_NYTT_BARN);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).isEqualTo(BehandlingResultatType.OPPHØR);
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(tilkjentYtelsePerioder)
                .as("Antall tilkjent ytelses peridoer")
                .hasSizeGreaterThan(1);
        // Litt datogamble
        assertThat(tilkjentYtelsePerioder.get(1).getFom())
                .as("Avslått tilkjent ytelse Fom")
                .isEqualTo(VirkedagUtil.helgejustertTilMandag(termindato.minusWeeks(4)));
        assertThat(tilkjentYtelsePerioder.get(1).getDagsats())
                .as("Avslått tilkjent ytelse med dagsats 0")
                .isZero();
    }
}
