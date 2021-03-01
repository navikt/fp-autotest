package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;
import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HistorikkinnslagType implements Kode {

    BREV_SENT,
    BREV_BESTILT,
    BEH_STARTET_PÅ_NYTT,
    BEH_STARTET,
    BEH_OPPDATERT_NYE_OPPL,
    BEH_MAN_GJEN,
    BEH_GJEN,
    BEH_AVBRUTT_VUR,
    ANKEBEH_STARTET,
    VRS_REV_IKKE_SNDT,
    VEDLEGG_MOTTATT,
    TERMINBEKREFTELSE_UGYLDIG,
    SPOLT_TILBAKE,
    REVURD_OPPR,
    REGISTRER_PAPIRSØK,
    NYE_REGOPPLYSNINGER,
    MIGRERT_FRA_INFOTRYGD_FJERNET,
    MIGRERT_FRA_INFOTRYGD,
    MANGELFULL_SØKNAD,
    KLAGEBEH_STARTET,
    INNSYN_OPPR,
    KØET_BEH_GJEN,
    VEDTAK_FATTET,
    UENDRET_UTFALL,
    TILBAKEKR_VIDEREBEHANDLING,
    REGISTRER_OM_VERGE,
    FORSLAG_VEDTAK_UTEN_TOTRINN,
    FORSLAG_VEDTAK,
    SAK_RETUR,
    SAK_GODKJENT,
    FJERNET_VERGE,
    IVERKSETTELSE_VENT,
    BEH_VENT,
    BEH_KØET,
    AVBRUTT_BEH,
    UTTAK,
    KLAGE_BEH_NK,
    KLAGE_BEH_NFP,
    FAKTA_ENDRET,
    BYTT_ENHET,
    ANKE_BEH,
    NY_INFO_FRA_TPS,
    OVERSTYRT,
    OPPTJENING,
    OVST_UTTAK_SPLITT,
    FASTSATT_UTTAK_SPLITT,
    FASTSATT_UTTAK,
    OVST_UTTAK,
    AVKLART_AKTIVITETSKRAV,
    ;

    private final String kode;

    HistorikkinnslagType() {
        this(null);
    }

    HistorikkinnslagType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }


    @JsonCreator
    public static HistorikkinnslagType fraKode(String kode) {
        return Arrays.stream(HistorikkinnslagType.values())
                .filter(value -> value.name().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet HistorikkinnslagType %s.", kode));
    }

    @Override
    public String getKode() {
        return kode;
    }
}
