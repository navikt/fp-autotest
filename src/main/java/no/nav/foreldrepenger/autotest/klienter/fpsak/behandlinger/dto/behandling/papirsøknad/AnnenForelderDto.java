package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AnnenForelderDto {

    private boolean kanIkkeOppgiAnnenForelder = true;
    private KanIkkeOppgiBegrunnelse kanIkkeOppgiBegrunnelse = new KanIkkeOppgiBegrunnelse();
    private boolean sokerHarAleneomsorg = false;
    private boolean denAndreForelderenHarRettPaForeldrepenger = true;

    public AnnenForelderDto() {
        super();
    }

    @JsonCreator
    public AnnenForelderDto(boolean kanIkkeOppgiAnnenForelder, KanIkkeOppgiBegrunnelse kanIkkeOppgiBegrunnelse,
                            boolean sokerHarAleneomsorg, boolean denAndreForelderenHarRettPaForeldrepenger) {
        this.kanIkkeOppgiAnnenForelder = kanIkkeOppgiAnnenForelder;
        this.kanIkkeOppgiBegrunnelse = kanIkkeOppgiBegrunnelse;
        this.sokerHarAleneomsorg = sokerHarAleneomsorg;
        this.denAndreForelderenHarRettPaForeldrepenger = denAndreForelderenHarRettPaForeldrepenger;
    }

    public boolean isKanIkkeOppgiAnnenForelder() {
        return kanIkkeOppgiAnnenForelder;
    }

    public KanIkkeOppgiBegrunnelse getKanIkkeOppgiBegrunnelse() {
        return kanIkkeOppgiBegrunnelse;
    }

    public boolean isSokerHarAleneomsorg() {
        return sokerHarAleneomsorg;
    }

    public boolean isDenAndreForelderenHarRettPaForeldrepenger() {
        return denAndreForelderenHarRettPaForeldrepenger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnenForelderDto that = (AnnenForelderDto) o;
        return kanIkkeOppgiAnnenForelder == that.kanIkkeOppgiAnnenForelder &&
                sokerHarAleneomsorg == that.sokerHarAleneomsorg &&
                denAndreForelderenHarRettPaForeldrepenger == that.denAndreForelderenHarRettPaForeldrepenger &&
                Objects.equals(kanIkkeOppgiBegrunnelse, that.kanIkkeOppgiBegrunnelse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kanIkkeOppgiAnnenForelder, kanIkkeOppgiBegrunnelse, sokerHarAleneomsorg, denAndreForelderenHarRettPaForeldrepenger);
    }

    @Override
    public String toString() {
        return "AnnenForelderDto{" +
                "kanIkkeOppgiAnnenForelder=" + kanIkkeOppgiAnnenForelder +
                ", kanIkkeOppgiBegrunnelse=" + kanIkkeOppgiBegrunnelse +
                ", sokerHarAleneomsorg=" + sokerHarAleneomsorg +
                ", denAndreForelderenHarRettPaForeldrepenger=" + denAndreForelderenHarRettPaForeldrepenger +
                '}';
    }

    @JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class KanIkkeOppgiBegrunnelse {

        private String arsak = "UKJENT_FORELDER";

        public KanIkkeOppgiBegrunnelse() {
        }

        public KanIkkeOppgiBegrunnelse(@JsonProperty("arsak") String arsak) {
            this.arsak = arsak;
        }

        public String getArsak() {
            return arsak;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KanIkkeOppgiBegrunnelse that = (KanIkkeOppgiBegrunnelse) o;
            return Objects.equals(arsak, that.arsak);
        }

        @Override
        public int hashCode() {
            return Objects.hash(arsak);
        }

        @Override
        public String toString() {
            return "KanIkkeOppgiBegrunnelse{" +
                    "arsak='" + arsak + '\'' +
                    '}';
        }
    }
}
