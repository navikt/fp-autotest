package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BehandlingÅrsakType {
    // MANUELL OPPRETTING - GUI-anvendelse
    RE_FEIL_I_LOVANDVENDELSE("RE-LOV"),
    RE_FEIL_REGELVERKSFORSTÅELSE("RE-RGLF"),
    RE_FEIL_ELLER_ENDRET_FAKTA("RE-FEFAKTA"),
    RE_FEIL_PROSESSUELL("RE-PRSSL"),
    RE_ANNET("RE-ANNET"),

    RE_OPPLYSNINGER_OM_MEDLEMSKAP("RE-MDL"),
    RE_OPPLYSNINGER_OM_OPPTJENING("RE-OPTJ"),
    RE_OPPLYSNINGER_OM_FORDELING("RE-FRDLING"),
    RE_OPPLYSNINGER_OM_INNTEKT("RE-INNTK"),
    RE_OPPLYSNINGER_OM_FØDSEL("RE-FØDSEL"),
    RE_OPPLYSNINGER_OM_DØD("RE-DØD"),
    RE_OPPLYSNINGER_OM_SØKERS_REL("RE-SRTB"),
    RE_OPPLYSNINGER_OM_SØKNAD_FRIST("RE-FRIST"),
    RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG("RE-BER-GRUN"),

    // KLAGE - Manuelt opprettet revurdering (obs: årsakene kan også bli satt på en automatisk opprettet revurdering)
    RE_KLAGE_UTEN_END_INNTEKT("RE-KLAG-U-INNTK"),
    RE_KLAGE_MED_END_INNTEKT("RE-KLAG-M-INNTK"),
    ETTER_KLAGE("ETTER_KLAGE"),

    // Etterkontroll + funksjonell
    RE_MANGLER_FØDSEL("RE-MF"),
    RE_MANGLER_FØDSEL_I_PERIODE("RE-MFIP"),
    RE_AVVIK_ANTALL_BARN("RE-AVAB"),

    // Mottak
    RE_ENDRING_FRA_BRUKER("RE-END-FRA-BRUKER"),
    RE_ENDRET_INNTEKTSMELDING("RE-END-INNTEKTSMELD"),
    BERØRT_BEHANDLING("BERØRT-BEHANDLING"),
    REBEREGN_FERIEPENGER("REBEREGN-FERIEPENGER"),

    // G-regulering
    RE_SATS_REGULERING("RE-SATS-REGULERING"),

    //For automatiske informasjonsbrev
    INFOBREV_BEHANDLING("INFOBREV_BEHANDLING"),
    INFOBREV_OPPHOLD("INFOBREV_OPPHOLD"),

    //For å vurdere opphør av ytelse
    OPPHØR_YTELSE_NYTT_BARN("OPPHØR-NYTT-BARN"),

    // Hendelser
    RE_HENDELSE_FØDSEL("RE-HENDELSE-FØDSEL"),
    RE_HENDELSE_DØD_FORELDER("RE-HENDELSE-DØD-F"),
    RE_HENDELSE_DØD_BARN("RE-HENDELSE-DØD-B"),
    RE_HENDELSE_DØDFØDSEL("RE-HENDELSE-DØDFØD"),
    ;

    private final String kode;

    BehandlingÅrsakType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static BehandlingÅrsakType fraKode(String kode) {
        return Arrays.stream(BehandlingÅrsakType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet behandlingårsakstype " + kode));
    }

    public String getKode() {
        return kode;
    }
}
