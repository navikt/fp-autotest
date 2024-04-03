package no.nav.foreldrepenger.generator.kalkulus;

import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.FpkalkulusYtelser;
import no.nav.folketrygdloven.kalkulus.beregning.v1.ForeldrepengerGrunnlag;
import no.nav.folketrygdloven.kalkulus.beregning.v1.SvangerskapspengerGrunnlag;
import no.nav.folketrygdloven.kalkulus.beregning.v1.YtelsespesifiktGrunnlagDto;
import no.nav.folketrygdloven.kalkulus.felles.v1.AktørIdPersonident;
import no.nav.folketrygdloven.kalkulus.felles.v1.KalkulatorInputDto;
import no.nav.folketrygdloven.kalkulus.felles.v1.Saksnummer;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.detaljert.BeregningsgrunnlagGrunnlagDto;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.typer.AktørId;

public class TestscenarioRepositoryImpl {
    public static final String KALKULATOR_INPUT_JSON_FIL_NAVN = "kalkulator-input.json";

    public static final String FORVENTET_RESULTAT_JSON_FIL_NAVN = "forventet-resultat.json";
    public static final String FORVENTET_GUI_KOFAKBER_JSON_FIL_NAVN = "forventet-gui-kofakber.json";
    public static final String FORVENTET_GUI_FORESLÅ_JSON_FIL_NAVN = "forventet-gui-foreslå.json";
    public static final String FORVENTET_GUI_FORDEL_JSON_FIL_NAVN = "forventet-gui-fordel.json";


