package no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum AnnenOpptjeningType {
    LØNN_UNDER_UTDANNING,
    ETTERLØNN_ARBEIDSGIVER,
    MILITÆR_ELLER_SIVILTJENESTE,
    VENTELØNN,
    VARTPENGER,
    SLUTTPAKKE,
    VENTELØNN_VARTPENGER,
    ETTERLØNN_SLUTTPAKKE
}
