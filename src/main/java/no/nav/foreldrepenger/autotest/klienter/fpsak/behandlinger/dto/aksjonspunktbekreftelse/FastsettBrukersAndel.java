package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettBrukersAndel {
        private int andelsnr;
        private double fastsattBeløp;
        private String inntektskategori;
        private boolean lagtTilAvSaksbehandler;
        private boolean nyAndel;

        public FastsettBrukersAndel() {
            // TODO Auto-generated constructor stub
        }

        public FastsettBrukersAndel(double fastsattBeløp, String inntektskategori) {
            super();
            this.fastsattBeløp = fastsattBeløp;
            this.inntektskategori = inntektskategori;
        }

        @JsonCreator
        public FastsettBrukersAndel(int andelsnr, double fastsattBeløp, String inntektskategori,
                                    boolean lagtTilAvSaksbehandler, boolean nyAndel) {
            this.andelsnr = andelsnr;
            this.fastsattBeløp = fastsattBeløp;
            this.inntektskategori = inntektskategori;
            this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
            this.nyAndel = nyAndel;
        }

        public int getAndelsnr() {
            return andelsnr;
        }

        public void setAndelsnr(int andelsnr) {
            this.andelsnr = andelsnr;
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettBrukersAndel that = (FastsettBrukersAndel) o;
        return andelsnr == that.andelsnr &&
                Double.compare(that.fastsattBeløp, fastsattBeløp) == 0 &&
                lagtTilAvSaksbehandler == that.lagtTilAvSaksbehandler &&
                nyAndel == that.nyAndel &&
                Objects.equals(inntektskategori, that.inntektskategori);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andelsnr, fastsattBeløp, inntektskategori, lagtTilAvSaksbehandler, nyAndel);
    }

    @Override
        public String toString() {
            return "YtelseAndeler{" +
                    "andelsnr=" + andelsnr +
                    ", fastsattBeløp=" + fastsattBeløp +
                    ", inntektskategori='" + inntektskategori + '\'' +
                    ", lagtTilAvSaksbehandler=" + lagtTilAvSaksbehandler +
                    ", nyAndel=" + nyAndel +
                    '}';
        }
}
