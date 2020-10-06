package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class HistorikkInnslag { ;

    protected int behandlingId;
    protected Kode type;
    protected Kode aktoer;
    protected Kode kjoenn;
    protected List<HistorikkinnslagDel> historikkinnslagDeler;

    HistorikkInnslag(){
        // for test
    }

    public HistorikkInnslag(int behandlingId, Kode type, Kode aktoer, Kode kjoenn,
                            List<HistorikkinnslagDel> historikkinnslagDeler) {
        this.behandlingId = behandlingId;
        this.type = type;
        this.aktoer = aktoer;
        this.kjoenn = kjoenn;
        this.historikkinnslagDeler = historikkinnslagDeler;
    }

    public int getBehandlingId() {
        return behandlingId;
    }

    @JsonIgnore
    public String getTypeKode() {
        return type.kode;
    }

    public Kode getType() {
        return type;
    }

    public Kode getAktoer() {
        return aktoer;
    }

    public Kode getKjoenn() {
        return kjoenn;
    }

    public List<HistorikkinnslagDel> getHistorikkinnslagDeler() {
        return historikkinnslagDeler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorikkInnslag that = (HistorikkInnslag) o;
        return behandlingId == that.behandlingId &&
                Objects.equals(type, that.type) &&
                Objects.equals(aktoer, that.aktoer) &&
                Objects.equals(kjoenn, that.kjoenn) &&
                Objects.equals(historikkinnslagDeler, that.historikkinnslagDeler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, type, aktoer, kjoenn, historikkinnslagDeler);
    }

    @Override
    public String toString() {
        return "HistorikkInnslag{" +
                "behandlingId=" + behandlingId +
                ", type=" + type +
                ", aktoer=" + aktoer +
                ", kjoenn=" + kjoenn +
                ", historikkinnslagDeler=" + historikkinnslagDeler +
                '}';
    }

    public static final Type REVURD_OPPR = Type.REVURD_OPPR;
    public static final Type BEH_VENT = Type.BEH_VENT;
    public static final Type BREV_BESTILT = Type.BREV_BESTILT;
    public static final Type BREV_SENDT = Type.BREV_SENT;
    public static final Type VEDLEGG_MOTTATT = Type.VEDLEGG_MOTTATT;
    public static final Type VEDTAK_FATTET = Type.VEDTAK_FATTET;
    public static final Type BEHANDLINGEN_ER_FLYTTET = Type.SPOLT_TILBAKE;
    public static final Type NYE_REGOPPLYSNINGER = Type.NYE_REGOPPLYSNINGER;
    public static final Type UENDRET_UTFALL = Type.UENDRET_UTFALL;
    public static final Type BEH_OPPDATERT_NYE_OPPL = Type.BEH_OPPDATERT_NYE_OPPL;

    /** Historikkinnslag type. */
    public enum Type {
        REVURD_OPPR,
        BEH_VENT,
        BEH_OPPDATERT_NYE_OPPL,
        BREV_BESTILT,
        BREV_SENT,
        VEDLEGG_MOTTATT,
        VEDTAK_FATTET,
        SPOLT_TILBAKE,
        NYE_REGOPPLYSNINGER,
        UENDRET_UTFALL;

        public String getKode() {
            return name();
        }
    }
}
