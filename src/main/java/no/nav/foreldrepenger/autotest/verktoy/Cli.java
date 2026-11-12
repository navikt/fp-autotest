package no.nav.foreldrepenger.autotest.verktoy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import no.nav.vedtak.mapper.json.DefaultJsonMapper;

public final class Cli {

    private static final int STANDARD_VENT_SEKUNDER = 60;
    private static final List<String> STEG = List.of("miljo_status", "lag_familie", "familie", "send_soknad", "vent_pa_soknad",
            "send_inntektsmelding", "folg_opp", "status", "vent_til_aksjonspunkt", "vent_pa_behandling");
    private static final String HJELP = """
            Bruk: verktoy/testsak.sh <steg> [JSON-objekt]
            Uten JSON-objekt leses parametrene fra stdin.
            Steg:
            miljo_status: Sjekk status for lokale foreldrepenger-tjenester. vent_sekunder (heltall; standard 0): vent til alle er oppe, inntil så lenge.
            lag_familie: Registrer familie i VTP og returner familie_id. hendelse (tekst: fodsel, termin, adopsjon; standard fodsel), dato (ÅÅÅÅ-MM-DD; valgfri, ellers fodt_dager_siden (standard 30), termin_om_dager (standard 30) eller omsorgsovertakelse_dager_siden (standard 14)), mor_arbeid og far_arbeid (tekst: ingen, under_6g, over_6g, to_arbeidsforhold, frilans, sn, arbeid_og_frilans, arbeid_og_sn, stillingsprosent_0 (arbeidsforhold med stillingsprosent 0), sykepenger (bare sykepenger, ikke arbeid); standard ingen), med_far (boolsk; standard true), relasjon (tekst: gift, samboer, ingen; standard gift), barn_i_pdl (heltall; standard 1 ved fødsel, ellers 0), ytelse (tekst: FP, ES, SVP; standard FP; blir standard for de andre stegene og velger søknadsapp). under_6g er arbeid i 3 år med 480 000 kr/år; FP- og SVP-søkere trenger normalt arbeid for opptjening. Returnerer også fnr og lenke til søknadsappen.
            familie: Vis fnr for mor og far, hendelsesdato og lenke til søknadsappen. familie_id (tekst; påkrevd). Agentens verktøyutskrift maskerer fnr, så brukeren kjører dette selv med kommandoen lag_familie skriver ut.
            send_soknad: Send søknad for en registrert familie. familie_id (tekst; påkrevd), sokjar (tekst: mor, far; standard mor), ytelse (tekst: FP, ES, SVP; standard fra lag_familie), barn_i_soknad (heltall; standard 1), annen_forelder (tekst: rett, ikke_rett, aleneomsorg, ukjent; standard rett; bare FP). Søknadsmalen følger familiens hendelse. Har søkeren frilans eller SN (mor_arbeid/far_arbeid), kommer det med i søknaden. SN oppgir snittinntekten fra skatteopplysningene, eller med varig_endring (boolsk; standard false) varig endret næring og 90 % høyere inntekt. SVP: bare mor ved termin, én tilrettelegging per arbeidsgiver og for næringen, tilrettelegging (tekst: ingen, delvis, hel; standard ingen), tilrettelegging_prosent (heltall; standard 50). perioder (liste; bare FP; valgfri, ellers søknadsappens standardplan): felles uttaksplan, der hver periode har forelder (mor, far; standard søker), fra_uke og til_uke (uker fra familiehendelsen, til_uke ikke inkludert; negativ før fødsel) eller fom og tom (ÅÅÅÅ-MM-DD), og enten konto (FORELDREPENGER_FØR_FØDSEL, MØDREKVOTE, FEDREKVOTE, FELLESPERIODE, FORELDREPENGER) eller utsettelse (ARBEID, FERIE, SØKER_SYKDOM, SØKER_INNLAGT, BARN_INNLAGT, HV_ØVELSE, NAV_TILTAK, FRI). Valgfritt per periode: mors_aktivitet (ARBEID, UTDANNING, KVALPROG, INTROPROG, TRENGER_HJELP, INNLAGT, ARBEID_OG_UTDANNING, UFØRE, IKKE_OPPGITT), samtidig_uttak (prosent), gradering (arbeidstidsprosent hos første arbeidsgiver), overforing (SYKDOM_ANNEN_FORELDER, INSTITUSJONSOPPHOLD_ANNEN_FORELDER, ALENEOMSORG, IKKE_RETT_ANNEN_FORELDER). Send samme perioder for begge foreldrene. Med perioder er standard startdato for inntektsmelding søkerens første dag i planen.
            vent_pa_soknad: Vent til manuell søknad fra registrert familie er behandlet. familie_id (tekst; påkrevd), forelder (tekst: mor, far; påkrevd), ytelse (tekst: FP, ES, SVP; standard fra lag_familie), timeout_sekunder (heltall; standard 45). Ved TIMEOUT: spør brukeren om søknaden er sendt, og kjør steget igjen.
            send_inntektsmelding: Vent på forespørsel og send inntektsmelding. saksnummer (tekst; påkrevd), manedsinntekt (heltall; valgfri; gjelder hver arbeidsgiver, også for SVP), startdato (dato ÅÅÅÅ-MM-DD; valgfri). Uten manedsinntekt brukes a-inntekt; over 25 % avvik gir avvik i beregningen (endringsårsak BONUS settes). Har søkeren ikke ordinært arbeidsforhold, sendes ingenting, og steget sier det. Standard startdato (FP) er fødsel eller termin minus 3 uker for mor, ellers familiehendelsen. SVP sender én inntektsmelding per arbeidsgiver; ES har ingen.
            folg_opp: Vent på manuelle søknader og send inntektsmelding (a-inntekt, standard startdato) straks forespørselen kommer. familie_id (tekst; påkrevd), forelder (tekst: mor, far, begge; standard begge når familien har far og ytelsen er FP), ytelse (tekst: FP, ES, SVP; standard fra lag_familie), timeout_minutter (heltall; standard 30). Erstatter vent_pa_soknad og send_inntektsmelding. Forelder uten arbeid får ingen inntektsmelding. Kjøres i egen terminal eller i bakgrunnen, siden den venter lenge.
            status: Vis status og åpne aksjonspunkter for en sak. saksnummer (tekst; påkrevd).
            vent_til_aksjonspunkt: Vent til aksjonspunkt åpnes og vis status. saksnummer (tekst; påkrevd), kode (tekst; påkrevd), timeout_sekunder (heltall; standard 60).
            vent_pa_behandling: Vent til behandlingen er avsluttet eller stopper på et aksjonspunkt saksbehandler må løse. saksnummer (tekst; påkrevd), timeout_sekunder (heltall; standard 60). Svarer OK, STOPP eller TIMEOUT, og viser status.
            """;

