package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public KravgrunnlagDetaljert(Long saksnummer, String ident, String behandlingId, String ytelseType,
            String kravStatusKode) {
        this.vedtakId = saksnummer - 11111;
        this.kravgrunnlagId = 10000L + (long) (Math.random() * (9999999L - 10000L));
        this.kravStatusKode = kravStatusKode;
        if (ytelseType.equals("ES")) {
            this.fagOmrådeKode = "REFUTG";
        } else {
            this.fagOmrådeKode = ytelseType;
        }
        this.fagSystemId = saksnummer.toString() + "100";
        this.vedtakFagSystemDato = LocalDate.now().toString();
        this.omgjortVedtakId = 0;
        this.gjelderVedtakId = ident;
        this.gjelderType = "PERSON";
        this.utbetalesTilId = ident;
        this.utbetGjelderType = "PERSON";
        this.ansvarligEnhet = "8020";
        this.bostedEnhet = "8020";
        this.behandlendeEnhet = "8020";
        this.kontrollFelt = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").format(new Date()); // 2019-10-22-18.59.28.484337
        this.saksBehId = "K231B433";
        this.referanse = behandlingId;

        this.perioder = new ArrayList<>();
    }

    public void setReferanse(String referanse) {
        this.referanse = referanse;
    }

    public void leggTilPeriode(){
        leggTilPeriode(PeriodeType.GENERISK);
    }
    public void leggTilPeriodeMedSmåBeløp() {
        leggTilPeriode(PeriodeType.SMÅ_BELØP);
        this.kontrollFelt = (LocalDateTime.now().minusMonths(3).withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh.mm.ss.SSSSSS")));
    }
    public void leggTilPeriode(PeriodeType periodeType) {
        KravgrunnlagPeriode kravgrunnlagPeriode = new KravgrunnlagPeriode(
                LocalDate.now().minusMonths(6).withDayOfMonth(1).toString(),
                LocalDate.now().minusMonths(6).withDayOfMonth(LocalDate.now().minusMonths(6).lengthOfMonth())
                        .toString(),
                BigDecimal.valueOf(412));
        if (periodeType.equals(PeriodeType.SMÅ_BELØP)){
            kravgrunnlagPeriode.leggTilPosteringMedLiteBeløp();
        }
        else { kravgrunnlagPeriode.leggTilPostering(); }
        this.perioder.add(kravgrunnlagPeriode);
    }

    public void leggTilPeriodeForEngangsstonad() {
        if (!this.fagOmrådeKode.equals("REFUTG")) {
            throw new IllegalStateException(
                    "Periode for Engangsstønad ikke tillatt for fagområde: " + this.fagOmrådeKode);
        }
        KravgrunnlagPeriode kravgrunnlagPeriode = new KravgrunnlagPeriode(
                LocalDate.now().minusMonths(6).with(DayOfWeek.MONDAY).toString(),
                LocalDate.now().minusMonths(6).with(DayOfWeek.MONDAY).toString(),
                BigDecimal.ZERO);
        kravgrunnlagPeriode.leggTilPosteringForEngangsstonad();
        this.perioder.add(kravgrunnlagPeriode);
    }

    public enum PeriodeType {
        GENERISK,
        SMÅ_BELØP
    }
}
