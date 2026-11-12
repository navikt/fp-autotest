package no.nav.foreldrepenger.autotest.verktoy;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingFpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektGenerator;
import no.nav.foreldrepenger.generator.familie.generator.PersonBuilder;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.ArbeidsforholdMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadSvangerskapspengerMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttakMaler;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.builder.BarnBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.builder.SøknadBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.builder.TilretteleggingBehovBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.opptjening.NæringDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.Arbeidsforholdstype;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.FamilierelasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.YtelseDto;

final class ByggeklossVerktoy {

    private static final Map<String, String> SØKNADSAPP = Map.of(
            "FP", "http://localhost:9101/foreldrepenger/soknad",
            "SVP", "http://localhost:9102/svangerskapspenger/soknad",
            "ES", "http://localhost:9103/engangsstonad/soknad");
    private static final List<String> HENDELSER = List.of("fodsel", "termin", "adopsjon");
    // InntektGenerator.selvstendigNæringsdrivende registrerer næringen i VTP med dette orgnummeret.
    private static final String VTP_NÆRING_ORGNR = "999999999";

    private final Tilstand tilstand;

    ByggeklossVerktoy(Tilstand tilstand) {
        this.tilstand = tilstand;
    }

    private Tilstand.LagretFamilie familieIVtp(String id) {
        var lagret = tilstand.familie(id);
        var mor = lagret.familie().mor().fødselsnummer().value();
        if (!new TestscenarioKlient().hentAlleIdenterFraAlleScenarier().contains(mor)) {
            tilstand.slettFamilie(id);
            throw new IllegalArgumentException("Familien " + id + " finnes ikke lenger i VTP. Er miljøet startet på nytt? "
                    + "Den er slettet fra tilstanden. Lag en ny med lag_familie.");
        }
        return lagret;
    }