    private final File rootDir = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("scenarios")).getFile());
    private final File rootDirResultat = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("resultat")).getFile());


    public BeregnRequestDto hentScenario(String scenarioId, String inputPrefix) throws FileNotFoundException {
        return lesOgReturnerScenarioFraJsonfil(scenarioId, inputPrefix);
    }

    public BeregnRequestDto hentScenario(String scenarioId, String inputPrefix, BeregnRequestDto request) throws FileNotFoundException {
        return lesOgReturnerScenarioFraJsonfil(scenarioId, inputPrefix, request);
    }

    public BeregningsgrunnlagGrunnlagDto hentForventetResultat(String testId) throws FileNotFoundException {
        return LesOgReturnerForventetResultatFraJsonfil(testId, FORVENTET_RESULTAT_JSON_FIL_NAVN, BeregningsgrunnlagGrunnlagDto.class);
    }


    public BeregningsgrunnlagDto hentForventetGUIResultatKofakber(String testId) throws FileNotFoundException {
        return lesOgReturnerForventetGUIResultatFraJsonfil(testId, FORVENTET_GUI_KOFAKBER_JSON_FIL_NAVN);
    }

    public BeregningsgrunnlagDto hentForventetGUIResultatForeslå(String testId) throws FileNotFoundException {
        return lesOgReturnerForventetGUIResultatFraJsonfil(testId, FORVENTET_GUI_FORESLÅ_JSON_FIL_NAVN);
    }

    public BeregningsgrunnlagDto hentForventetGUIResultatFordel(String testId) throws FileNotFoundException {
        return lesOgReturnerForventetGUIResultatFraJsonfil(testId, FORVENTET_GUI_FORDEL_JSON_FIL_NAVN);
    }

    private BeregningsgrunnlagDto lesOgReturnerForventetGUIResultatFraJsonfil(String scenarioId, String filnavn) throws FileNotFoundException {
        var scenarioFiles = hentResultatFil(scenarioId);
        if (scenarioFiles == null) {
            return null;
        }

        try {
            var fil = hentFilSomMatcherStreng(scenarioFiles, filnavn);
            if (fil != null) {
                var fileReader = new FileReader(fil);
                return MAPPER.readValue(fileReader, BeregningsgrunnlagDto.class);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne ikke startBeregningRequest vars for scenario", e);
        }

        return null;
    }

    private <T> T LesOgReturnerForventetResultatFraJsonfil(String testId, String forventetResultatJsonFilNavn, Class<T> klasse) throws FileNotFoundException {
        var resultFiles = hentResultatFil(testId);
        if (resultFiles == null) {
            throw new FileNotFoundException("Fant ikke resultat for test [" + testId + "]");
        }

        try {
            var fil = hentFilSomMatcherStreng(resultFiles, forventetResultatJsonFilNavn);
            if (fil != null) {
                var fileReader = new FileReader(fil);
                return MAPPER.readValue(fileReader, klasse);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne lese forventet resultat", e);
        }

        return null;
    }

    private BeregnRequestDto lesOgReturnerScenarioFraJsonfil(String scenarioId,
                                                               String inputPrefix) throws FileNotFoundException {
        var kalkulatorInputDto = finnKalkulatorInpu(scenarioId, inputPrefix);
        return genererNyRequest(kalkulatorInputDto,
                AktørId.dummy().getAktørId(), Saksnummer.fra(UUID.randomUUID().toString().substring(0, 19).replace("-", "")));
    }

    private BeregnRequestDto lesOgReturnerScenarioFraJsonfil(String scenarioId,
                                                               String inputPrefix,
                                                               BeregnRequestDto originalRequest) throws FileNotFoundException {
        var kalkulatorInputDto = finnKalkulatorInpu(scenarioId, inputPrefix);
        var saksnummer = originalRequest.saksnummer();
        var aktørId = originalRequest.aktør().getIdent();
        var request = genererNyRequest(kalkulatorInputDto, aktørId, saksnummer);
        return request;
    }

    private KalkulatorInputDto finnKalkulatorInpu(String scenarioId, String inputPrefix) throws FileNotFoundException {
        File scenarioFiles = hentScenarioFileneSomStarterMed(scenarioId);
        if (scenarioFiles == null) {
            throw new FileNotFoundException("Fant ikke scenario med scenario nummer [" + scenarioId + "]");
        }

        try {
            String inputNavn = inputPrefix == null ? KALKULATOR_INPUT_JSON_FIL_NAVN : inputPrefix + "-" + KALKULATOR_INPUT_JSON_FIL_NAVN;
            File kalkulatorInputFil = hentFilSomMatcherStreng(scenarioFiles, inputNavn);
            if (kalkulatorInputFil != null) {
                var fileReader = new FileReader(kalkulatorInputFil);
                return MAPPER.readValue(fileReader, KalkulatorInputDto.class);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne ikke finne kalkulatorinput for scenario", e);
        }
        throw new IllegalArgumentException("Kunne ikke finne kalkulatorinput for scenario");
    }

    private BeregnRequestDto genererNyRequest(KalkulatorInputDto kalkulatorInputDto,
                                              String aktørId,
                                              Saksnummer saksnummer) {
        return new BeregnRequestDto(
                saksnummer,
                UUID.randomUUID(),
                new AktørIdPersonident(aktørId),
                getYtelseSomSkalBeregnes(kalkulatorInputDto),
                BeregningSteg.FASTSETT_STP_BER,
                kalkulatorInputDto
        );
    }


    private FpkalkulusYtelser getYtelseSomSkalBeregnes(KalkulatorInputDto kalkulatorInputDto) {
        YtelsespesifiktGrunnlagDto ytelsespesifiktGrunnlag = kalkulatorInputDto.getYtelsespesifiktGrunnlag();
        return switch (ytelsespesifiktGrunnlag) {
            case ForeldrepengerGrunnlag ignored -> FpkalkulusYtelser.FORELDREPENGER;
            case SvangerskapspengerGrunnlag ignored -> FpkalkulusYtelser.SVANGERSKAPSPENGER;
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

    private File hentResultatFil(String testId) {
        File[] filesFiltered = rootDirResultat.listFiles((dir, name) -> name.equals(testId));
        if (filesFiltered != null && filesFiltered.length > 0) {
            return filesFiltered[0];
        }
        return null;
    }

    private File hentScenarioFileneSomStarterMed(String scenarioNummer) {
        File[] filesFiltered = rootDir.listFiles((dir, name) -> name.startsWith(scenarioNummer));

        if (filesFiltered != null && filesFiltered.length > 1) {
            throw new IllegalStateException("Det er mer enn ett scenario med nummer: " + scenarioNummer);
        }

        if (filesFiltered != null && filesFiltered.length > 0) {
            return filesFiltered[0];
        }
        return null;
    }
}
