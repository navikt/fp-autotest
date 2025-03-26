package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

// TODO: Fiks opp i testbasene
public abstract class VerdikjedeTestBase extends FpsakTestBase {

    protected static final Integer G_2025 = 124028;
    protected static final Integer SEKS_G_2025 = G_2025 * 6;

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandling(Saksnummer saksnummer,
                                                                   boolean revurdering,
                                                                   boolean tilbakekreving) {
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, revurdering, tilbakekreving, true);
    }

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandling(Saksnummer saksnummer, boolean revurdering,
                                                                   boolean tilbakekreving, boolean ventPåSendtBrev) {
        if (!revurdering) {
            saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        }
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        if (beslutter.harRevurderingBehandling() && revurdering) {
            beslutter.ventPåOgVelgRevurderingBehandling();
        }
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        if (tilbakekreving) {
            beslutter.bekreftAksjonspunkt(bekreftelse);
            beslutter.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        }
        if (ventPåSendtBrev) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        }
    }

    protected void ventPåInntektsmeldingForespørsel(Saksnummer saksnummer) {
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.MIN_SIDE_ARBEIDSGIVER);
    }

    protected void validerInnsendtInntektsmeldingForeldrepenger(Fødselsnummer fødselsnummer,
                                                                LocalDate førsteDagMedYtelsen,
                                                                Integer månedsInntekt,
                                                                boolean harRefusjon) {
        validerInnsendtInntektsmelding(fødselsnummer, førsteDagMedYtelsen, månedsInntekt, false, TypeYtelse.FP, 0);
    }

    protected void validerInnsendtInntektsmeldingSvangerskapspenger(Fødselsnummer fødselsnummer,
                                                  LocalDate førsteDagMedYtelsen,
                                                  Integer månedsInntekt,
                                                  boolean harRefusjon) {
        validerInnsendtInntektsmelding(fødselsnummer, førsteDagMedYtelsen, månedsInntekt, harRefusjon, TypeYtelse.SVP, 0);
    }

    protected void validerInnsendtInntektsmelding(Fødselsnummer fødselsnummer,
                                                  LocalDate førsteDagMedYtelsen,
                                                  Integer månedsInntekt,
                                                  boolean harRefusjon,
                                                  TypeYtelse typeYtelse,
                                                  int historikkInnslagIndeks) {

        Objects.requireNonNull(typeYtelse, "ytelseType");
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.VEDLEGG_MOTTATT);
        var brevAssertionsBuilder = BrevAssertionBuilder.ny();
        var månedslønnFormatert = formaterKroner(månedsInntekt);
        var refusjon = "Nei";
        if (harRefusjon) {
            refusjon = "Ja";
            brevAssertionsBuilder
                    .medEgenndefinertAssertion("Refusjonsbeløp dere krever per måned%skr".formatted(månedslønnFormatert));
        }
        var førsteDagAvkortet = førsteDagMedYtelsen;
        if (TypeYtelse.FP.equals(typeYtelse)) {
            førsteDagAvkortet = førsteArbeidsdagEtter(førsteDagMedYtelsen);
        }

        brevAssertionsBuilder
                .medEgenndefinertAssertion("Innsendt: %s".formatted(formaterDato(LocalDate.now())))
                .medEgenndefinertAssertion("Inntektsmelding %s".formatted(ytelseNavn(typeYtelse)))
                .medEgenndefinertAssertion("Arbeidsgiver")
                .medEgenndefinertAssertion("Den ansatte")
                .medEgenndefinertAssertion("f.nr. %s".formatted(formaterFnr(fødselsnummer)))
                .medEgenndefinertAssertion("Kontaktperson fra bedriften")
                .medEgenndefinertAssertion("Corpolarsen")
                .medEgenndefinertAssertion("Første dag med %s".formatted(ytelseNavn(typeYtelse)))
                .medEgenndefinertAssertion(formaterDato(førsteDagAvkortet))
                .medEgenndefinertAssertion("Beregnet månedslønn")
                .medEgenndefinertAssertion("%s kr".formatted(månedslønnFormatert))
                .medEgenndefinertAssertion("Utbetaling og refusjon")
                .medEgenndefinertAssertion("Betaler dere den ansatte lønn under fraværet og krever refusjon? %s".formatted(refusjon))
                .medEgenndefinertAssertion("Naturalytelser")
                .medEgenndefinertAssertion("Har den ansatte naturalytelser som faller bort ved fraværet?Nei");

        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, null, DokumentTag.INNTEKSTMELDING, HistorikkType.VEDLEGG_MOTTATT, historikkInnslagIndeks);
    }

    protected static LocalDate førsteArbeidsdagEtter(LocalDate dato) {
        if (DayOfWeek.SATURDAY.equals(dato.getDayOfWeek())) {
            return dato.plusDays(2);
        } else if (DayOfWeek.SUNDAY.equals(dato.getDayOfWeek())) {
            return dato.plusDays(1);
        } else {
            return dato;
        }
    }

    private static String ytelseNavn(TypeYtelse typeYtelse) {
        return switch (typeYtelse) {
            case FP -> "foreldrepenger";
            case SVP -> "svangerskapspenger";
        };
    }

    public enum TypeYtelse {
        FP,
        SVP
    }

}
