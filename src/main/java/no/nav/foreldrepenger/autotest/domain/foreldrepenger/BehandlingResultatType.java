package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

public enum BehandlingResultatType {

    IKKE_FASTSATT,
    INNVILGET,
    AVSLÅTT,
    OPPHØR,
    HENLAGT_SØKNAD_TRUKKET,
    HENLAGT_FEILOPPRETTET,
    HENLAGT_BRUKER_DØD,
    MERGET_OG_HENLAGT,
    HENLAGT_SØKNAD_MANGLER,
    FORELDREPENGER_ENDRET,
    FORELDREPENGER_SENERE,
    INGEN_ENDRING,
    MANGLER_BEREGNINGSREGLER,

    // Tilbakekrevinger
    FASTSATT,
    HENLAGT_FEILOPPRETTET_MED_BREV,
    HENLAGT_FEILOPPRETTET_UTEN_BREV,
    HENLAGT_KRAVGRUNNLAG_NULLSTILT,
    HENLAGT_TEKNISK_VEDLIKEHOLD,
    HENLAGT,
    INGEN_TILBAKEBETALING,
    DELVIS_TILBAKEBETALING,
    FULL_TILBAKEBETALING,

    // Klage
    KLAGE_AVVIST,
    KLAGE_MEDHOLD,
    KLAGE_DELVIS_MEDHOLD,
    KLAGE_OMGJORT_UGUNST,
    KLAGE_YTELSESVEDTAK_OPPHEVET,
    KLAGE_YTELSESVEDTAK_STADFESTET,
    KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET, // Brukes av kun Tilbakekreving eller Tilbakekreving Revurdering
    HENLAGT_KLAGE_TRUKKET,
    HJEMSENDE_UTEN_OPPHEVE,

    // Anke
    ANKE_AVVIST,
    ANKE_MEDHOLD,
    ANKE_DELVIS_MEDHOLD,
    ANKE_OMGJORT_UGUNST,
    ANKE_OPPHEVE_OG_HJEMSENDE,
    ANKE_HJEMSENDE_UTEN_OPPHEV,
    ANKE_YTELSESVEDTAK_STADFESTET,
    HENLAGT_ANKE_TRUKKET,

    // Innsyn
    INNSYN_INNVILGET,
    INNSYN_DELVIS_INNVILGET,
    INNSYN_AVVIST,
    HENLAGT_INNSYN_TRUKKET,

}
