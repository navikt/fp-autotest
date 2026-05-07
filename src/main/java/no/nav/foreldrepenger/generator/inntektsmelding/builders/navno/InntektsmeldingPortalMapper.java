package no.nav.foreldrepenger.generator.inntektsmelding.builders.navno;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.InntektsmeldingKlient;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.api.InntektsmeldingRequest;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.AktørIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.ArbeidsgiverDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.NaturalytelsetypeDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.SendInntektsmeldingDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.YtelseType;
import no.nav.foreldrepenger.autotest.util.CollectionUtils;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
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

    public static InntektsmeldingRequest mapForApi(Inntektsmelding inntektsmelding, InntektsmeldingKlient.ForespørselDto forespørsel) {
        return new InntektsmeldingRequest(forespørsel.forespoerselId(),
                inntektsmelding.arbeidstakerFnr(),
                forespørsel.startdato(),
                mapYtelseType(inntektsmelding.ytelseType()),
                new InntektsmeldingRequest.InntektInfo(inntektsmelding.arbeidsforhold().beregnetInntekt(), mapEndringsårsakerTilApi(inntektsmelding)),
                mapRefusjonTilApi(inntektsmelding),
                mapBortfalteNaturalytelserTilApi(inntektsmelding),
                new InntektsmeldingRequest.Kontaktinformasjon(inntektsmelding.arbeidsgiver().navn(),
                inntektsmelding.arbeidsgiver().kontaktnummer()),
                new InntektsmeldingRequest.Avsender("Autotest-LPS", "1.0.0"));
    }

    private static List<InntektsmeldingRequest.InntektInfo.Endringsårsak> mapEndringsårsakerTilApi(Inntektsmelding inntektsmelding) {
        if (inntektsmelding.endringer() == null) {
            return List.of();
        }
        return inntektsmelding.endringer().stream()
                .map(e -> new InntektsmeldingRequest.InntektInfo.Endringsårsak(mapEndringsårsak(e.årsak()), e.fom(), e.tom(), e.bleKjentFom()))
                .toList();
    }

    private static InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType mapEndringsårsak(Inntektsmelding.Endringsårsaker.Endringsårsak årsak) {
        return switch (årsak) {
            case PERMITTERING -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.PERMITTERING;
            case NY_STILLING -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.NY_STILLING;
            case NY_STILLINGSPROSENT -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.NY_STILLINGSPROSENT;
            case SYKEFRAVÆR -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.SYKEFRAVÆR;
            case BONUS -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.BONUS;
            case FERIETREKK_ELLER_UTBETALING_AV_FERIEPENGER -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.FERIETREKK_ELLER_UTBETALING_AV_FERIEPENGER;
            case NYANSATT -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.NYANSATT;
            case MANGELFULL_RAPPORTERING_AORDNING -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.MANGELFULL_RAPPORTERING_AORDNING;
            case INNTEKT_IKKE_RAPPORTERT_ENDA_AORDNING -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.INNTEKT_IKKE_RAPPORTERT_ENDA_AORDNING;
            case TARIFFENDRING -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.TARIFFENDRING;
            case FERIE -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.FERIE;
            case VARIG_LØNNSENDRING -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.VARIG_LØNNSENDRING;
            case PERMISJON -> InntektsmeldingRequest.InntektInfo.Endringsårsak.EndringsårsakType.PERMISJON;
        };
    }

    private static InntektsmeldingRequest.Refusjon mapRefusjonTilApi(Inntektsmelding inntektsmelding) {
        if (inntektsmelding.refusjon() == null) {
            return null;
        }
        List<InntektsmeldingRequest.Refusjon.RefusjonEndring> refusjonsendringer = new ArrayList<>();
        if (inntektsmelding.refusjon().refusjonOpphørsdato() != null){
            refusjonsendringer.add(new InntektsmeldingRequest.Refusjon.RefusjonEndring(BigDecimal.ZERO, inntektsmelding.refusjon().refusjonOpphørsdato()));
        }
        inntektsmelding.refusjon().refusjonEndringList().stream().map(r -> new InntektsmeldingRequest.Refusjon.RefusjonEndring(r.beloepPrMnd(), r.fom())).forEach(refusjonsendringer::add);
        return new InntektsmeldingRequest.Refusjon(inntektsmelding.refusjon().refusjonBeløpPrMnd(), refusjonsendringer);
    }

    private static List<InntektsmeldingRequest.Naturalytelse> mapBortfalteNaturalytelserTilApi(Inntektsmelding inntektsmelding) {
        if (inntektsmelding.opphørAvNaturalytelseList() == null) {
            return Collections.emptyList();
        }
        return inntektsmelding.opphørAvNaturalytelseList().stream()
                .map(naturalytelse -> new InntektsmeldingRequest.Naturalytelse(
                        mapNaturalytelseTypeTilApi(naturalytelse.natyralYtelseType()),
                        naturalytelse.beloepPrMnd(),
                        naturalytelse.fom(),
                        naturalytelse.tom()))
                .toList();
    }

    private static InntektsmeldingRequest.Naturalytelse.Naturalytelsetype mapNaturalytelseTypeTilApi(Inntektsmelding.NaturalytelseType naturalytelseType) {
        return switch (naturalytelseType) {
            case BIL -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.BIL;
            case KOST_DAGER -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.KOST_DAGER;
            case FRI_TRANSPORT -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.FRI_TRANSPORT;
            case ANNET -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.ANNET;
            case BOLIG -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.BOLIG;
            case LOSJI -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.LOSJI;
            case OPSJONER -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.OPSJONER;
            case ELEKTRISK_KOMMUNIKASJON -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.ELEKTRISK_KOMMUNIKASJON;
            case KOST_DOEGN -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.KOST_DOEGN;
            case BEDRIFTSBARNEHAGEPLASS -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.BEDRIFTSBARNEHAGEPLASS;
            case RENTEFORDEL_LÅN -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.RENTEFORDEL_LÅN;
            case TILSKUDD_BARNEHAGEPLASS -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.TILSKUDD_BARNEHAGEPLASS;
            case KOSTBESPARELSE_I_HJEMMET -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.KOSTBESPARELSE_I_HJEMMET;
            case BESØKSREISER_HJEMMET_ANNET -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.BESØKSREISER_HJEMMET_ANNET;
            case SKATTEPLIKTIG_DEL_FORSIKRINGER -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.SKATTEPLIKTIG_DEL_FORSIKRINGER;
            case YRKEBIL_TJENESTLIGBEHOV_KILOMETER -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.YRKEBIL_TJENESTLIGBEHOV_KILOMETER;
            case YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS;
            case AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS;
            case INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING -> InntektsmeldingRequest.Naturalytelse.Naturalytelsetype.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING;
        };
    }
}
