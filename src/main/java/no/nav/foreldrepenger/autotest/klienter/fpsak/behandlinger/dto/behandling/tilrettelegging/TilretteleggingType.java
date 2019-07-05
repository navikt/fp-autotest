package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.tilrettelegging;

import no.nav.foreldrepenger.autotest.klienter.spberegning.kodeverk.dto.KodeListe;

public class TilretteleggingType extends KodeListe {
    public static final String DISCRIMINATOR = "SVP_TILRETTELEGGING_TYPE";

    public static final TilretteleggingType HEL_TILRETTELEGGING = new TilretteleggingType("HEL_TILRETTELEGGING");
    public static final TilretteleggingType DELVIS_TILRETTELEGGING = new TilretteleggingType("DELVIS_TILRETTELEGGING");
    public static final TilretteleggingType INGEN_TILRETTELEGGING = new TilretteleggingType("INGEN_TILRETTELEGGING");

    public TilretteleggingType(String kode) {
        super(kode, DISCRIMINATOR);
    }


}