    String lagFamilie(Map<String, Object> args) throws Exception {
            var hendelse = tekst(args, "hendelse", "fodsel");
            if (!HENDELSER.contains(hendelse)) {
                throw new IllegalArgumentException("Ukjent hendelse: " + hendelse + ". Gyldige: " + String.join(", ", HENDELSER));
            }
            var dato = hendelsesdato(args, hendelse);
            var antallBarn = heltall(args, "barn_i_pdl", hendelse.equals("fodsel") ? 1 : 0);
            var medFar = !(args != null && Boolean.FALSE.equals(args.get("med_far")));
            var relasjon = tekst(args, "relasjon", "gift");
            var ytelse = ytelse(args, "FP");

            var generator = FamilieGenerator.ny().forelder(medArbeid(mor(), tekst(args, "mor_arbeid", "ingen")).build());
            if (medFar) {
                generator.forelder(medArbeid(far(), tekst(args, "far_arbeid", "ingen")).build());
                switch (relasjon) {
                    case "gift" -> generator.relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE);
                    case "samboer" -> generator.relasjonForeldre(FamilierelasjonDto.Relasjon.SAMBOER);
                    case "ingen" -> { }
                    default -> throw new IllegalArgumentException("Ukjent relasjon: " + relasjon);
                }
            }
            for (int i = 0; i < antallBarn; i++) {
                generator.barn(hendelse.equals("fodsel") ? dato : LocalDate.now().minusYears(5));
            }
            var familie = generator.build();
            var id = tilstand.leggTilFamilie(familie, dato, hendelse, ytelse);
            var linjer = new ArrayList<String>();
            linjer.add("familie_id: " + id);
            linjer.add("Mor" + (medFar ? " og far" + (relasjon.equals("ingen") ? "" : " (" + relasjon + ")") : "")
                    + ", " + antallBarn + " barn i PDL, " + hendelsestekst(hendelse) + " " + dato + ".");
            linjer.add("Mor fnr: " + familie.mor().fødselsnummer().value());
            if (medFar) {
                linjer.add("Far fnr: " + familie.far().fødselsnummer().value());
            }
            linjer.add("Søk manuelt om " + ytelse + ": " + SØKNADSAPP.get(ytelse) + " (logg inn med fnr). folg_opp venter på søknaden og sender inntektsmelding.");
            linjer.add("Følg opp automatisk (i egen terminal): " + skript() + " folg_opp '{\"familie_id\":\"" + id + "\"}'");
            linjer.add("Vis fnr (brukeren limer inn i Copilot CLI): " + visFnrKommando(id));
            return String.join("\n", linjer);
    }

    String familie(Map<String, Object> args) throws Exception {
            var lagret = familieIVtp(påkrevd(args, "familie_id"));
            var familie = lagret.familie();
            var linjer = new ArrayList<String>();
            linjer.add("Mor fnr: " + familie.mor().fødselsnummer().value());
            if (harFar(familie)) {
                linjer.add("Far fnr: " + familie.far().fødselsnummer().value());
            }
            linjer.add(storForbokstav(hendelsestekst(lagret.hendelseType())) + ": " + lagret.fodselsdato());
            linjer.add("Søknadsapp (" + lagret.ytelseType() + "): " + SØKNADSAPP.get(lagret.ytelseType()));
            return String.join("\n", linjer);
    }

    static String visFnrKommando(String familieId) {
        return "!" + skript() + " familie '{\"familie_id\":\"" + familieId + "\"}'";
    }

    private static String skript() {
        return Repo.rot().resolve("verktoy").resolve("testsak.sh").toString();
    }

    private static boolean harFar(Familie familie) {
        try {
            familie.far();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    String ventPaSoknad(Map<String, Object> args) throws Exception {
            var familieId = påkrevd(args, "familie_id");
            var lagret = familieIVtp(familieId);
            var forelder = påkrevd(args, "forelder");
            var ytelse = ytelse(args, lagret.ytelseType());
            Søker søker = forelder.equals("mor") ? lagret.familie().mor() : lagret.familie().far();
            var timeout = heltall(args, "timeout_sekunder", 45);
            var slutt = System.currentTimeMillis() + timeout * 1000L;
            while (System.currentTimeMillis() < slutt) {
                var saksnummer = finnSak(søker, ytelse);
                if (saksnummer.isPresent()) {
                    var startdato = registrerSak(saksnummer.get(), familieId, forelder, ytelse, lagret);
                    return "Saksnummer: " + saksnummer.get().value() + "\nSøknaden fra " + forelder + " er mottatt."
                            + (startdato == null ? "" : " Standard startdato for inntektsmelding: " + startdato + ".");
                }
                Thread.sleep(3000);
            }
            return "TIMEOUT: ingen sak for " + forelder + " etter " + timeout + " s. Er søknaden sendt?";
    }

    String folgOpp(Map<String, Object> args) throws Exception {
            var familieId = påkrevd(args, "familie_id");
            var lagret = familieIVtp(familieId);
            var familie = lagret.familie();
            var ytelse = ytelse(args, lagret.ytelseType());
            var standardForelder = harFar(familie) && ytelse.equals("FP") ? "begge" : "mor";
            var valg = tekst(args, "forelder", standardForelder);
            var gjenstår = new ArrayList<>(switch (valg) {
                case "begge" -> List.of("mor", "far");
                case "mor", "far" -> List.of(valg);
                default -> throw new IllegalArgumentException("Ukjent forelder: " + valg);
            });
            var timeoutMinutter = heltall(args, "timeout_minutter", 30);
            var slutt = System.currentTimeMillis() + timeoutMinutter * 60_000L;
            var oppsummering = new ArrayList<String>();
            System.out.println("Venter på " + ytelse + "-søknad fra " + String.join(" og ", gjenstår) + " (inntil " + timeoutMinutter + " min).");
            while (!gjenstår.isEmpty() && System.currentTimeMillis() < slutt) {
                for (var forelder : List.copyOf(gjenstår)) {
                    Søker søker = forelder.equals("mor") ? familie.mor() : familie.far();
                    var saksnummer = finnSak(søker, ytelse);
                    if (saksnummer.isEmpty()) {
                        continue;
                    }
                    gjenstår.remove(forelder);
                    var startdato = registrerSak(saksnummer.get(), familieId, forelder, ytelse, lagret);
                    System.out.println(forelder + ": søknad mottatt, saksnummer " + saksnummer.get().value() + ".");
                    String resultat;
                    if (ytelse.equals("ES")) {
                        resultat = "engangsstønad, ingen inntektsmelding";
                    } else if (søker.arbeidsgivere().toList().isEmpty()) {
                        resultat = "ingen arbeidsgiver, inntektsmelding ikke sendt";
                    } else {
                        System.out.println(forelder + ": venter på forespørsel om inntektsmelding.");
                        resultat = sendIm(saksnummer.get(), søker, ytelse, startdato, null);
                    }
                    System.out.println(forelder + ": " + resultat + ".");
                    oppsummering.add(forelder + ": saksnummer " + saksnummer.get().value() + ", " + resultat + ".");
                }
                if (!gjenstår.isEmpty()) {
                    Thread.sleep(3000);
                }
            }
            if (!gjenstår.isEmpty()) {
                oppsummering.add("TIMEOUT: ingen søknad fra " + String.join(" og ", gjenstår) + " etter " + timeoutMinutter + " min.");
            }
            oppsummering.add("Bruk status eller vent_til_aksjonspunkt for å se hvor sakene stopper.");
            return String.join("\n", oppsummering);
    }

    String sendSoknad(Map<String, Object> args) throws Exception {
            var familieId = påkrevd(args, "familie_id");
            var lagret = familieIVtp(familieId);
            var familie = lagret.familie();
            var dato = lagret.fodselsdato();
            var hendelse = lagret.hendelseType();
            var ytelse = ytelse(args, lagret.ytelseType());
            var sokjar = tekst(args, "sokjar", "mor");
            var erMor = sokjar.equals("mor");
            Søker søker = erMor ? familie.mor() : familie.far();
            var antallBarn = heltall(args, "barn_i_soknad", 1);
            var plan = args != null && args.get("perioder") != null
                    ? Uttaksplan.fra(args.get("perioder"), familie, dato, sokjar)
                    : null;
            if (plan != null && !ytelse.equals("FP")) {
                throw new IllegalArgumentException("perioder gjelder bare FP.");
            }

            SøknadBuilder<?> søknad = switch (ytelse) {
                case "ES" -> switch (hendelse) {
                    case "fodsel" -> SøknadEngangsstønadMaler.lagEngangstønadFødsel(dato)
                            .medBarn(BarnBuilder.fødsel(antallBarn, dato).build());
                    case "termin" -> SøknadEngangsstønadMaler.lagEngangstønadTermin(dato)
                            .medBarn(BarnBuilder.termin(antallBarn, dato).build());
                    default -> SøknadEngangsstønadMaler.lagEngangstønadAdopsjon(dato, false);
                };
                case "SVP" -> svpSøknad(args, søker, erMor, hendelse, dato);
                default -> fpSøknad(args, familie, søker, erMor, hendelse, dato, antallBarn, plan);
            };
            var saksnummer = søker.søk(søknad);
            var startdato = plan == null ? startdato(sokjar, ytelse, hendelse, dato) : plan.førsteDag(sokjar).orElse(null);
            tilstand.leggTilSak(saksnummer.value(), new Tilstand.LagretSak(familieId, sokjar, startdato, ytelse));
            return "Saksnummer: " + saksnummer.value();
    }

    private static ForeldrepengerBuilder fpSøknad(Map<String, Object> args, Familie familie, Søker søker, boolean erMor, String hendelse,
                                                  LocalDate dato, int antallBarn, Uttaksplan plan) {
        var rolle = erMor ? BrukerRolle.MOR : BrukerRolle.FAR;
        var fp = switch (hendelse) {
            case "fodsel" -> SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(dato, rolle)
                    .medBarn(BarnBuilder.fødsel(antallBarn, dato).build());
            case "termin" -> SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(dato, rolle)
                    .medBarn(BarnBuilder.termin(antallBarn, dato).build());
            default -> SøknadForeldrepengerMaler.lagSøknadForeldrepengerAdopsjon(dato, rolle, false);
        };
        var annenForelder = tekst(args, "annen_forelder", "rett");
        if (!annenForelder.equals("ukjent")) {
            Søker annen = erMor ? familie.far() : familie.mor();
            switch (annenForelder) {
                case "rett" -> fp.medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(annen));
                case "ikke_rett" -> fp.medAnnenForelder(AnnenforelderMaler.norskIkkeRett(annen));
                case "aleneomsorg" -> fp.medAnnenForelder(AnnenforelderMaler.norskAleneomsorg(annen))
                        .medPerioder(erMor
                                ? UttakMaler.fordelingMorAleneomsorgHappyCase(dato)
                                : UttakMaler.fordelingFarAleneomsorg(dato));
                default -> throw new IllegalArgumentException("Ukjent annen_forelder: " + annenForelder);
            }
        }
        if (plan != null) {
            fp.medPerioder(plan.sett(erMor ? "mor" : "far"));
        }
        næring(args, søker).ifPresent(fp::medSelvstendigNæringsdrivendeInformasjon);
        if (erFrilanser(søker)) {
            fp.medFrilansInformasjon(OpptjeningMaler.frilansOpptjening());
        }
        return fp;
    }

    private static SøknadBuilder<?> svpSøknad(Map<String, Object> args, Søker søker, boolean erMor, String hendelse, LocalDate termin) {
        if (!erMor) {
            throw new IllegalArgumentException("Bare mor kan søke svangerskapspenger.");
        }
        if (!hendelse.equals("termin")) {
            throw new IllegalArgumentException("SVP krever en familie laget med hendelse termin.");
        }
        var type = tekst(args, "tilrettelegging", "ingen");
        var prosent = heltall(args, "tilrettelegging_prosent", 50);
        var fom = LocalDate.now();
        var behov = søker.arbeidsforholdene().stream()
                .map(a -> a.arbeidsgiverIdentifikasjon())
                .distinct()
                .filter(id -> id.length() == 9)
                .map(orgnr -> {
                    var builder = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet(new Orgnummer(orgnr)), fom);
                    return switch (type) {
                        case "ingen" -> builder.ingen(fom).build();
                        case "hel" -> builder.hel(fom).build();
                        case "delvis" -> builder.delvis(fom, (double) prosent).build();
                        default -> throw new IllegalArgumentException("Ukjent tilrettelegging: " + type);
                    };
                })
                .collect(Collectors.toCollection(ArrayList::new));
        var næring = næring(args, søker);
        if (næring.isPresent()) {
            var builder = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.selvstendigNæringsdrivende(), fom);
            behov.add(switch (type) {
                case "ingen" -> builder.ingen(fom).build();
                case "hel" -> builder.hel(fom).build();
                default -> builder.delvis(fom, (double) prosent).build();
            });
        }
        if (behov.isEmpty()) {
            throw new IllegalArgumentException("SVP krever at mor har arbeidsforhold hos en virksomhet eller er selvstendig næringsdrivende (mor_arbeid).");
        }
        var svp = SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad(termin, behov);
        næring.ifPresent(svp::medSelvstendigNæringsdrivendeInformasjon);
        return svp;
    }

    private static Optional<NæringDto> næring(Map<String, Object> args, Søker søker) {
        var inntekt = søker.næringsinntekt();
        if (inntekt <= 0) {
            return Optional.empty();
        }
        var varigEndring = args != null && Boolean.TRUE.equals(args.get("varig_endring"));
        // Over 25 % avvik fra snittet i Sigrun gir 5039, som i VerdikjedeForeldrepenger.morSelvstendigNæringsdrivendeTest.
        var oppgitt = varigEndring ? inntekt * 1.9 : inntekt;
        return Optional.of(OpptjeningMaler.egenNaeringOpptjening(VTP_NÆRING_ORGNR, søker.næringStartdato(), LocalDate.now(),
                false, oppgitt, varigEndring));
    }

    private static boolean erFrilanser(Søker søker) {
        return søker.arbeidsforholdene().stream()
                .anyMatch(a -> Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER.equals(a.arbeidsforholdstype()));
    }

    String sendInntektsmelding(Map<String, Object> args) throws Exception {
            var saksnummer = new Saksnummer(påkrevd(args, "saksnummer"));
            var sak = tilstand.sak(saksnummer.value()).orElseThrow(() -> new IllegalArgumentException(
                    "Ukjent sak: " + saksnummer.value() + ". Saken må være laget med send_soknad eller funnet med vent_pa_soknad."));
            if (sak.ytelseType().equals("ES")) {
                throw new IllegalArgumentException("Engangsstønad har ingen inntektsmelding.");
            }
            var startdato = args != null && args.get("startdato") != null
                    ? LocalDate.parse(args.get("startdato").toString())
                    : sak.startdato();
            var søker = sak.søker(familieIVtp(sak.familieId()));
            if (søker.arbeidsgivere().toList().isEmpty()) {
                return "Ingen inntektsmelding: " + sak.forelder()
                        + " har ikke ordinært arbeidsforhold (bare frilans, næring, ytelse eller ingenting).\nBruk vent_til_aksjonspunkt eller status for å se hvor saken stopper.";
            }
            var beløp = args != null && args.get("manedsinntekt") instanceof Number n ? n.intValue() : null;
            var resultat = sendIm(saksnummer, søker, sak.ytelseType(), startdato, beløp);
            return storForbokstav(resultat) + ".\nBruk vent_til_aksjonspunkt eller status for å se hvor saken stopper.";
    }

    private static Optional<Saksnummer> finnSak(Søker søker, String ytelse) {
        var fagsakKlient = new FagsakKlient(SaksbehandlerRolle.SAKSBEHANDLER);
        var behandlingKlient = new BehandlingFpsakKlient(SaksbehandlerRolle.SAKSBEHANDLER);
        return fagsakKlient.søk(søker.fødselsnummer()).stream()
                .filter(f -> ytelse.equals(f.fagsakYtelseType()))
                .map(Fagsak::saksnummer)
                .max(Comparator.comparingInt(s -> Integer.parseInt(s.value())))
                .filter(s -> !behandlingKlient.alle(s).isEmpty());
    }

    private LocalDate registrerSak(Saksnummer saksnummer, String familieId, String forelder, String ytelse,
                                   Tilstand.LagretFamilie lagret) {
        var startdato = startdato(forelder, ytelse, lagret.hendelseType(), lagret.fodselsdato());
        tilstand.leggTilSak(saksnummer.value(), new Tilstand.LagretSak(familieId, forelder, startdato, ytelse));
        return startdato;
    }

    private static LocalDate startdato(String forelder, String ytelse, String hendelse, LocalDate dato) {
        if (!ytelse.equals("FP")) {
            return null;
        }
        if (forelder.equals("mor") && !hendelse.equals("adopsjon")) {
            return dato.minusWeeks(3);
        }
        return dato;
    }

    private static String sendIm(Saksnummer saksnummer, Søker søker, String ytelse, LocalDate startdato, Integer månedsinntekt) {
        var arbeidsgivere = søker.arbeidsgivere();
        if (arbeidsgivere.toList().isEmpty()) {
            throw new IllegalArgumentException("Søker har ingen arbeidsgiver i AAREG, så det finnes ingen inntektsmelding å sende.");
        }
        var saksbehandler = new Saksbehandler();
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.FORESPØRSEL_OM_INNTEKTSMELDING);
        var antall = arbeidsgivere.toList().size();
        if (månedsinntekt != null) {
            for (var arbeidsgiver : arbeidsgivere.toList()) {
                var im = ytelse.equals("SVP") ? arbeidsgiver.lagInntektsmeldingSVP() : arbeidsgiver.lagInntektsmeldingFP(startdato);
                arbeidsgiver.sendInntektsmelding(saksnummer, im.medBeregnetInntekt(månedsinntekt)
                        .leggTilEndretInntektÅrsak(Inntektsmelding.Endringsårsaker.Endringsårsak.BONUS));
            }
            return "inntektsmelding for " + ytelse + " sendt fra " + antall + " arbeidsgiver(e) med " + månedsinntekt
                    + " kr/mnd hver og endringsårsak BONUS" + (ytelse.equals("FP") ? ", startdato " + startdato : "");
        }
        if (ytelse.equals("SVP")) {
            arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummer);
            return "inntektsmelding for SVP sendt fra " + antall + " arbeidsgiver(e)";
        }
        arbeidsgivere.sendDefaultInntektsmeldingerFP(saksnummer, startdato);
        return "inntektsmelding sendt fra " + antall + " arbeidsgiver(e) med inntekt fra a-inntekt, startdato " + startdato;
    }

    private static LocalDate hendelsesdato(Map<String, Object> args, String hendelse) {
        if (args != null && args.get("dato") != null) {
            return LocalDate.parse(args.get("dato").toString());
        }
        return switch (hendelse) {
            case "fodsel" -> LocalDate.now().minusDays(heltall(args, "fodt_dager_siden", 30));
            case "termin" -> LocalDate.now().plusDays(heltall(args, "termin_om_dager", 30));
            default -> LocalDate.now().minusDays(heltall(args, "omsorgsovertakelse_dager_siden", 14));
        };
    }

    private static String hendelsestekst(String hendelse) {
        return switch (hendelse) {
            case "termin" -> "termindato";
            case "adopsjon" -> "omsorgsovertakelse";
            default -> "fødselsdato";
        };
    }

    private static String storForbokstav(String tekst) {
        return tekst.substring(0, 1).toUpperCase() + tekst.substring(1);
    }

    private static PersonBuilder medArbeid(PersonBuilder person, String arbeid) {
        var frilansFom = LocalDate.now().minusYears(2);
        return switch (arbeid) {
            case "ingen" -> person;
            case "under_6g" -> person.inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build());
            case "over_6g" -> person.inntekt(InntektGenerator.ny().arbeidMedOpptjeningOver6G().build());
            case "to_arbeidsforhold" -> person.inntekt(InntektGenerator.ny()
                    .arbeidsforhold(LocalDate.now().minusYears(4), 480_000)
                    .arbeidsforhold(50, LocalDate.now().minusYears(2), 240_000)
                    .build());
            case "frilans" -> person.inntekt(InntektGenerator.ny().frilans(frilansFom, 360_000).build());
            case "sn" -> person.inntekt(InntektGenerator.ny().selvstendigNæringsdrivende(500_000).build());
            case "arbeid_og_frilans" -> person.inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().frilans(frilansFom, 120_000).build());
            case "arbeid_og_sn" -> person.inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().selvstendigNæringsdrivende(300_000).build());
            case "stillingsprosent_0" -> person.inntekt(InntektGenerator.ny().arbeidsforhold(0, LocalDate.now().minusMonths(12), 360_000).build());
            case "sykepenger" -> person.ytelse(YtelseDto.YtelseType.SYKEPENGER, LocalDate.now().minusMonths(9), LocalDate.now(), 462, 100);
            default -> throw new IllegalArgumentException("Ukjent arbeid: " + arbeid
                    + ". Gyldige: ingen, under_6g, over_6g, to_arbeidsforhold, frilans, sn, arbeid_og_frilans, arbeid_og_sn, stillingsprosent_0, sykepenger");
        };
    }

    private static String ytelse(Map<String, Object> args, String standard) {
        var ytelse = tekst(args, "ytelse", standard);
        if (!List.of("FP", "ES", "SVP").contains(ytelse)) {
            throw new IllegalArgumentException("Ukjent ytelse: " + ytelse + ". Gyldige: FP, ES, SVP");
        }
        return ytelse;
    }

    private static String tekst(Map<String, Object> args, String navn, String standard) {
        var verdi = args == null ? null : args.get(navn);
        return verdi == null ? standard : verdi.toString();
    }

    private static int heltall(Map<String, Object> args, String navn, int standard) {
        return args != null && args.get(navn) instanceof Number n ? n.intValue() : standard;
    }

    private static String påkrevd(Map<String, Object> args, String navn) {
        var verdi = args == null ? null : args.get(navn);
        if (verdi == null || verdi.toString().isBlank()) {
            throw new IllegalArgumentException("Mangler argument: " + navn);
        }
        return verdi.toString();
    }
}
