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
                forespørsel.foersteUttaksdato(),
                mapYtelseType(inntektsmelding.ytelseType()),
                new InntektsmeldingRequest.Kontaktperson(inntektsmelding.arbeidsgiver().navn(), inntektsmelding.arbeidsgiver().kontaktnummer()),
                inntektsmelding.arbeidsforhold().beregnetInntekt(),
                mapRefusjonTilApi(inntektsmelding),
                mapBortfalteNaturalytelserTilApi(inntektsmelding),
                mapEndringsårsakerTilApi(inntektsmelding),
                new InntektsmeldingRequest.AvsenderSystem("Autotest-LPS", "1.0.0"));
    }

    private static List<InntektsmeldingRequest.Endringsårsaker> mapEndringsårsakerTilApi(Inntektsmelding inntektsmelding) {
        if (inntektsmelding.endringer() == null) {
            return List.of();
        }
        return inntektsmelding.endringer().stream()
                .map(e -> new InntektsmeldingRequest.Endringsårsaker(mapEndringsårsak(e.årsak()), e.fom(), e.tom(), e.bleKjentFom()))
                .toList();
    }

    private static InntektsmeldingRequest.Endringsårsaker.Endringsårsak mapEndringsårsak(Inntektsmelding.Endringsårsaker.Endringsårsak årsak) {
        return switch (årsak) {
            case PERMITTERING -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.PERMITTERING;
            case NY_STILLING -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.NY_STILLING;
            case NY_STILLINGSPROSENT -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.NY_STILLINGSPROSENT;
            case SYKEFRAVÆR -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.SYKEFRAVÆR;
            case BONUS -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.BONUS;
            case FERIETREKK_ELLER_UTBETALING_AV_FERIEPENGER -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.FERIETREKK_ELLER_UTBETALING_AV_FERIEPENGER;
            case NYANSATT -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.NYANSATT;
            case MANGELFULL_RAPPORTERING_AORDNING -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.MANGELFULL_RAPPORTERING_AORDNING;
            case INNTEKT_IKKE_RAPPORTERT_ENDA_AORDNING -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.INNTEKT_IKKE_RAPPORTERT_ENDA_AORDNING;
            case TARIFFENDRING -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.TARIFFENDRING;
            case FERIE -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.FERIE;
            case VARIG_LØNNSENDRING -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.VARIG_LØNNSENDRING;
            case PERMISJON -> InntektsmeldingRequest.Endringsårsaker.Endringsårsak.PERMISJON;
        };
    }

    private static List<InntektsmeldingRequest.Refusjon> mapRefusjonTilApi(Inntektsmelding inntektsmelding) {
        List<InntektsmeldingRequest.Refusjon> refusjonsendringer = new ArrayList<>();
        if (inntektsmelding.refusjon() == null) {
            return refusjonsendringer;
        }
        refusjonsendringer.add(new InntektsmeldingRequest.Refusjon(inntektsmelding.arbeidsforhold().førsteFraværsdag(), inntektsmelding.refusjon().refusjonBeløpPrMnd()));
        if (inntektsmelding.refusjon().refusjonOpphørsdato() != null){
            refusjonsendringer.add(new InntektsmeldingRequest.Refusjon(inntektsmelding.refusjon().refusjonOpphørsdato(), BigDecimal.ZERO));
        }
        inntektsmelding.refusjon().refusjonEndringList().stream().map(r -> new InntektsmeldingRequest.Refusjon(r.fom(), r.beloepPrMnd())).forEach(refusjonsendringer::add);
        return refusjonsendringer;
    }

    private static List<InntektsmeldingRequest.BortfaltNaturalytelse> mapBortfalteNaturalytelserTilApi(Inntektsmelding inntektsmelding) {
        if (inntektsmelding.opphørAvNaturalytelseList() == null) {
            return Collections.emptyList();
        }
        return inntektsmelding.opphørAvNaturalytelseList().stream()
                .map(naturalytelse -> new InntektsmeldingRequest.BortfaltNaturalytelse(
                        naturalytelse.fom(),
                        naturalytelse.tom(),
                        mapNaturalytelseTypeTilApi(naturalytelse.natyralYtelseType()),
                        naturalytelse.beloepPrMnd()))
                .toList();
    }

    private static InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype mapNaturalytelseTypeTilApi(Inntektsmelding.NaturalytelseType naturalytelseType) {
        return switch (naturalytelseType) {
            case BIL -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.BIL;
            case KOST_DAGER -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.KOST_DAGER;
            case FRI_TRANSPORT -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.FRI_TRANSPORT;
            case ANNET -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.ANNET;
            case BOLIG -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.BOLIG;
            case LOSJI -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.LOSJI;
            case OPSJONER -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.OPSJONER;
            case ELEKTRISK_KOMMUNIKASJON -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.ELEKTRISK_KOMMUNIKASJON;
            case KOST_DOEGN -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.KOST_DOEGN;
            case BEDRIFTSBARNEHAGEPLASS -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.BEDRIFTSBARNEHAGEPLASS;
            case RENTEFORDEL_LÅN -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.RENTEFORDEL_LÅN;
            case TILSKUDD_BARNEHAGEPLASS -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.TILSKUDD_BARNEHAGEPLASS;
            case KOSTBESPARELSE_I_HJEMMET -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.KOSTBESPARELSE_I_HJEMMET;
            case BESØKSREISER_HJEMMET_ANNET -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.BESØKSREISER_HJEMMET_ANNET;
            case SKATTEPLIKTIG_DEL_FORSIKRINGER -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.SKATTEPLIKTIG_DEL_FORSIKRINGER;
            case YRKEBIL_TJENESTLIGBEHOV_KILOMETER -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.YRKEBIL_TJENESTLIGBEHOV_KILOMETER;
            case YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS;
            case AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS;
            case INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING -> InntektsmeldingRequest.BortfaltNaturalytelse.Naturalytelsetype.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING;
        };
    }
}
