package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Kravgrunnlag {

    protected Long vedtakId;
    protected Long kravgrunnlagId;
    protected String kravStatusKode;
    protected String fagOmrådeKode;
    protected String fagSystemId;
    protected String vedtakFagSystemDato;
    protected int omgjortVedtakId;
    protected String gjelderVedtakId;
    protected String gjelderType;
    protected String utbetalesTilId;
    protected String utbetGjelderType;
    protected String ansvarligEnhet;
    protected String bostedEnhet;
    protected String behandlendeEnhet;
    protected String kontrollFelt;
    protected String saksBehId;
    protected int referanse;

    protected List<KravgrunnlagPeriode> perioder;

    public Kravgrunnlag(Long saksnummer, String ident, int behandlingId, String ytelseType, String kravStatusKode){
        this.vedtakId = saksnummer-11111;
        this.kravgrunnlagId = saksnummer-11112;
        this.kravStatusKode = kravStatusKode;
        this.fagOmrådeKode = ytelseType;
        this.fagSystemId = saksnummer.toString()+"100";
        this.vedtakFagSystemDato = LocalDate.now().toString();
        this.omgjortVedtakId = 0;
        this.gjelderVedtakId = ident;
        this.gjelderType = "PERSON";
        this.utbetalesTilId = ident;
        this.utbetGjelderType = "PERSON";
        this.ansvarligEnhet = "8020";
        this.bostedEnhet = "8020";
        this.behandlendeEnhet = "8020";
        this.kontrollFelt = LocalDateTime.now().toString();
        this.saksBehId = "K231B433";
        this.referanse = behandlingId;

        this.perioder = new ArrayList<>();
    }

    public void setReferanse(int referanse) {
        this.referanse = referanse;
    }

    public void leggTilPeriode() {
        KravgrunnlagPeriode kravgrunnlagPeriode = new KravgrunnlagPeriode(
                LocalDate.now().withDayOfMonth(1).minusMonths(6).toString(),
                LocalDate.now().minusMonths(6).withDayOfMonth(LocalDate.now().minusMonths(6).lengthOfMonth()).toString(),
                0);
        kravgrunnlagPeriode.leggTilPostering();
        this.perioder.add(kravgrunnlagPeriode);
    }
}
