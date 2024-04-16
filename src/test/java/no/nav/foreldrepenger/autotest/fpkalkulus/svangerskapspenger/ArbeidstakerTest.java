package no.nav.foreldrepenger.autotest.fpkalkulus.svangerskapspenger;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Description;
import no.nav.folketrygdloven.kalkulus.kodeverk.Inntektskategori;
import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;

@Tag("fpkalkulus")
class ArbeidstakerTest extends Beregner {


    @DisplayName("Svangerskapspenger - arbeidstaker med inntektsmelding med full refusjon")
    @Description("Svangerskapspenger - arbeidstaker med inntektsmelding med full refusjon")
    @Test
    void svangerskapspenger_arbeidstaker_full_refusjon(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Svangerskapspenger - Arbeistaker med avvik og søkt refusjon før start av ytelse")
    @Description("Svangerskapspenger - Arbeistaker med avvik og søkt refusjon før start av ytelse")
    @Test
    void svp_søkt_refusjon_før_start_av_permisjon_og_avvik(TestInfo testInfo) throws Exception {
        beregnMedAvvik(testInfo, Map.of(1L, 637056, 2L, 76452), null, true);
    }

    @DisplayName("Svangerskapspenger - Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet.")
    @Description("Svangerskapspenger -  Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet. " +
            "Arbeidsforhold som avslutter før skjæringstidspunktet tas med i beregning. " +
            "Avvik i beregning pga redusert arbeidsinntekt ved start av SVP.")
    @Test
    void svp_arbeid_avslutter_dagen_før_stp(TestInfo testInfo) throws Exception {
        beregnMedAvvik(testInfo, Map.of(1L, 360000), null, true);
    }

    @DisplayName("Svangerskapspenger - Tilkommet arbeidsforhold med refusjon i deler av uttaket")
    @Description("Svangerskapspenger - Tilkommet arbeidsforhold med en periode i starten av uttaket der det utbetales" +
            " refusjon og en periode etterpå der det skal utbetales direkte til bruker.")
    @Test
    void svp_tilkommet_arbeidsforhold_refusjon_i_deler_av_uttak(TestInfo testInfo) throws Exception {
        behandleMedManuellFordeling(testInfo, null, Map.of(1L, 0, 2L, 501276), Map.of(
                1L, Inntektskategori.ARBEIDSTAKER,
                2L, Inntektskategori.ARBEIDSTAKER), Map.of(), true);
    }

}
