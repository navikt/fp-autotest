package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.util.ArrayList;
import java.util.List;

public class ApVilkårsvurderingAktsomhetInfo {

    protected boolean harGrunnerTilReduksjon;
    protected List<SærligeGrunner> sarligGrunner = new ArrayList<>();
    protected String sarligGrunnerBegrunnelse;
    protected boolean tilbakekrevSelvOmBeloepErUnder4Rettsgebyr;

    public ApVilkårsvurderingAktsomhetInfo(){
        this.sarligGrunnerBegrunnelse = "Dette er en særlige grunner vurdering skrevet av Autotest!";
    }

    public void addGeneriskAktsomhet(){
        this.harGrunnerTilReduksjon = false;
        this.tilbakekrevSelvOmBeloepErUnder4Rettsgebyr = true;
        this.sarligGrunner.add(SærligeGrunner.HELT_ELLER_DELVIS_NAVS_FEIL);
        this.sarligGrunner.add(SærligeGrunner.TID_FRA_UTBETALING);
    }

}
