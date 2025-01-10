package no.nav.foreldrepenger.autotest.fplos;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.TilretteleggingBehovBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.maler.ArbeidsforholdMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fplos")
class Fplos extends FpsakTestBase {

    @Test
    @DisplayName("Saksmarkering i fpsak gir oppgaveegenskap i LOS")
    @Description("Legger på saksmarkering som skal gi tilsvarende OppgaveEgenskap/AndreKriterier i LOS")
    void enkelSaksmarkering() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(6);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.getFirst().arbeidsgiverIdentifikasjon();

        var forsteTilrettelegging = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet((Orgnummer) arbeidsforhold1), termindato.minusMonths(3))
                .delvis(termindato.minusMonths(3), 50.0)
                .build();
        var søknad = lagSvangerskapspengerSøknad(termindato, List.of(forsteTilrettelegging));
        var saksnummerSVP = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerSVP);

        saksbehandler.endreSaksmarkering(saksnummerSVP, Set.of("SAMMENSATT_KONTROLL"));

        var oppgaver = saksbehandler.hentLosOppgaver(saksnummerSVP);
        assertThat(oppgaver).first().matches(o ->
                o.andreKriterier().contains("SAMMENSATT_KONTROLL"), "har egenskap SAMMENSATT_KONTROLL");
    }

}



























