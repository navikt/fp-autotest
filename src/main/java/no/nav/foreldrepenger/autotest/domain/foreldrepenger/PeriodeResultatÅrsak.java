package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import no.nav.foreldrepenger.autotest.util.jackson.PeriodeResultatÅrsakDeserializer;

@JsonDeserialize(using = PeriodeResultatÅrsakDeserializer.class)
public interface PeriodeResultatÅrsak {
    PeriodeResultatÅrsak UKJENT = new PeriodeResultatÅrsak() {
        @Override
        public String getKode() {
            return "-";
        }

        @Override
        public String getNavn() {
            return "Ikke definert";
        }

        @Override
        public String getKodeverk() {
            return "PERIODE_RESULTAT_AARSAK";
        }
    };


    String getKode();
    String getNavn();
    String getKodeverk();
}
