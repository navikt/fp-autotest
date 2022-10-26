package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.InntektArbeidYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ArbeidOgInntektsmeldingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.Beregningsresultat;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger.Feriepengegrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.Medlem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Tilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.autotest.util.vent.Lazy;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Behandling {

    //TODO palfi fjern all bruk av id
    //bruk uuid
    @Deprecated(forRemoval = true)
    public int id;
    public UUID uuid;
    public int versjon;
    public long fagsakId;
    public BehandlingType type;
    public BehandlingStatus status;

    public LocalDate skjaringstidspunkt;
    public LocalDateTime avsluttet;
    public LocalDateTime opprettet;
    public LocalDate fristBehandlingPaaVent;

    public String ansvarligSaksbehandler;
    public String behandlendeEnhet;
    public String behandlendeEnhetNavn;
    public Boolean behandlingHenlagt;
    public Boolean behandlingPaaVent;
    public String venteArsakKode;
    public Behandlingsresultat behandlingsresultat;
    public List<BehandlingÅrsak> behandlingÅrsaker;

    // TODO: Spesifikk for fptilbake
    public boolean harVerge;

    private Lazy<List<Vilkar>> vilkar;
    private Lazy<List<Aksjonspunkt>> aksjonspunkter;
    private Lazy<Beregningsgrunnlag> beregningsgrunnlag;
    private Lazy<Beregningsresultat> beregningResultatEngangsstonad;
    private Lazy<BeregningsresultatMedUttaksplan> beregningResultatForeldrepenger;
    private Lazy<UttakResultatPerioder> uttakResultatPerioder;
    private Lazy<Feriepengegrunnlag> feriepengegrunnlag;
    private Lazy<Soknad> soknad;
    private Lazy<Opptjening> opptjening;
    private Lazy<InntektArbeidYtelse> inntektArbeidYtelse;
    private Lazy<KontrollerFaktaData> kontrollerFaktaData;
    private Lazy<Medlem> medlem;
    private Lazy<KlageInfo> klagevurdering;
    private Lazy<Saldoer> saldoer;
    private Lazy<Tilrettelegging> tilrettelegging;
    private Lazy<ArbeidOgInntektsmeldingDto> arbeidOgInntektsmeldingDto;
    private Lazy<List<KontrollerAktiviteskravPeriode>> kontrollerAktiviteskrav;

    public List<UttakResultatPeriode> hentUttaksperioder() {
        return getUttakResultatPerioder().getPerioderSøker().stream()
                .sorted(Comparator.comparing(UttakResultatPeriode::getFom)).collect(Collectors.toList());
    }

    public UttakResultatPeriode hentUttaksperiode(int index) {
        return hentUttaksperioder().get(index);
    }

    public boolean erSattPåVent() {
        return behandlingPaaVent;
    }

    public boolean erHenlagt() {
        return behandlingHenlagt;
    }

    public BehandlingResultatType hentBehandlingsresultat() {
        return behandlingsresultat.type();
    }

    public Avslagsårsak hentAvslagsarsak() {
        return behandlingsresultat.avslagsarsak();
    }

    public List<KontrollerAktiviteskravPeriode> getKontrollerAktiviteskrav() {
        return get(kontrollerAktiviteskrav);
    }

    @Override
    public String toString() {
        // Ikke bruk fields som er deferred i tostring, skaper mange kall
        StringBuilder sb = new StringBuilder();
        sb.append("{Behandlingsid: ").append(this.uuid).append("}\n");
        sb.append("{Behandlingsstatus: ").append(this.status.getKode()).append("}\n");
        sb.append("{Behandlingstype: ").append(this.type.getKode()).append("}");
        if ((this.behandlingsresultat != null) && (this.behandlingsresultat.avslagsarsak() != null)) {
            sb.append("{Årsak avslag: ").append(this.behandlingsresultat.avslagsarsak().getKode()).append("}\n");
        }
        return sb.toString();
    }

    public List<BehandlingÅrsak> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public List<Vilkar> getVilkar() {
        return get(vilkar);
    }

    public void setVilkar(Lazy<List<Vilkar>> dVilkår) {
        this.vilkar = dVilkår;
    }

    public Beregningsgrunnlag getBeregningsgrunnlag() {
        return get(beregningsgrunnlag);
    }

    public void setBeregningsgrunnlag(Lazy<Beregningsgrunnlag> dBeregningsgrunnlag) {
        this.beregningsgrunnlag = dBeregningsgrunnlag;
    }

    public Beregningsresultat getBeregningResultatEngangsstonad() {
        return get(beregningResultatEngangsstonad);
    }

    public void setBeregningResultatEngangsstonad(Lazy<Beregningsresultat> dBeregningsresultat) {
        this.beregningResultatEngangsstonad = dBeregningsresultat;
    }

    public BeregningsresultatMedUttaksplan getBeregningResultatForeldrepenger() {
        return get(beregningResultatForeldrepenger);
    }

    public void setBeregningResultatForeldrepenger(
            Lazy<BeregningsresultatMedUttaksplan> dBeregningsresultatMedUttaksplan) {
        this.beregningResultatForeldrepenger = dBeregningsresultatMedUttaksplan;
    }

    public void setFeriepengegrunnlag(
            Lazy<Feriepengegrunnlag> feriepengegrunnlag) {
        this.feriepengegrunnlag = feriepengegrunnlag;
    }

    public Feriepengegrunnlag getFeriepengegrunnlag() {
        return get(feriepengegrunnlag);
    }


    public UttakResultatPerioder getUttakResultatPerioder() {
        return get(uttakResultatPerioder);
    }

    public void setUttakResultatPerioder(Lazy<UttakResultatPerioder> dUttakResultatPerioder) {
        this.uttakResultatPerioder = dUttakResultatPerioder;
    }

    public Soknad getSoknad() {
        return get(soknad);
    }

    public void setSoknad(Lazy<Soknad> dSoknad) {
        this.soknad = dSoknad;
    }

    public Opptjening getOpptjening() {
        return get(opptjening);
    }

    public void setOpptjening(Lazy<Opptjening> dOpptjening) {
        this.opptjening = dOpptjening;
    }

    public InntektArbeidYtelse getInntektArbeidYtelse() {
        return get(inntektArbeidYtelse);
    }

    public void setInntektArbeidYtelse(Lazy<InntektArbeidYtelse> dInntektArbeidYtelse) {
        this.inntektArbeidYtelse = dInntektArbeidYtelse;
    }

    public KontrollerFaktaData getKontrollerFaktaData() {
        return get(kontrollerFaktaData);
    }

    public void setKontrollerFaktaData(Lazy<KontrollerFaktaData> dKontrollerFaktaData) {
        this.kontrollerFaktaData = dKontrollerFaktaData;
    }

    public void setKontrollerAktivitetskrav(Lazy<List<KontrollerAktiviteskravPeriode>> data) {
        this.kontrollerAktiviteskrav = data;
    }

    public Medlem getMedlem() {
        return get(medlem);
    }

    public void setMedlem(Lazy<Medlem> dMedlem) {
        this.medlem = dMedlem;
    }

    public KlageInfo getKlagevurdering() {
        return get(klagevurdering);
    }

    public void setKlagevurdering(Lazy<KlageInfo> klagevurdering) {
        this.klagevurdering = klagevurdering;
    }

    public Saldoer getSaldoer() {
        return get(saldoer);
    }

    public void setSaldoer(Lazy<Saldoer> dStonadskontoer) {
        this.saldoer = dStonadskontoer;
    }

    public List<Aksjonspunkt> getAksjonspunkter() {
        return get(aksjonspunkter);
    }

    public void setAksjonspunkter(Lazy<List<Aksjonspunkt>> dAksjonspunkter) {
        this.aksjonspunkter = dAksjonspunkter;
    }

    public Tilrettelegging getTilrettelegging() {
        return get(tilrettelegging);
    }

    public void setTilrettelegging(Lazy<Tilrettelegging> dTilrettelegging) {
        this.tilrettelegging = dTilrettelegging;
    }

    public void setArbeidInntektsmelding(Lazy<ArbeidOgInntektsmeldingDto> arbeidInntektsmelding) {
        this.arbeidOgInntektsmeldingDto = arbeidInntektsmelding;
    }

    public ArbeidOgInntektsmeldingDto getArbeidOgInntektsmeldingDto() {
        return get(arbeidOgInntektsmeldingDto);
    }

    public static <V> V get(Lazy<V> o) {
        return o == null ? null : o.get();
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }
}
