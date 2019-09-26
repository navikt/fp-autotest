package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BekreftedePerioderDto {
    protected LocalDate vurderingsdato;
    protected List<String> aksjonspunkter = new ArrayList<>();
    protected Boolean bosattVurdering;
    protected Boolean erEosBorger;
    protected Boolean oppholdsrettVurdering;
    protected Boolean lovligOppholdVurdering;
    protected LocalDate fodselsdato;
    protected Kode medlemskapManuellVurderingType;
    protected String omsorgsovertakelseDato;
    protected String begrunnelse;

    public void setMedlemskapManuellVurderingType(Kode medlemskapManuellVurderingType) {
        this.medlemskapManuellVurderingType = medlemskapManuellVurderingType;
    }

    public void setBosattVurdering(Boolean bosattVurdering) {
        this.bosattVurdering = bosattVurdering;
    }
}
