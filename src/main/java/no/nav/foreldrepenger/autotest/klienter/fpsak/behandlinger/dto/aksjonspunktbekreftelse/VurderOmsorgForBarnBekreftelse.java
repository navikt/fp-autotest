package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5061")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderOmsorgForBarnBekreftelse extends AksjonspunktBekreftelse {

    private Boolean omsorg;
    private List<Periode> ikkeOmsorgPerioder = new ArrayList<>();

    public VurderOmsorgForBarnBekreftelse() {
        super();
    }

    public Boolean getOmsorg() {
        return omsorg;
    }

    public List<Periode> getIkkeOmsorgPerioder() {
        return ikkeOmsorgPerioder;
    }

    public void bekreftBrukerHarOmsorg() {
        omsorg = true;
    }

    public void bekreftBrukerHarIkkeOmsorg(LocalDate fra, LocalDate til) {
        omsorg = false;
        ikkeOmsorgPerioder.add(new Periode(fra, til));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderOmsorgForBarnBekreftelse that = (VurderOmsorgForBarnBekreftelse) o;
        return Objects.equals(omsorg, that.omsorg) &&
                Objects.equals(ikkeOmsorgPerioder, that.ikkeOmsorgPerioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(omsorg, ikkeOmsorgPerioder);
    }

    @Override
    public String toString() {
        return "VurderOmsorgForBarnBekreftelse{" +
                "omsorg=" + omsorg +
                ", ikkeOmsorgPerioder=" + ikkeOmsorgPerioder +
                "} " + super.toString();
    }

    @JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class Periode {
        private LocalDate periodeFom;
        private LocalDate periodeTom;

        public Periode(LocalDate periodeFom, LocalDate periodeTom) {
            this.periodeFom = periodeFom;
            this.periodeTom = periodeTom;
        }

        public LocalDate getPeriodeFom() {
            return periodeFom;
        }

        public LocalDate getPeriodeTom() {
            return periodeTom;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Periode periode = (Periode) o;
            return Objects.equals(periodeFom, periode.periodeFom) &&
                    Objects.equals(periodeTom, periode.periodeTom);
        }

        @Override
        public int hashCode() {
            return Objects.hash(periodeFom, periodeTom);
        }

        @Override
        public String toString() {
            return "Periode{" +
                    "periodeFom=" + periodeFom +
                    ", periodeTom=" + periodeTom +
                    '}';
        }
    }
}
