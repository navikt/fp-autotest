package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import java.util.ArrayList;
import java.util.List;

public class KravgrunnlagPeriode {

    protected String fom;
    protected String tom;
    protected int beløpSkattMnd;

    protected List<KravgrunnlagPeriodePostering> posteringer;

    public KravgrunnlagPeriode(String fom, String tom, int beløpSkattMnd){

        this.fom = fom;
        this.tom = tom;
        this.beløpSkattMnd = beløpSkattMnd;

        this.posteringer = new ArrayList<>();
    }

    public void leggTilPostering(){
        this.posteringer.add(new KravgrunnlagPeriodePostering());
    }
}
