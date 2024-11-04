package no.nav.foreldrepenger.autotest.aktoerer.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.foreldrepengesoknapi.InnsynKlient;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.innsyn.BehandlingTilstand;
import no.nav.foreldrepenger.common.innsyn.EsSak;
import no.nav.foreldrepenger.common.innsyn.FpSak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.common.innsyn.svp.SvpSak;

public class Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(Innsyn.class);
    private static final int API_PROGRESSIV_VENTETID = 3000;
    private static final int API_TIMEOUT_SEKUNDER = 30;

    private final InnsynKlient innsynKlient;
    private final Fødselsnummer fnr;

    public Innsyn(Fødselsnummer fnr) {
        innsynKlient = new InnsynKlient();
        this.fnr = fnr;
    }

    private Saker hentSaker() {
        LOG.info("Henter saker på bruker {}", fnr.value());
        return innsynKlient.hentSaker(fnr);
    }

    public EsSak hentEsSakMedÅpenBehandlingTilstand(Saksnummer saksnummer, BehandlingTilstand behandlingTilstand) {
        return Vent.på(() -> {
            var saker = hentSaker();
            var sak = saker.engangsstønad().stream().filter(s -> s.saksnummer().value().equals(saksnummer.value())).findFirst();
            if (sak.isEmpty()) {
                return null;
            }
            if (sak.get().åpenBehandling() == null || sak.get().åpenBehandling().tilstand() != behandlingTilstand) {
                return null;
            }
            return sak.get();
        }, () -> "Finner ikke sak med tilstand " + behandlingTilstand + " for sak " + saksnummer, API_PROGRESSIV_VENTETID,
                "åpen es behandling med tilstand " + behandlingTilstand, API_TIMEOUT_SEKUNDER);
    }

    public SvpSak hentSvpSakMedÅpenBehandlingTilstand(Saksnummer saksnummer, BehandlingTilstand behandlingTilstand) {
        return Vent.på(() -> {
            var saker = hentSaker();
            var sak = saker.svangerskapspenger().stream().filter(s -> s.saksnummer().value().equals(saksnummer.value())).findFirst();
            if (sak.isEmpty()) {
                return null;
            }
            if (sak.get().åpenBehandling() == null || sak.get().åpenBehandling().tilstand() != behandlingTilstand) {
                return null;
            }
            return sak.get();
        }, () -> "Finner ikke sak med tilstand " + behandlingTilstand + " for sak " + saksnummer, API_PROGRESSIV_VENTETID,
                "åpen svp behandling med tilstand " + behandlingTilstand, API_TIMEOUT_SEKUNDER);
    }

    public EsSak hentEsSakUtenÅpenBehandling(Saksnummer saksnummer) {
        return Vent.på(() -> {
            var saker = hentSaker();
            var sak = saker.engangsstønad().stream().filter(s -> s.saksnummer().value().equals(saksnummer.value())).findFirst();
            if (sak.isEmpty()) {
                return null;
            }
            if (sak.get().åpenBehandling() != null) {
                return null;
            }
            return sak.get();
        }, () -> "Finner ikke sak uten åpen behandling for sak " + saksnummer, API_PROGRESSIV_VENTETID,
                "es sak uten åpen behandling", API_TIMEOUT_SEKUNDER);
    }

    public SvpSak hentSvpSakUtenÅpenBehandling(Saksnummer saksnummer) {
        return Vent.på(() -> {
            var saker = hentSaker();
            var sak = saker.svangerskapspenger().stream().filter(s -> s.saksnummer().value().equals(saksnummer.value())).findFirst();
            if (sak.isEmpty()) {
                return null;
            }
            if (sak.get().åpenBehandling() != null) {
                return null;
            }
            return sak.get();
        }, () -> "Finner ikke sak uten åpen behandling for sak " + saksnummer, API_PROGRESSIV_VENTETID,
                "svp sak uten åpen behandling", API_TIMEOUT_SEKUNDER);
    }

    public FpSak hentFpSakUtenÅpenBehandling(Saksnummer saksnummer) {
        return Vent.på(() -> {
            var saker = hentSaker();
            var sak = saker.foreldrepenger().stream().filter(s -> s.saksnummer().value().equals(saksnummer.value())).findFirst();
            if (sak.isEmpty()) {
                return null;
            }
            if (sak.get().åpenBehandling() != null) {
                return null;
            }
            return sak.get();
        }, () -> "Finner ikke sak uten åpen behandling for sak " + saksnummer, API_PROGRESSIV_VENTETID,
                "fp sak uten åpen behandling", API_TIMEOUT_SEKUNDER);
    }
}
