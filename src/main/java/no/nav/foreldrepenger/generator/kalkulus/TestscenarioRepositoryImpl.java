package no.nav.foreldrepenger.generator.kalkulus;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.TestInfo;

import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.kodeverk.FagsakYtelseType;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelBeregnRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.input.KalkulatorInputDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.input.YtelsespesifiktGrunnlagDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.input.foreldrepenger.ForeldrepengerGrunnlag;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.input.svangerskapspenger.SvangerskapspengerGrunnlag;
import no.nav.foreldrepenger.kalkulus.kontrakt.response.beregningsgrunnlag.detaljert.BeregningsgrunnlagGrunnlagDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.typer.AktørIdPersonident;
import no.nav.foreldrepenger.kalkulus.kontrakt.typer.Saksnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

public class TestscenarioRepositoryImpl {
    public static final String KALKULATOR_INPUT_JSON_FIL_NAVN = "kalkulator-input.json";

    public static final String FORVENTET_RESULTAT_JSON_FIL_NAVN = "forventet-resultat.json";
    public static final String FORVENTET_GUI_KOFAKBER_JSON_FIL_NAVN = "forventet-gui-kofakber.json";
    public static final String FORVENTET_GUI_FORESLÅ_JSON_FIL_NAVN = "forventet-gui-foreslå.json";
    public static final String FORVENTET_GUI_FORDEL_JSON_FIL_NAVN = "forventet-gui-fordel.json";

    private final static String INPUT_PREFIKS = "input";
    private final static String RESULTAT_PREFIKS = "resultat";

