package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5046")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FordelBeregningsgrunnlagBekreftelse extends AksjonspunktBekreftelse {

    private static final Logger LOG = LoggerFactory.getLogger(FordelBeregningsgrunnlagBekreftelse.class);

    protected List<FastsettBeregningsgrunnlagPeriodeDto> endretBeregningsgrunnlagPerioder;

    public FordelBeregningsgrunnlagBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        LOG.info("oppdaterer beregning ap");
        var beregningsgrunnlag = behandling.getBeregningsgrunnlag();
        LOG.info("hentet beregningsgrunnlag {}", beregningsgrunnlag.getSkjaeringstidspunktBeregning());
        endretBeregningsgrunnlagPerioder = beregningsgrunnlag.getFaktaOmFordeling().getFordelBeregningsgrunnlag()
                .getFordelBeregningsgrunnlagPerioder().stream()
                .filter(FordelBeregningsgrunnlagPeriodeDto::isHarPeriodeAarsakGraderingEllerRefusjon)
                .map(p -> {
                    var bgPeriode = beregningsgrunnlag.getBeregningsgrunnlagPeriode(p.getFom());
                    return new FastsettBeregningsgrunnlagPeriodeDto(p, bgPeriode);
                })
                .collect(Collectors.toList());
        LOG.info("oppdaterer beregning ap return");
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategori(LocalDate fom, int fastsattBeløp,
                                                                                   Inntektskategori inntektskategori,
                                                                                   int andelsnr) {
        FastsettBeregningsgrunnlagAndelDto andel = getFastsettBeregningsgrunnlagAndelDto(fom, andelsnr);
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, inntektskategori));
        return this;
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategoriMedRefusjon(LocalDate fom,
            int fastsattBeløp, int refusjonPrÅr, Inntektskategori inntektskategori, int andelsnr) {
        LOG.info("fastsetter periode {}", fom);
        FastsettBeregningsgrunnlagAndelDto andel = getFastsettBeregningsgrunnlagAndelDto(fom, andelsnr);
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, refusjonPrÅr, inntektskategori));
        return this;
    }

    private FastsettBeregningsgrunnlagAndelDto getFastsettBeregningsgrunnlagAndelDto(LocalDate fom, int andelsnr) {
        FastsettBeregningsgrunnlagPeriodeDto periode = endretBeregningsgrunnlagPerioder.stream()
                .filter(p -> p.fom.isEqual(fom))
                .findFirst().orElseThrow();
        return periode.andeler.stream()
                .filter(a -> a.getAndelsnr() == andelsnr)
                .findFirst().orElseThrow();
    }

}
