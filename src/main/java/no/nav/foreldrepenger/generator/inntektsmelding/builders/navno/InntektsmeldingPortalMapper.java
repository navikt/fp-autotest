package no.nav.foreldrepenger.generator.inntektsmelding.builders.navno;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.AktørIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.ArbeidsgiverDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.NaturalytelsetypeDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.SendInntektsmeldingDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.YtelseType;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;

public class InntektsmeldingPortalMapper {

    private InntektsmeldingPortalMapper() {
        // skjul ctor
    }

    public static SendInntektsmeldingDto map(Inntektsmelding im, UUID forespørselUuid, AktørIdDto aktørIdDto, LocalDate startDato) {
        return new SendInntektsmeldingDto(
                forespørselUuid,
                aktørIdDto,
                mapYtelseType(im.ytelseType()),
                new ArbeidsgiverDto(im.arbeidsgiver().arbeidsgiverIdentifikator()),
                new SendInntektsmeldingDto.KontaktpersonRequestDto(im.arbeidsgiver().navn(), im.arbeidsgiver().kontaktnummer()),
                startDato,
                im.arbeidsforhold().beregnetInntekt(),
                mapRefusjon(im.refusjon(), im.arbeidsforhold().førsteFraværsdag()),
                mapBortfalteNaturalytelser(im.opphørAvNaturalytelseList()),
                Collections.emptyList());

    }

    private static @NotNull List<SendInntektsmeldingDto.Refusjon> mapRefusjon(Inntektsmelding.Refusjon refusjon, LocalDate refusjonStartDato) {
        if (refusjon == null) {
            return Collections.emptyList();
        }

        return List.of(new SendInntektsmeldingDto.Refusjon(refusjonStartDato, refusjon.refusjonBeløpPrMnd()));
    }

    private static List<SendInntektsmeldingDto.BortfaltNaturalytelseRequestDto> mapBortfalteNaturalytelser(List<Inntektsmelding.OpphørAvNaturalytelse> opphørteNaturalytelser) {
        return opphørteNaturalytelser
                .stream()
                .map(naturalytelse -> new SendInntektsmeldingDto.BortfaltNaturalytelseRequestDto(naturalytelse.fom(),
                        naturalytelse.fom(), mapNaturalytelseType(naturalytelse.natyralYtelseType()), naturalytelse.beloepPrMnd()))
                .toList();
    }

    private static @NotNull NaturalytelsetypeDto mapNaturalytelseType(Inntektsmelding.NaturalytelseType naturalytelseType) {
        return switch (naturalytelseType) {
            case BIL -> NaturalytelsetypeDto.BIL;
            case KOST_DAGER -> NaturalytelsetypeDto.KOST_DAGER;
            case FRI_TRANSPORT -> NaturalytelsetypeDto.FRI_TRANSPORT;
            case ANNET -> NaturalytelsetypeDto.ANNET;
            case BOLIG -> NaturalytelsetypeDto.BOLIG;
            case LOSJI -> NaturalytelsetypeDto.LOSJI;
            case OPSJONER -> NaturalytelsetypeDto.OPSJONER;
            case ELEKTRISK_KOMMUNIKASJON -> NaturalytelsetypeDto.ELEKTRISK_KOMMUNIKASJON;
            case KOST_DOEGN -> NaturalytelsetypeDto.KOST_DOEGN;
            case BEDRIFTSBARNEHAGEPLASS -> NaturalytelsetypeDto.BEDRIFTSBARNEHAGEPLASS;
            case RENTEFORDEL_LÅN -> NaturalytelsetypeDto.RENTEFORDEL_LÅN;
            case TILSKUDD_BARNEHAGEPLASS -> NaturalytelsetypeDto.TILSKUDD_BARNEHAGEPLASS;
            case KOSTBESPARELSE_I_HJEMMET -> NaturalytelsetypeDto.KOSTBESPARELSE_I_HJEMMET;
            case BESØKSREISER_HJEMMET_ANNET -> NaturalytelsetypeDto.BESØKSREISER_HJEMMET_ANNET;
            case SKATTEPLIKTIG_DEL_FORSIKRINGER -> NaturalytelsetypeDto.SKATTEPLIKTIG_DEL_FORSIKRINGER;
            case YRKEBIL_TJENESTLIGBEHOV_KILOMETER -> NaturalytelsetypeDto.YRKEBIL_TJENESTLIGBEHOV_KILOMETER;
            case YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS -> NaturalytelsetypeDto.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS;
            case AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS -> NaturalytelsetypeDto.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS;
            case INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING -> NaturalytelsetypeDto.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING;
        };
    }

    private static @NotNull @Valid YtelseType mapYtelseType(Inntektsmelding.YtelseType ytelseType) {
        return switch (ytelseType) {
            case FORELDREPENGER -> YtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> YtelseType.SVANGERSKAPSPENGER;
        };
    }
}
