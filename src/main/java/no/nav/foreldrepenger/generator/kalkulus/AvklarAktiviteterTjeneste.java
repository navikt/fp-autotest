package no.nav.foreldrepenger.generator.kalkulus;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import no.nav.folketrygdloven.kalkulus.håndtering.v1.avklaraktiviteter.BeregningsaktivitetLagreDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.overstyring.OverstyrBeregningsaktiviteterDto;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;

public class AvklarAktiviteterTjeneste {

    private AvklarAktiviteterTjeneste() {
        // Skjul
    }

    /**
     * Lager overstyringsdto for beregningsaktiviteter. Tar inn beregningsgrunnlag og et map fra startdato for aktivitet til om aktiviteten skal brukes.
     *
     * Om en aktivitetdato-fom ikke finnes i mappet defaulter den til å bruke aktiviteten (i motsetning til å fjerne fra beregning).
     *
     * @param beregningsgrunnlagDto Beregningsgrunnlag fra gui
     * @param startDatoSkalBrukesMap map for aktiviteter som skal fjernes
     * @return Overstyringsdto for beregningsaktiviteter
     */
    public static OverstyrBeregningsaktiviteterDto lagOverstyrAktiviteterDto(BeregningsgrunnlagDto beregningsgrunnlagDto, Map<LocalDate, Boolean> startDatoSkalBrukesMap) {
        var aktiviteter = beregningsgrunnlagDto.getFaktaOmBeregning().getAvklarAktiviteter().getAktiviteterTomDatoMapping().get(0).getAktiviteter();

        var beregningsaktivitetLagreDtoList = aktiviteter.stream().map(a -> BeregningsaktivitetLagreDto.builder()
                .medFom(a.getFom())
                .medTom(a.getTom())
                .medArbeidsforholdRef(a.getArbeidsforholdId() != null ? UUID.fromString(a.getArbeidsforholdId()) : null)
                .medArbeidsgiverIdentifikator(a.getArbeidsgiverIdent())
                .medOppdragsgiverOrg(a.getArbeidsgiverIdent())
                .medOpptjeningAktivitetType(a.getArbeidsforholdType())
                .medSkalBrukes(!startDatoSkalBrukesMap.containsKey(a.getFom()) || startDatoSkalBrukesMap.get(a.getFom()))
                .build())
                .toList();
        return new OverstyrBeregningsaktiviteterDto(beregningsaktivitetLagreDtoList);
    }

}
