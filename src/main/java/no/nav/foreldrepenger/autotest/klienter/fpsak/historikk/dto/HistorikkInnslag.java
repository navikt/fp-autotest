package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HistorikkInnslag(int behandlingId, Kode type, Kode aktoer, Kode kjoenn,
                               List<HistorikkInnslagDokumentLinkDto> dokumentLinks,
                               List<HistorikkinnslagDel> historikkinnslagDeler) {

    @JsonIgnore
    public String getTypeKode() {
        return type.kode;
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
