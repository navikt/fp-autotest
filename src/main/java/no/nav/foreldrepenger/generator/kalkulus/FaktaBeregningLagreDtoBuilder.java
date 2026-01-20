package no.nav.foreldrepenger.generator.kalkulus;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.folketrygdloven.kalkulus.kodeverk.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.BesteberegningFødendeKvinneDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FaktaBeregningLagreDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FaktaOmBeregningTilfelleDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FastsettBgKunYtelseDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FastsettEtterlønnSluttpakkeDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FastsettMånedsinntektFLDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FastsettMånedsinntektUtenInntektsmeldingDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.MottarYtelseDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.RefusjonskravPrArbeidsgiverVurderingDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderATogFLiSammeOrganisasjonDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderEtterlønnSluttpakkeDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderLønnsendringDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderMilitærDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderNyoppstartetFLDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderSelvstendigNæringsdrivendeNyIArbeidslivetDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderTidsbegrensetArbeidsforholdDto;

public class FaktaBeregningLagreDtoBuilder {

    private VurderNyoppstartetFLDto vurderNyoppstartetFL;
    private VurderTidsbegrensetArbeidsforholdDto vurderTidsbegrensetArbeidsforhold;
    private VurderSelvstendigNæringsdrivendeNyIArbeidslivetDto vurderNyIArbeidslivet;
    private FastsettMånedsinntektFLDto fastsettMaanedsinntektFL;
    private VurderLønnsendringDto vurdertLonnsendring;
    private FastsettMånedsinntektUtenInntektsmeldingDto fastsattUtenInntektsmelding;
    private VurderATogFLiSammeOrganisasjonDto vurderATogFLiSammeOrganisasjon;
    private BesteberegningFødendeKvinneDto besteberegningAndeler;
    private FaktaOmBeregningTilfelleDto faktaOmBeregningTilfelleDto;
    private FastsettBgKunYtelseDto kunYtelseFordeling;
    private VurderEtterlønnSluttpakkeDto vurderEtterlønnSluttpakke;
    private FastsettEtterlønnSluttpakkeDto fastsettEtterlønnSluttpakke;
    private MottarYtelseDto mottarYtelse;
    private VurderMilitærDto vurderMilitaer;
    private List<RefusjonskravPrArbeidsgiverVurderingDto> refusjonskravGyldighet;

    public static FaktaBeregningLagreDtoBuilder ny() {
        return new FaktaBeregningLagreDtoBuilder();
    }

    public FaktaBeregningLagreDtoBuilder medRefusjonskravGyldighet(List<RefusjonskravPrArbeidsgiverVurderingDto> refusjonskravGyldighet) {
        this.refusjonskravGyldighet = refusjonskravGyldighet;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medVurderMilitær(VurderMilitærDto vurderMilitærDto) {
        this.vurderMilitaer = vurderMilitærDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medMottarYtelse(MottarYtelseDto mottarYtelseDto) {
        this.mottarYtelse = mottarYtelseDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medFastsettEtterlønnSluttpakke(FastsettEtterlønnSluttpakkeDto fastsettEtterlønnSluttpakkeDto) {
        this.fastsettEtterlønnSluttpakke = fastsettEtterlønnSluttpakkeDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medVurderEtterlønnSluttpakke(VurderEtterlønnSluttpakkeDto vurderEtterlønnSluttpakkeDto) {
        this.vurderEtterlønnSluttpakke = vurderEtterlønnSluttpakkeDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medFastsettBgKunYtelse(FastsettBgKunYtelseDto fastsettBgKunYtelseDto) {
        this.kunYtelseFordeling = fastsettBgKunYtelseDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medFaktaOmBeregningTilfeller(List<FaktaOmBeregningTilfelle> faktaOmBeregningTilfeller) {
        List<FaktaOmBeregningTilfelle> faktaOmBeregningTilfelles = faktaOmBeregningTilfeller.stream()
                .map(FaktaOmBeregningTilfelle::getKode)
                .map(FaktaOmBeregningTilfelle::fraKode)
                .collect(Collectors.toList());
        this.faktaOmBeregningTilfelleDto = new FaktaOmBeregningTilfelleDto(faktaOmBeregningTilfelles);
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medBesteberegningFødendeKvinneDto(BesteberegningFødendeKvinneDto besteberegningFødendeKvinneDto) {
        this.besteberegningAndeler = besteberegningFødendeKvinneDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medVurderNyoppstartetFL(VurderNyoppstartetFLDto vurderNyoppstartetFLDto) {
        this.vurderNyoppstartetFL = vurderNyoppstartetFLDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medVurderTidsbegrensetArbeidsforhold(VurderTidsbegrensetArbeidsforholdDto vurderTidsbegrensetArbeidsforholdDto) {
        this.vurderTidsbegrensetArbeidsforhold = vurderTidsbegrensetArbeidsforholdDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medVurderSelvstendigNæringsdrivendeNyIArbeidslivet(VurderSelvstendigNæringsdrivendeNyIArbeidslivetDto vurderSelvstendigNæringsdrivendeNyIArbeidslivetDto) {
        this.vurderNyIArbeidslivet = vurderSelvstendigNæringsdrivendeNyIArbeidslivetDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medFastsettMånedsinntektFL(FastsettMånedsinntektFLDto fastsettMånedsinntektFLDto) {
        this.fastsettMaanedsinntektFL = fastsettMånedsinntektFLDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medVurderLønnsendring(VurderLønnsendringDto vurderLønnsendringDto) {
        this.vurdertLonnsendring = vurderLønnsendringDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medFastsettMånedsinntektUtenInntektsmelding(FastsettMånedsinntektUtenInntektsmeldingDto fastsettMånedsinntektUtenInntektsmeldingDto) {
        this.fastsattUtenInntektsmelding = fastsettMånedsinntektUtenInntektsmeldingDto;
        return this;
    }

    public FaktaBeregningLagreDtoBuilder medVurderATogFLiSammeOrganisasjonDto(VurderATogFLiSammeOrganisasjonDto vurderATogFLiSammeOrganisasjonDto) {
        this.vurderATogFLiSammeOrganisasjon = vurderATogFLiSammeOrganisasjonDto;
        return this;
    }

    public FaktaBeregningLagreDto build() {
        return new FaktaBeregningLagreDto(
                vurderNyoppstartetFL,
                vurderTidsbegrensetArbeidsforhold,
                vurderNyIArbeidslivet,
                fastsettMaanedsinntektFL,
                vurdertLonnsendring,
                fastsattUtenInntektsmelding,
                vurderATogFLiSammeOrganisasjon,
                besteberegningAndeler,
                faktaOmBeregningTilfelleDto,
                kunYtelseFordeling,
                vurderEtterlønnSluttpakke,
                fastsettEtterlønnSluttpakke,
                mottarYtelse,
                vurderMilitaer,
                refusjonskravGyldighet
        );
    }

}
