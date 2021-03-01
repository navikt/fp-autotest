package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.MedlemskapManuellVurderingType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BekreftedePerioderDto {
    protected LocalDate vurderingsdato;
    protected List<String> aksjonspunkter = new ArrayList<>();
    protected Boolean bosattVurdering;
    protected Boolean erEosBorger;
    protected Boolean oppholdsrettVurdering;
    protected Boolean lovligOppholdVurdering;
    protected LocalDate fodselsdato;
    protected MedlemskapManuellVurderingType medlemskapManuellVurderingType;
    protected String omsorgsovertakelseDato;
    protected String begrunnelse;

    public void setMedlemskapManuellVurderingType(MedlemskapManuellVurderingType medlemskapManuellVurderingType) {
        this.medlemskapManuellVurderingType = medlemskapManuellVurderingType;
    }

    public void setBosattVurdering(Boolean bosattVurdering) {
        this.bosattVurdering = bosattVurdering;
    }

    public void setErEosBorger(Boolean erEosBorger) {
        this.erEosBorger = erEosBorger;
    }

    public void setLovligOppholdVurdering(Boolean lovligOppholdVurdering) {
        this.lovligOppholdVurdering = lovligOppholdVurdering;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public void setVurderingsdato(LocalDate vurderingsdato) {
        this.vurderingsdato = vurderingsdato;
    }

    public void setAksjonspunkter(List<String> aksjonspunkter) {
        this.aksjonspunkter = aksjonspunkter;
    }
}
