package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Orgnummer;

@BekreftelseKode(kode = "5080")
public class AvklarArbeidsforholdBekreftelse extends AksjonspunktBekreftelse {

    protected List<Arbeidsforhold> arbeidsforhold = new ArrayList<>();

    public AvklarArbeidsforholdBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        arbeidsforhold = behandling.getInntektArbeidYtelse().arbeidsforhold;

        for (Arbeidsforhold arbeidsforholdBehandling : arbeidsforhold) {
            arbeidsforholdBehandling.setBrukArbeidsforholdet(true);
        }
    }

    public AvklarArbeidsforholdBekreftelse bekreftArbeidsforholdErAktivt(Orgnummer orgnummer, boolean fortsettUtenInntekt) {
        Arbeidsforhold forhold = finnArbeidsforhold(orgnummer);
        if (forhold == null) {
            throw new RuntimeException("fant ikke arbeidsforhold: " + orgnummer);
        }
        bekreftArbeidsforholdErAktivt(forhold, fortsettUtenInntekt);
        return this;
    }

    public AvklarArbeidsforholdBekreftelse bekreftArbeidsforholdErIkkeAktivt(Orgnummer orgnummer, LocalDate startDato,
            LocalDate overstyrtTom, String begrunnelse) {
        Arbeidsforhold forhold = finnArbeidsforhold(orgnummer, startDato);
        if (forhold == null) {
            throw new RuntimeException("fant ikke arbeidsforhold: " + orgnummer);
        }
        forhold.setFortsettBehandlingUtenInntektsmelding(true);
        forhold.setOverstyrtTom(overstyrtTom);
        forhold.setBegrunnelse(begrunnelse);
        return this;
    }

    public AvklarArbeidsforholdBekreftelse bekreftArbeidsforholdErBasertPåInntektsmelding(Orgnummer orgnummer,
            LocalDate startDato, LocalDate sluttDato, BigDecimal stillingsprosent) {
        Arbeidsforhold arbeidsforhold = finnArbeidsforhold(orgnummer);

        arbeidsforhold.setBrukArbeidsforholdet(true);
        arbeidsforhold.setBasertPaInntektsmelding(true);
        arbeidsforhold.setTomDato(sluttDato);
        arbeidsforhold.setFomDato(startDato);
        arbeidsforhold.setStillingsprosent(stillingsprosent);
        return this;
    }

    public void bekreftArbeidsforholdErAktivt(Arbeidsforhold forhold, boolean fortsettUtenInntekt) {
        forhold.setBrukArbeidsforholdet(true);
        forhold.setFortsettBehandlingUtenInntektsmelding(fortsettUtenInntekt);
        forhold.setBegrunnelse("Begrunnelse fra Autotest.");
    }

    private Arbeidsforhold finnArbeidsforhold(Orgnummer orgnummer) {
        return this.arbeidsforhold.stream()
            .filter(a -> orgnummer.equals(a.getArbeidsgiverReferanse()))
            .findFirst().orElse(null);
    }

    private Arbeidsforhold finnArbeidsforholdForNavn(String navn) {
        for (Arbeidsforhold arbeidsforhold : this.arbeidsforhold) {
            if (arbeidsforhold.getNavn().equals(navn)) {
                return arbeidsforhold;
            }
        }
        return null;
    }

    // Trenger og skille to arbeidsforhold i samme bedrift
    private Arbeidsforhold finnArbeidsforhold(Orgnummer orgnummer, LocalDate startDato) {
        return this.arbeidsforhold.stream()
            .filter(a -> orgnummer.equals(a.getArbeidsgiverReferanse()))
            .filter(a -> startDato.equals(a.getFomDato()))
            .findFirst().orElse(null);
    }

    public AvklarArbeidsforholdBekreftelse leggTilArbeidsforhold(String navn, LocalDate startDato, LocalDate sluttDato,
            int stillingsprosent) {
        Arbeidsforhold arbeid = new Arbeidsforhold(navn, startDato, sluttDato, BigDecimal.valueOf(stillingsprosent),
                true);
        arbeid.setBrukArbeidsforholdet(true);
        arbeidsforhold.add(arbeid);
        return this;
    }

}
