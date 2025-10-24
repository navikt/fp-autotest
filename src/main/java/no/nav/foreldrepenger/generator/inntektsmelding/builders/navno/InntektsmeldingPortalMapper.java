package no.nav.foreldrepenger.generator.inntektsmelding.builders.navno;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.InntektsmeldingKlient;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.AktørIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.ArbeidsgiverDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.NaturalytelsetypeDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.SendInntektsmeldingDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.YtelseType;
import no.nav.foreldrepenger.autotest.util.CollectionUtils;
import no.nav.foreldrepenger.generator.familie.AktørId;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;

public class InntektsmeldingPortalMapper {

    private InntektsmeldingPortalMapper() {
        // skjul ctor
    }
    public static SendInntektsmeldingDto mapUtenForespørsel(Inntektsmelding im, AktørId aktørId, LocalDate startDato, boolean registrertIAareg) {
        return new SendInntektsmeldingDto(
                null,
                new AktørIdDto(aktørId.value()),
                mapYtelseType(im.ytelseType()),
                registrertIAareg ? SendInntektsmeldingDto.ArbeidsgiverinitiertÅrsakDto.NYANSATT : SendInntektsmeldingDto.ArbeidsgiverinitiertÅrsakDto.UREGISTRERT,
                new ArbeidsgiverDto(im.arbeidsgiver().arbeidsgiverIdentifikator()),
                new SendInntektsmeldingDto.KontaktpersonRequestDto(im.arbeidsgiver().navn(), im.arbeidsgiver().kontaktnummer()),
                startDato,
                im.arbeidsforhold().beregnetInntekt(),
                mapRefusjon(im.refusjon(), startDato),
                mapBortfalteNaturalytelser(im.opphørAvNaturalytelseList()),
                Collections.emptyList());
    }


    public static SendInntektsmeldingDto map(Inntektsmelding im, InntektsmeldingKlient.InntektsmeldingForespørselDto forespørsel) {
        return new SendInntektsmeldingDto(
                forespørsel.uuid(),
                forespørsel.aktørid(),
                forespørsel.ytelsetype(),
                null,
                forespørsel.arbeidsgiverident(),
                new SendInntektsmeldingDto.KontaktpersonRequestDto(im.arbeidsgiver().navn(), im.arbeidsgiver().kontaktnummer()),
                forespørsel.startDato(),
                im.arbeidsforhold().beregnetInntekt(),
                mapRefusjon(im.refusjon(), forespørsel.startDato()),
                mapBortfalteNaturalytelser(im.opphørAvNaturalytelseList()),
                Collections.emptyList());
    }

    private static  List<SendInntektsmeldingDto.Refusjon> mapRefusjon(Inntektsmelding.Refusjon refusjon, LocalDate refusjonStartDato) {
        if (refusjon == null) {
            return Collections.emptyList();
        }
        var resultatList = new ArrayList<>(List.of(new SendInntektsmeldingDto.Refusjon(refusjonStartDato, refusjon.refusjonBeløpPrMnd())));
        if (CollectionUtils.isNotEmpty(refusjon.refusjonEndringList())) {
            resultatList.addAll(refusjon.refusjonEndringList().stream().map(endring -> new SendInntektsmeldingDto.Refusjon(endring.fom(), endring.beloepPrMnd())).toList());
        }
        if (refusjon.refusjonOpphørsdato() != null) {
            resultatList.add(new SendInntektsmeldingDto.Refusjon(refusjon.refusjonOpphørsdato(), BigDecimal.ZERO));
        }
        return resultatList;
    }

    private static List<SendInntektsmeldingDto.BortfaltNaturalytelseRequestDto> mapBortfalteNaturalytelser(List<Inntektsmelding.OpphørAvNaturalytelse> opphørteNaturalytelser) {
        return opphørteNaturalytelser
                .stream()
                .map(naturalytelse ->
                        new SendInntektsmeldingDto.BortfaltNaturalytelseRequestDto(naturalytelse.fom(), null, mapNaturalytelseType(naturalytelse.natyralYtelseType()), naturalytelse.beloepPrMnd()))
                .toList();
    }

    private static NaturalytelsetypeDto mapNaturalytelseType(Inntektsmelding.NaturalytelseType naturalytelseType) {
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

    private static YtelseType mapYtelseType(Inntektsmelding.YtelseType ytelseType) {
        return switch (ytelseType) {
            case FORELDREPENGER -> YtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> YtelseType.SVANGERSKAPSPENGER;
        };
    }
}
