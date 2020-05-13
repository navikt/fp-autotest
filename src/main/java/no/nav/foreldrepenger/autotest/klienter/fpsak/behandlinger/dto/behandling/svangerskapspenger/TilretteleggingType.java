package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

public class TilretteleggingType extends Kode {

    private static final String DISCRIMINATOR = "SVP_TILRETTELEGGING_TYPE";

    public static final TilretteleggingType HEL_TILRETTELEGGING = new TilretteleggingType("HEL_TILRETTELEGGING", "hel_tilrettelegging");
    public static final TilretteleggingType DELVIS_TILRETTELEGGING = new TilretteleggingType("DELVIS_TILRETTELEGGING", "delvis_tilrettelegging");
    public static final TilretteleggingType INGEN_TILRETTELEGGING = new TilretteleggingType("INGEN_TILRETTELEGGING", "ingen_tilrettelegging");

    public TilretteleggingType() {}

    private TilretteleggingType(String kode, String navn) {
        super(DISCRIMINATOR,kode,navn);
    }
}
