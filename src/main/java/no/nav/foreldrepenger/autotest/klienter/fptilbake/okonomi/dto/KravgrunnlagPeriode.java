package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class KravgrunnlagPeriode {

    protected String fom;
    protected String tom;
    protected BigDecimal beløpSkattMnd;

    protected List<KravgrunnlagPeriodePostering> posteringer;

    public KravgrunnlagPeriode(String fom, String tom, BigDecimal beløpSkattMnd) {

        this.fom = fom;
        this.tom = tom;
        this.beløpSkattMnd = beløpSkattMnd;

        this.posteringer = new ArrayList<>();
    }

    public void leggTilPostering() {
        KravgrunnlagPeriodePostering kravgrunnlagPeriodePostering = new KravgrunnlagPeriodePostering(
                "FPATORD", "YTEL",
                BigDecimal.valueOf(3253),
                BigDecimal.valueOf(1637),
                BigDecimal.valueOf(1616),
                BigDecimal.ZERO,
                BigDecimal.valueOf(25.5221));
        this.posteringer.add(kravgrunnlagPeriodePostering);
        kravgrunnlagPeriodePostering = new KravgrunnlagPeriodePostering(
                "KL_KODE_FEIL_KORTTID", "FEIL",
                BigDecimal.ZERO,
                BigDecimal.valueOf(1616),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(25.5221));
        this.posteringer.add(kravgrunnlagPeriodePostering);
    }

    public void leggTilPosteringForEngangsstonad() {
        KravgrunnlagPeriodePostering kravgrunnlagPeriodePostering = new KravgrunnlagPeriodePostering(
                "FPENFOD-OP", "YTEL",
                BigDecimal.valueOf(83140),
                BigDecimal.ZERO,
                BigDecimal.valueOf(83140),
                BigDecimal.ZERO,
                BigDecimal.ZERO);
        this.posteringer.add(kravgrunnlagPeriodePostering);
        kravgrunnlagPeriodePostering = new KravgrunnlagPeriodePostering(
                "KL_KODE_FEIL_REFUTG", "FEIL",
                BigDecimal.ZERO,
                BigDecimal.valueOf(83140),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
        this.posteringer.add(kravgrunnlagPeriodePostering);
    }

    public void leggTilPosteringMedLiteBeløp(){
        KravgrunnlagPeriodePostering kravgrunnlagPeriodePostering = new KravgrunnlagPeriodePostering(
                "FPATORD", "YTEL",
                BigDecimal.valueOf(1053),
                BigDecimal.valueOf(537),
                BigDecimal.valueOf(516),
                BigDecimal.ZERO,
                BigDecimal.valueOf(25.5221));
        this.posteringer.add(kravgrunnlagPeriodePostering);
        kravgrunnlagPeriodePostering = new KravgrunnlagPeriodePostering(
                "KL_KODE_FEIL_KORTTID", "FEIL",
                BigDecimal.ZERO,
                BigDecimal.valueOf(516),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(25.5221));
        this.posteringer.add(kravgrunnlagPeriodePostering);
    }
}
