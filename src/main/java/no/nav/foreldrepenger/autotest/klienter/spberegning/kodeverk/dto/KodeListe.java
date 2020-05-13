package no.nav.foreldrepenger.autotest.klienter.spberegning.kodeverk.dto;

import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KodeListe extends ArrayList<Kode>{
    private String kode;
    private String kodeverk;

    public KodeListe () {
    }
    public KodeListe(String kode, String kodeverk) {
        Objects.requireNonNull(kode, "kode"); //$NON-NLS-1$
        Objects.requireNonNull(kodeverk, "kodeverk"); //$NON-NLS-1$
        this.kode = kode;
        this.kodeverk = kodeverk;
    }

    /** @deprecated - IKKE bruk navn for kodeverdi ved oppslag, bruk kode. */
    @Deprecated
    public Kode getKode(String kodeverdi) {
        for (Kode kode : this) {
            if(kode.kode.equals(kodeverdi) || kode.navn.equals(kodeverdi)) { //Kan hente kode basert på kode eller navn
                return kode;
            }
        }
        return null;
    }
}
