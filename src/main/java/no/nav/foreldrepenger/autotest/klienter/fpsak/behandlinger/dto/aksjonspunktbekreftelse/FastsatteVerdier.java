package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;

public class FastsatteVerdier {

    protected Integer refusjon;
    protected Integer fastsattBeløp;
    protected Inntektskategori inntektskategori;

    public FastsatteVerdier(Integer refusjon, Integer fastsattBeløp, Inntektskategori inntektskategori) {
        this.refusjon = refusjon;
        this.fastsattBeløp = fastsattBeløp;
        this.inntektskategori = inntektskategori;
    }

    public Integer getRefusjon() {
        return refusjon;
    }

    public void setRefusjon(Integer refusjon) {
        this.refusjon = refusjon;
    }

    public Integer getFastsattBeløp() {
        return fastsattBeløp;
    }

    public void setFastsattBeløp(Integer fastsattBeløp) {
        this.fastsattBeløp = fastsattBeløp;
    }

    public Inntektskategori getInntektskategori() {
        return inntektskategori;
    }

    public void setInntektskategori(Inntektskategori inntektskategori) {
        this.inntektskategori = inntektskategori;
    }
}
