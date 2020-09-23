package no.nav.foreldrepenger.autotest.klienter.fpsak.brev.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestillBrev {

    private int behandlingId;
    private String mottaker;
    private String brevmalkode;
    private String fritekst;
    private String arsakskode;
    private String årsakskode;

    public BestillBrev(int behandlingId, String mottaker, String brevmalkode, String fritekst) {
        super();
        this.behandlingId = behandlingId;
        this.mottaker = mottaker;
        this.brevmalkode = brevmalkode;
        this.fritekst = fritekst;
    }

    @JsonCreator
    public BestillBrev(int behandlingId, String mottaker, String brevmalkode, String fritekst, String arsakskode,
                       String årsakskode) {
        this.behandlingId = behandlingId;
        this.mottaker = mottaker;
        this.brevmalkode = brevmalkode;
        this.fritekst = fritekst;
        this.arsakskode = arsakskode;
        this.årsakskode = årsakskode;
    }

    public int getBehandlingId() {
        return behandlingId;
    }

    public String getMottaker() {
        return mottaker;
    }

    public String getBrevmalkode() {
        return brevmalkode;
    }

    public String getFritekst() {
        return fritekst;
    }

    public String getArsakskode() {
        return arsakskode;
    }

    public String getÅrsakskode() {
        return årsakskode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BestillBrev that = (BestillBrev) o;
        return behandlingId == that.behandlingId &&
                Objects.equals(mottaker, that.mottaker) &&
                Objects.equals(brevmalkode, that.brevmalkode) &&
                Objects.equals(fritekst, that.fritekst) &&
                Objects.equals(arsakskode, that.arsakskode) &&
                Objects.equals(årsakskode, that.årsakskode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, mottaker, brevmalkode, fritekst, arsakskode, årsakskode);
    }

    @Override
    public String toString() {
        return "BestillBrev{" +
                "behandlingId=" + behandlingId +
                ", mottaker='" + mottaker + '\'' +
                ", brevmalkode='" + brevmalkode + '\'' +
                ", fritekst='" + fritekst + '\'' +
                ", arsakskode='" + arsakskode + '\'' +
                ", årsakskode='" + årsakskode + '\'' +
                '}';
    }
}
