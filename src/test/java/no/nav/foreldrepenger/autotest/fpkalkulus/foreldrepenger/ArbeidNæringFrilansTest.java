package no.nav.foreldrepenger.autotest.fpkalkulus.foreldrepenger;

import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVarigEndring;
import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVedAvvik;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Description;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.response.v1.TilstandResponse;
import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;

@Tag("fpkalkulus")
class ArbeidNæringFrilansTest extends Beregner {

    @DisplayName("Foreldrepenger - arbeidsforhold og selvstendig næringsdrivende")
    @Description("Foreldrepenger - arbeidsforhold og selvstendig næringsdrivende, gjøres avviksvurdering på begge to. Finner avvik på begge.")
    @Test
    @Disabled // Avhenger av toggle lokalt og at vi nærmer oss dato 1.1.2023. Kan tas tilbake i desember 2022
    void fp_arbeid_næring_avvik_på_begge(TestInfo testInfo) throws Exception {
        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        saksbehandler.håndterBeregning(lagHåndterRequest(request, fastsettInntektVedAvvik(Map.of(1L, 300000))));

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        var håndterBeregningDto = fastsettInntektVarigEndring(null, false);
        saksbehandler.håndterBeregning(lagHåndterRequest(request, håndterBeregningDto));

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);

        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

}
