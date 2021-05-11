package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;
import java.util.List;

public class OmsorgDto {
    Integer antallBarn;
    private List<LocalDate> foedselsDato;
    private LocalDate omsorgsovertakelsesdato;
    private LocalDate ankomstdato;
    private boolean erEktefellesBarn = false;

    public OmsorgDto(Integer antallBarn, List<LocalDate> foedselsDato) {
        this.antallBarn = antallBarn;
        this.foedselsDato = foedselsDato;
    }

    public Integer getAntallBarn() {
        return antallBarn;
    }

    public void setAntallBarn(Integer antallBarn) {
        this.antallBarn = antallBarn;
    }

    public List<LocalDate> getFoedselsDato() {
        return foedselsDato;
    }

    public void setFoedselsDato(List<LocalDate> foedselsDato) {
        this.foedselsDato = foedselsDato;
    }

    public LocalDate getOmsorgsovertakelsesdato() {
        return omsorgsovertakelsesdato;
    }

    public void setOmsorgsovertakelsesdato(LocalDate omsorgsovertakelsesdato) {
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
    }

    public LocalDate getAnkomstdato() {
        return ankomstdato;
    }

    public void setAnkomstdato(LocalDate ankomstdato) {
        this.ankomstdato = ankomstdato;
    }

    public boolean isErEktefellesBarn() {
        return erEktefellesBarn;
    }

    public void setErEktefellesBarn(boolean erEktefellesBarn) {
        this.erEktefellesBarn = erEktefellesBarn;
    }
}