    private final File rootDir = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("kalkulus")).getFile());

    public EnkelBeregnRequestDto hentScenario(TestInfo testInfo, String inputPrefix) throws FileNotFoundException {
        return lesOgReturnerScenarioFraJsonfil(testInfo, inputPrefix);
    }

    public EnkelBeregnRequestDto hentScenario(TestInfo testInfo, String inputPrefix, EnkelBeregnRequestDto request) throws FileNotFoundException {
        return lesOgReturnerScenarioFraJsonfil(testInfo, inputPrefix, request);
    }

    public BeregningsgrunnlagGrunnlagDto hentForventetResultat(TestInfo testInfo) throws FileNotFoundException {
        return LesOgReturnerForventetResultatFraJsonfil(testInfo, FORVENTET_RESULTAT_JSON_FIL_NAVN, BeregningsgrunnlagGrunnlagDto.class);
    }


    public BeregningsgrunnlagDto hentForventetGUIResultatKofakber(TestInfo testInfo) throws FileNotFoundException {
        return lesOgReturnerForventetGUIResultatFraJsonfil(testInfo, FORVENTET_GUI_KOFAKBER_JSON_FIL_NAVN);
    }

    public BeregningsgrunnlagDto hentForventetGUIResultatForeslå(TestInfo testInfo) throws FileNotFoundException {
        return lesOgReturnerForventetGUIResultatFraJsonfil(testInfo, FORVENTET_GUI_FORESLÅ_JSON_FIL_NAVN);
    }

    public BeregningsgrunnlagDto hentForventetGUIResultatFordel(TestInfo testInfo) throws FileNotFoundException {
        return lesOgReturnerForventetGUIResultatFraJsonfil(testInfo, FORVENTET_GUI_FORDEL_JSON_FIL_NAVN);
    }

    private BeregningsgrunnlagDto lesOgReturnerForventetGUIResultatFraJsonfil(TestInfo testInfo, String filnavn) throws FileNotFoundException {
        var scenarioFiles = hentResultatFil(testInfo);
        if (scenarioFiles == null) {
            return null;
        }

        try {
            var fil = hentFilSomMatcherStreng(scenarioFiles, filnavn);
            if (fil != null) {
                var fileReader = new FileReader(fil);
                return DefaultJsonMapper.getObjectMapper().readValue(fileReader, BeregningsgrunnlagDto.class);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne ikke startBeregningRequest vars for scenario", e);
        }

        return null;
    }

    private <T> T LesOgReturnerForventetResultatFraJsonfil(TestInfo testInfo, String forventetResultatJsonFilNavn, Class<T> klasse) throws FileNotFoundException {
        var resultFiles = hentResultatFil(testInfo);
        if (resultFiles == null) {
            throw new FileNotFoundException("Fant ikke resultat for test [" + getTestName(testInfo) + "]");
        }

        try {
            var fil = hentFilSomMatcherStreng(resultFiles, forventetResultatJsonFilNavn);
            if (fil != null) {
                var fileReader = new FileReader(fil);
                return DefaultJsonMapper.getObjectMapper().readValue(fileReader, klasse);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne lese forventet resultat", e);
        }

        return null;
    }

    private EnkelBeregnRequestDto lesOgReturnerScenarioFraJsonfil(TestInfo testInfo,
                                                               String inputPrefix) throws FileNotFoundException {
        var kalkulatorInputDto = finnKalkulatorInput(testInfo, inputPrefix);
        var aktørId = opprettPersonIVTP();
        return genererNyRequest(kalkulatorInputDto, aktørId.value(), Saksnummer.fra(UUID.randomUUID().toString().substring(0, 19).replace("-", "")));
    }

    private static AktørId opprettPersonIVTP() {
        return FamilieGenerator.ny()
                .forelder(mor().build())
                .build()
                .mor().aktørId();
    }

    private EnkelBeregnRequestDto lesOgReturnerScenarioFraJsonfil(TestInfo testInfo,
                                                               String inputPrefix,
                                                               EnkelBeregnRequestDto originalRequest) throws FileNotFoundException {
        var kalkulatorInputDto = finnKalkulatorInput(testInfo, inputPrefix);
        var saksnummer = originalRequest.saksnummer();
        var aktørId = originalRequest.aktør().getIdent();
        return genererNyRequest(kalkulatorInputDto, aktørId, saksnummer, originalRequest.behandlingUuid());
    }

    private KalkulatorInputDto finnKalkulatorInput(TestInfo testInfo, String inputPrefix) throws FileNotFoundException {
        File scenarioFiles = hentKalkulatorInputFor(testInfo);
        if (scenarioFiles == null) {
            throw new FileNotFoundException("Fant ikke scenario med mappenavn [" + getTestName(testInfo) + "]");
        }
        try {
            String inputNavn = inputPrefix == null ? KALKULATOR_INPUT_JSON_FIL_NAVN : inputPrefix + "-" + KALKULATOR_INPUT_JSON_FIL_NAVN;
            File kalkulatorInputFil = hentFilSomMatcherStreng(scenarioFiles, inputNavn);
            if (kalkulatorInputFil != null) {
                var fileReader = new FileReader(kalkulatorInputFil);
                return DefaultJsonMapper.getObjectMapper().readValue(fileReader, KalkulatorInputDto.class);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne ikke finne kalkulatorinput for scenario", e);
        }
        throw new IllegalArgumentException("Kunne ikke finne kalkulatorinput for scenario");
    }

    private EnkelBeregnRequestDto genererNyRequest(KalkulatorInputDto kalkulatorInputDto,
                                              String aktørId,
                                              Saksnummer saksnummer) {
        return genererNyRequest(kalkulatorInputDto, aktørId, saksnummer, null);
    }

    private EnkelBeregnRequestDto genererNyRequest(KalkulatorInputDto kalkulatorInputDto,
                                              String aktørId,
                                              Saksnummer saksnummer, UUID originalKobling) {
        return new EnkelBeregnRequestDto(
                saksnummer,
                UUID.randomUUID(),
                new AktørIdPersonident(aktørId),
                getYtelseSomSkalBeregnes(kalkulatorInputDto),
                BeregningSteg.FASTSETT_STP_BER,
                kalkulatorInputDto,
                originalKobling
        );
    }


    private FagsakYtelseType getYtelseSomSkalBeregnes(KalkulatorInputDto kalkulatorInputDto) {
        YtelsespesifiktGrunnlagDto ytelsespesifiktGrunnlag = kalkulatorInputDto.getYtelsespesifiktGrunnlag();
        return switch (ytelsespesifiktGrunnlag) {
            case ForeldrepengerGrunnlag ignored -> FagsakYtelseType.FORELDREPENGER;
            case SvangerskapspengerGrunnlag ignored -> FagsakYtelseType.SVANGERSKAPSPENGER;
            case null, default -> throw new IllegalArgumentException("Ytelsetype støttes ikke");
        };
    }


    private File hentFilSomMatcherStreng(File scenarioFiler, String FilNavnPåJsonFil) {
        File[] files = scenarioFiler.listFiles((dir, name) -> name.equalsIgnoreCase(FilNavnPåJsonFil));
        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }

    private File hentResultatFil(TestInfo testInfo) {
        var filesFiltered = Arrays.stream(Objects.requireNonNull(rootDir.listFiles((dir, name) -> name.equalsIgnoreCase(getTestName(testInfo)))))
                .map(file -> file.listFiles((dir, name) -> name.equalsIgnoreCase(RESULTAT_PREFIKS)))
                .findFirst();
        if (filesFiltered.isPresent() && filesFiltered.get().length > 0) {
            return filesFiltered.get()[0];
        }
        return null;
    }

    private File hentKalkulatorInputFor(TestInfo testInfo) {
        var filesFiltered = Arrays.stream(Objects.requireNonNull(rootDir.listFiles((dir, name) -> name.equalsIgnoreCase(getTestName(testInfo)))))
                .map(file -> file.listFiles((dir, name) -> name.equalsIgnoreCase(INPUT_PREFIKS)))
                .findFirst();
        if (filesFiltered.isPresent() && filesFiltered.get().length > 1) {
            throw new IllegalStateException("Det er mer enn ett scenario med nummer: " + getTestName(testInfo));
        }

        if (filesFiltered.isPresent() && filesFiltered.get().length > 0) {
            return filesFiltered.get()[0];
        }
        return null;
    }

    private String getTestName(TestInfo testInfo) {
        return testInfo.getTestMethod().orElseThrow(() -> new IllegalArgumentException("Forventer testmetode")).getName();
    }
}
