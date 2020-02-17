package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KravgrunnlagDetaljert {

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
    protected String referanse;

    protected List<KravgrunnlagPeriode> perioder;

    public KravgrunnlagDetaljert(Long saksnummer, String ident, String behandlingId, String ytelseType, String kravStatusKode){
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
        this.kontrollFelt = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").format(new Date()); //2019-10-22-18.59.28.484337
        this.saksBehId = "K231B433";
        this.referanse = behandlingId;

        this.perioder = new ArrayList<>();
    }

    public void setReferanse(String referanse) {
        this.referanse = referanse;
    }

    public void leggTilPeriode() {
        KravgrunnlagPeriode kravgrunnlagPeriode = new KravgrunnlagPeriode(
                LocalDate.now().withDayOfMonth(1).minusMonths(6).toString(),
                LocalDate.now().minusMonths(6).withDayOfMonth(LocalDate.now().minusMonths(6).lengthOfMonth()).toString(),
                BigDecimal.valueOf(412));
        kravgrunnlagPeriode.leggTilPostering();
        this.perioder.add(kravgrunnlagPeriode);
    }
}
