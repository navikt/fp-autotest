package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.vtp.kontrakter.v2.AaregDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Arbeidsforholdstype;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Arbeidsgiver;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaMeldekort;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaVedtakDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GrunnlagDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.InfotrygdDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.InntektYtelseModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.InntektkomponentDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.OrganisasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PesysDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.SigrunDto;

public class InntektYtelseGenerator {

    private final InntektYtelseModellDto.Builder inntektYtelse = InntektYtelseModellDto.builder();
    private final TestOrganisasjoner testOrganisasjoner = new TestOrganisasjoner();
    private static final int DEFAULT_ÅRSLØNN = 600_000;
    private static final int DEAFULT_STILLINGSPROSENT = 100;

    public static InntektYtelseGenerator ny() {
        return new InntektYtelseGenerator();
    }

    public InntektYtelseGenerator arbeidMedOpptjeningOver6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 900_000);
    }

    public InntektYtelseGenerator arbeidMedOpptjeningUnder6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 480_000);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, tom, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(Arbeidsgiver arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(arbeidsgiver, fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(Arbeidsgiver arbeidsgiver, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, tom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, tom, årslønn, arbeidsavtaler);
    }

    public  InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(stillingsprosent, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public  InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, DEAFULT_STILLINGSPROSENT, fom, tom, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Arbeidsgiver arbeidsgiver, String arbeidsforholdId,
                                                 Integer stillingsprosent,
                                                 LocalDate fom,
                                                 LocalDate tom,
                                                 Integer årslønn,
                                                 ArbeidsavtaleDto... arbeidsavtaler)  {
        var arbeidsforholdDto = ArbeidsforholdDto.builder()
                .arbeidsgiver(arbeidsgiver)
                .arbeidsforholdId(arbeidsforholdId)
                .ansettelsesperiodeFom(fom)
                .ansettelsesperiodeTom(tom)
                .arbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
                .build();
        return arbeidsforhold(arbeidsforholdDto, årslønn);
    }

    public ArbeidsforholdDto.Builder arbeidsforholdB(LocalDate fom) {
        return ArbeidsforholdDto.builder()
                .arbeidsgiver(testOrganisasjoner.tilfeldigOrg())
                .arbeidsforholdId(testOrganisasjoner.arbeidsforholdId())
                .ansettelsesperiodeFom(fom)
                .arbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
                .arbeidsavtaler(List.of(defaultArbeidsavtale(fom, null, DEAFULT_STILLINGSPROSENT)));
    }


    public InntektYtelseGenerator frilans(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(DEAFULT_STILLINGSPROSENT, fom, tom, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(arbeidsgiver, arbeidsforholdId, fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(arbeidsgiver, arbeidsforholdId, DEAFULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        var frilansArbeidsforhold = ArbeidsforholdDto.builder()
                .arbeidsgiver(arbeidsgiver)
                .arbeidsforholdId(arbeidsforholdId)
                .ansettelsesperiodeFom(fom)
                .ansettelsesperiodeTom(tom)
                .arbeidsforholdstype(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER)
                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
                .build();
        return arbeidsforhold(frilansArbeidsforhold, årslønn);
    }

    private ArbeidsavtaleDto defaultArbeidsavtale(LocalDate fom, LocalDate tom, Integer stillingsprosent) {
        return ArbeidsavtaleDto.arbeidsavtale(fom, tom)
                .stillingsprosent(stillingsprosent)
                .build();
    }

    private InntektYtelseGenerator arbeidsforhold(ArbeidsforholdDto arbeidsforhold, Integer årslønn) {
        if (inntektYtelse.aareg() != null) {
            inntektYtelse.aareg().arbeidsforhold().add(arbeidsforhold);
        } else {
            var arbeidsforholdListe = new ArrayList<ArbeidsforholdDto>();
            arbeidsforholdListe.add(arbeidsforhold);
            inntektYtelse.aareg(new AaregDto(arbeidsforholdListe));
        }
        if (årslønn != null) {
            inntektsperiode(arbeidsforhold, årslønn/12);
        }
        return this;
    }

    public InntektYtelseGenerator inntektsperiode(ArbeidsforholdDto fraArbeidsforhold, Integer beløpPerMnd) {
        var inntektsperiode = new InntektsperiodeDto(
                fraArbeidsforhold.ansettelsesperiodeFom(),
                fraArbeidsforhold.ansettelsesperiodeTom() != null ? fraArbeidsforhold.ansettelsesperiodeTom() : LocalDate.now() ,
                beløpPerMnd,
                InntektsperiodeDto.InntektTypeDto.LØNNSINNTEKT,
                InntektsperiodeDto.InntektFordelDto.KONTANTYTELSE,
                fraArbeidsforhold.arbeidsgiver()

        );
        return inntektsperiode(inntektsperiode);
    }

    public InntektYtelseGenerator inntektsperiode(OrganisasjonDto organisasjon, LocalDate fom, LocalDate tom, Integer beløp) {
        var inntektsperiode = new InntektsperiodeDto(
                fom,
                tom,
                beløp,
                InntektsperiodeDto.InntektTypeDto.LØNNSINNTEKT,
                InntektsperiodeDto.InntektFordelDto.KONTANTYTELSE,
                organisasjon

        );
        return inntektsperiode(inntektsperiode);
    }

    public InntektYtelseGenerator inntektsperiode(InntektsperiodeDto inntektsperiode) {
        if (inntektYtelse.inntektskomponent() != null) {
            inntektYtelse.inntektskomponent().inntektsperioder().add(inntektsperiode);
        } else {
            var inntektsperioder = new ArrayList<InntektsperiodeDto>();
            inntektsperioder.add(inntektsperiode);
            var inntektskomponentModell = new InntektkomponentDto(inntektsperioder);
            inntektYtelse.inntektskomponent(inntektskomponentModell);
        }
        return this;
    }


    public InntektYtelseGenerator arena(ArenaSakerDto.YtelseTema tema, LocalDate fom, LocalDate tom, Integer beløp) {
        var dagsats = 1_000;
        var utbetalingsgrad = DEAFULT_STILLINGSPROSENT;
        var sak = new ArenaSakerDto(tema, ArenaSakerDto.SakStatus.AKTIV, List.of(
                new ArenaVedtakDto(fom, tom, ArenaVedtakDto.VedtakStatus.IVERK, dagsats, List.of(
                        new ArenaMeldekort(fom, tom, dagsats, beløp, utbetalingsgrad)
                ))
        ));

        if(inntektYtelse.arena() != null) {
            inntektYtelse.arena().saker().add(sak);
        } else {
            var saker = new ArrayList<ArenaSakerDto>();
            saker.add(sak);
            inntektYtelse.arena(new ArenaDto(saker));
        }
        var inntektsperiode = new InntektsperiodeDto(
                fom,
                tom,
                beløp,
                InntektsperiodeDto.InntektTypeDto.YTELSE_FRA_OFFENTLIGE,
                InntektsperiodeDto.InntektFordelDto.KONTANTYTELSE,
                TestOrganisasjoner.NAV

        );
        return inntektsperiode(inntektsperiode);
    }


    // trex -> infotrygd
    public InntektYtelseGenerator ytelse(GrunnlagDto.Ytelse tema, LocalDate fom, LocalDate tom, GrunnlagDto.Status status, LocalDate fødselsdatoBarn) {
        var ytelsegrunnlag = new GrunnlagDto(tema, fom, tom, status, fødselsdatoBarn, List.of(
                new GrunnlagDto.Vedtak(fom, tom, DEAFULT_STILLINGSPROSENT)));


        if(inntektYtelse.infotrygd() != null) {
            inntektYtelse.infotrygd().ytelser().add(ytelsegrunnlag);
        } else {
            var grunnlagliste = new ArrayList<GrunnlagDto>();
            grunnlagliste.add(ytelsegrunnlag);
            inntektYtelse.infotrygd(new InfotrygdDto(grunnlagliste));
        }
        var inntektsperiode = new InntektsperiodeDto(
                fom,
                tom,
                10_000,
                InntektsperiodeDto.InntektTypeDto.YTELSE_FRA_OFFENTLIGE,
                InntektsperiodeDto.InntektFordelDto.KONTANTYTELSE,
                TestOrganisasjoner.NAV_YTELSE_BETALING

        );
        return inntektsperiode(inntektsperiode);
    }

    public InntektYtelseGenerator selvstendigNæringsdrivende(Integer gjennomsnittligNæringsinntekt) {
        var sigrunDto = new SigrunDto(new ArrayList<>());
        var inntektsår = sigrunDto.inntektår();
        var now = LocalDate.now().minusYears(1);
        for (int i = 0; i < 4; i++) {
            inntektsår.add(new SigrunDto.InntektsårDto(now.getYear(), gjennomsnittligNæringsinntekt));
            now = now.minusYears(1);
        }
        inntektYtelse.sigrun(sigrunDto);
        return this;
    }

    public InntektYtelseGenerator harUføretrygd() {
        inntektYtelse.pesys(new PesysDto(true));
        return this;
    }

    public InntektYtelseModellDto build() {
        return inntektYtelse.build();
    }
}
