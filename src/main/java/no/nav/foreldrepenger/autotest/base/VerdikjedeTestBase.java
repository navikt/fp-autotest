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

// TODO: Fiks opp i testbasene
public abstract class VerdikjedeTestBase extends FpsakTestBase {

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

    protected void validerInnsendtInntektsmelding(Fødselsnummer fødselsnummer, LocalDate førsteDagMedYtelsen, Integer månedsInntekt) {
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.VEDLEGG_MOTTATT);
        var brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medEgenndefinertAssertion("Innsendt: %s".formatted(formaterDato(LocalDate.now())))
                .medEgenndefinertAssertion("Inntektsmelding foreldrepenger")
                .medEgenndefinertAssertion("Arbeidsgiver")
                .medEgenndefinertAssertion("Den ansatte")
                .medEgenndefinertAssertion("f.nr. %s".formatted(formaterFnr(fødselsnummer)))
                .medEgenndefinertAssertion("Kontaktperson fra bedriften")
                .medEgenndefinertAssertion("Corpolarsen")
                .medEgenndefinertAssertion("Første dag med foreldrepenger")
                .medEgenndefinertAssertion(formaterDato(førsteArbeidsdagEtter(førsteDagMedYtelsen)))
                .medEgenndefinertAssertion("Beregnet månedslønn")
                .medEgenndefinertAssertion("%s kr".formatted(formaterKroner(månedsInntekt)))
                .medEgenndefinertAssertion("Utbetaling og refusjon")
                .medEgenndefinertAssertion("Betaler dere den ansatte lønn under fraværet og krever refusjon? Nei")
                .medEgenndefinertAssertion("Naturalytelser")
                .medEgenndefinertAssertion("Har den ansatte naturalytelser som faller bort ved fraværet?Nei");

        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, null, DokumentTag.INNTEKSTMELDING, HistorikkType.VEDLEGG_MOTTATT);
    }

    protected LocalDate førsteArbeidsdagEtter(LocalDate dato) {
        if (DayOfWeek.SATURDAY.equals(dato.getDayOfWeek())) {
            return dato.plusDays(2);
        } else if (DayOfWeek.SUNDAY.equals(dato.getDayOfWeek())) {
            return dato.plusDays(1);
        } else {
            return dato;
        }
    }

}
