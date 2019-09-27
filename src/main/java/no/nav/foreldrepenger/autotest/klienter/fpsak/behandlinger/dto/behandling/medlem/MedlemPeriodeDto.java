package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MedlemPeriodeDto {
    private LocalDate vurderingsdato;
    private Set<String> aksjonspunkter = Collections.emptySet();
    private Boolean oppholdsrettVurdering;
    private Boolean erEosBorger;
    private Boolean lovligOppholdVurdering;
    private Boolean bosattVurdering;
    private String begrunnelse;

    public MedlemPeriodeDto() {
        // trengs for deserialisering av JSON
    }

    public LocalDate getVurderingsdato() {
        return vurderingsdato;
    }

    void setVurderingsdato(LocalDate vurderingsdato) {
        this.vurderingsdato = vurderingsdato;
    }

    public Set<String> getAksjonspunkter() {
        return aksjonspunkter;
    }

    void setAksjonspunkter(Set<String> aksjonspunkter) {
        this.aksjonspunkter = aksjonspunkter;
    }



    public Boolean getOppholdsrettVurdering() {
        return oppholdsrettVurdering;
    }

    void setOppholdsrettVurdering(Boolean oppholdsrettVurdering) {
        this.oppholdsrettVurdering = oppholdsrettVurdering;
    }

    public Boolean getErEosBorger() {
        return erEosBorger;
    }

    void setErEosBorger(Boolean erEosBorger) {
        this.erEosBorger = erEosBorger;
    }

    public Boolean getLovligOppholdVurdering() {
        return lovligOppholdVurdering;
    }

    public void setLovligOppholdVurdering(Boolean lovligOppholdVurdering) {
        this.lovligOppholdVurdering = lovligOppholdVurdering;
    }

    public Boolean getBosattVurdering() {
        return bosattVurdering;
    }

    public void setBosattVurdering(Boolean bosattVurdering) {
        this.bosattVurdering = bosattVurdering;
    }

}
