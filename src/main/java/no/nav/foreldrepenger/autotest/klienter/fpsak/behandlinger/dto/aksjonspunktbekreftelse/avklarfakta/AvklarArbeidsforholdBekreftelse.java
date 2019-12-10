package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@BekreftelseKode(kode="5080")
public class AvklarArbeidsforholdBekreftelse extends AksjonspunktBekreftelse {

    protected List<Arbeidsforhold> arbeidsforhold = new ArrayList<>();

    public AvklarArbeidsforholdBekreftelse(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);

        arbeidsforhold = behandling.getInntektArbeidYtelse().arbeidsforhold;

        for (Arbeidsforhold arbeidsforholdBehandling : arbeidsforhold) {
            arbeidsforholdBehandling.setBrukArbeidsforholdet(true);
        }
    }

    public void bekreftArbeidsforholdErRelevant(String navn, boolean fortsettUtenInntekt) {
        Arbeidsforhold forhold = finnArbeidsforhold(navn);
        if(forhold == null) {
            throw new RuntimeException("fant ikke arbeidsforhold: " + navn);
        }
        bekreftArbeidsforholdErRelevant(forhold, fortsettUtenInntekt);
    }

    public void bekreftArbeidsforholdErOverstyrt(String navn, LocalDate startDato, LocalDate overstyrtTom) {
        Arbeidsforhold forhold = finnArbeidsforhold(navn, startDato);
        if(forhold == null) {
            throw new RuntimeException("fant ikke arbeidsforhold: " + navn);
        }
        forhold.setFortsettBehandlingUtenInntektsmelding(true);
        forhold.setOverstyrtTom(overstyrtTom);
    }

    public void bekreftArbeidsforholdErBasertPåInntektsmelding(String navn, LocalDate startDato, LocalDate sluttDato, BigDecimal stillingsprosent) {
        Arbeidsforhold arbeidsforhold = finnArbeidsforhold(navn);

        arbeidsforhold.setBrukArbeidsforholdet(true);
        arbeidsforhold.setBasertPaInntektsmelding(true);
        arbeidsforhold.setTomDato(sluttDato);
        arbeidsforhold.setFomDato(startDato);
        arbeidsforhold.setStillingsprosent(stillingsprosent);
    }

    public void bekreftArbeidsforholdErRelevant(Arbeidsforhold forhold, boolean fortsettUtenInntekt) {
        forhold.setBrukArbeidsforholdet(true);
        forhold.setFortsettBehandlingUtenInntektsmelding(fortsettUtenInntekt);
    }

    public void bekreftArbeidsforholdErIkkeRelevant(String navn) {
        Arbeidsforhold forhold = finnArbeidsforhold(navn);
        if(forhold == null) {
            throw new RuntimeException("fant ikke arbeidsforhold: " + navn);
        }
        bekreftArbeidsforholdErIkkeRelevant(forhold);
    }

    public void bekreftArbeidsforholdErIkkeRelevant(Arbeidsforhold forhold) {
        forhold.setBrukArbeidsforholdet(false);
    }

    private Arbeidsforhold finnArbeidsforhold(String navn) {
        for (Arbeidsforhold arbeidsforhold : this.arbeidsforhold) {
            if(arbeidsforhold.getNavn().equals(navn)) {
                return arbeidsforhold;
            }
        }
        return null;
    }

    //Trenger og skille to arbeidsforhold i samme bedrift
    private Arbeidsforhold finnArbeidsforhold(String navn, LocalDate startDato) {
        for (Arbeidsforhold arbeidsforhold : this.arbeidsforhold) {
            if(arbeidsforhold.getNavn().equals(navn) && arbeidsforhold.getFomDato().equals(startDato)) {
                return arbeidsforhold;
            }
        }
        return null;
    }

    public Arbeidsforhold leggTilArbeidsforhold(String navn, LocalDate startDato, LocalDate sluttDato, int stillingsprosent) {
        Arbeidsforhold arbeid = new Arbeidsforhold(navn, startDato, sluttDato, BigDecimal.valueOf(stillingsprosent), true);
        arbeid.setBrukArbeidsforholdet(true);
        arbeidsforhold.add(arbeid);
        return null;
    }

}