    @FunctionalInterface
    private interface Handling {
        String utfør(Map<String, Object> args) throws Exception;
    }

    private Cli() {
    }

    public static void main(String[] args) {
        System.exit(kjor(args));
    }

    private static int kjor(String[] kommandolinje) {
        if (kommandolinje.length == 0 || kommandolinje[0].equals("hjelp") || kommandolinje[0].equals("--help")) {
            System.out.println(HJELP);
            return 0;
        }
        var steg = kommandolinje[0];
        var katalog = Katalog.finn();
        var saker = new SaksVerktoy(katalog);
        var byggeklosser = new ByggeklossVerktoy(Tilstand.standard());
        Map<String, Handling> verktøy = Map.of(
                "miljo_status", StatusVerktoy::kjor,
                "status", saker::status,
                "vent_til_aksjonspunkt", saker::vent,
                "vent_pa_behandling", saker::ventPåBehandling,
                "lag_familie", byggeklosser::lagFamilie,
                "familie", byggeklosser::familie,
                "send_soknad", byggeklosser::sendSoknad,
                "vent_pa_soknad", byggeklosser::ventPaSoknad,
                "send_inntektsmelding", byggeklosser::sendInntektsmelding,
                "folg_opp", byggeklosser::folgOpp);
        var handling = verktøy.get(steg);
        if (handling == null) {
            System.out.println("Ukjent steg: " + steg + ". Gyldige: " + String.join(", ", STEG));
            return 2;
        }
        try {
            var args = lesArgumenter(kommandolinje);
            // Agenter avbryter kommandoer etter et par minutter.
            if (steg.equals("vent_til_aksjonspunkt") || steg.equals("vent_pa_behandling")) {
                args.putIfAbsent("timeout_sekunder", STANDARD_VENT_SEKUNDER);
            }
            System.out.println(handling.utfør(args));
            return 0;
        } catch (Exception e) {
            var melding = e.getMessage();
            if (melding == null && e.getCause() != null) {
                var rotårsak = e.getCause();
                while (rotårsak.getCause() != null) {
                    rotårsak = rotårsak.getCause();
                }
                melding = rotårsak.getMessage();
            }
            System.out.println("FEIL: " + melding);
            return 1;
        }
    }

    private static Map<String, Object> lesArgumenter(String[] kommandolinje) throws IOException {
        var args = new LinkedHashMap<String, Object>();
        if (kommandolinje.length > 1) {
            lesJson(kommandolinje[1], args);
            return args;
        }
        if (System.console() != null) {
            return args;
        }
        var json = new String(System.in.readAllBytes(), StandardCharsets.UTF_8).trim();
        if (json.isEmpty()) {
            return args;
        }
        lesJson(json, args);
        return args;
    }

    private static void lesJson(String json, Map<String, Object> args) throws IOException {
        Map<?, ?> rå = DefaultJsonMapper.getJsonMapper().readValue(json, Map.class);
        rå.forEach((k, v) -> args.put(k.toString(), tilType(v)));
    }

    // nerdr sender tall og bool som tekst når parameteren er en streng; verktøyene venter Number og Boolean.
    private static Object tilType(Object verdi) {
        if (verdi instanceof String s) {
            if (s.matches("-?\\d{1,9}")) {
                return Integer.parseInt(s);
            }
            if (s.equals("true") || s.equals("false")) {
                return Boolean.parseBoolean(s);
            }
        }
        return verdi;
    }
}
