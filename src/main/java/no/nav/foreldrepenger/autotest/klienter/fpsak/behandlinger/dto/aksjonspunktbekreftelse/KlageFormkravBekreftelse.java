package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class KlageFormkravBekreftelse extends AksjonspunktBekreftelse {

    private boolean erKlagerPart;
    private boolean erFristOverholdt;
    private boolean erKonkret;
    private boolean erSignert;
    private String vedtak; // påklagdBehandlingsId;

    public KlageFormkravBekreftelse() {
        super();
    }

    public KlageFormkravBekreftelse setErKlagerPart(boolean verdi) {
        erKlagerPart = verdi;
        return this;
    }

    public KlageFormkravBekreftelse setErFristOverholdt(boolean verdi) {
        erFristOverholdt = verdi;
        return this;
    }

    public KlageFormkravBekreftelse setErKonkret(boolean verdi) {
        erKonkret = verdi;
        return this;
    }

    public KlageFormkravBekreftelse setErSignert(boolean verdi) {
        erSignert = verdi;
        return this;
    }

    public KlageFormkravBekreftelse setPåklagdVedtak(String vedtakId) {
        this.vedtak = vedtakId;
        return this;
    }

    public KlageFormkravBekreftelse godkjennAlleFormkrav() {
        setErKlagerPart(true);
        setErFristOverholdt(true);
        setErKonkret(true);
        setErSignert(true);
        return this;
    }

    public KlageFormkravBekreftelse klageErIkkeKonkret() {
        setErKlagerPart(true);
        setErFristOverholdt(true);
        setErKonkret(false);
        setErSignert(true);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KlageFormkravBekreftelse that = (KlageFormkravBekreftelse) o;
        return erKlagerPart == that.erKlagerPart &&
                erFristOverholdt == that.erFristOverholdt &&
                erKonkret == that.erKonkret &&
                erSignert == that.erSignert &&
                Objects.equals(vedtak, that.vedtak);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erKlagerPart, erFristOverholdt, erKonkret, erSignert, vedtak);
    }

    @Override
    public String toString() {
        return "KlageFormkravBekreftelse{" +
                "erKlagerPart=" + erKlagerPart +
                ", erFristOverholdt=" + erFristOverholdt +
                ", erKonkret=" + erKonkret +
                ", erSignert=" + erSignert +
                ", vedtak='" + vedtak + '\'' +
                "} " + super.toString();
    }
}
