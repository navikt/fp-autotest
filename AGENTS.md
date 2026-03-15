# fp-autotest — Integration Test Agent Instructions

This file helps Copilot find, recommend, and execute integration tests for Team Foreldrepenger's applications.

## How to Use

When a developer asks about integration tests, use this catalog to:
- **Find tests** by DisplayName, aksjonspunkt codes, or test method name
- **Recommend tests** relevant to code changes in any application
- **Provide run commands** for the recommended tests

## Running Tests

### Run a full suite
```
mvn test -P <suite>
```
Available suites: `fpsak`, `fptilbake`, `fpkalkulus`, `fplos`, `verdikjede`
Sub-suites: `foreldrepenger`, `engangsstonad`, `svangerskapspenger`

### Run a specific test class
```
mvn test -P <suite> -Dtest=<ClassName>
```

### Run a single test method
```
mvn test -P <suite> -Dtest="<ClassName>#<methodName>"
```

## Building and Testing Local Application Changes

When a developer wants to test local changes to an application:

1. **Build the application** (in the application's repo directory):
   ```bash
   mvn clean install -DskipTests
   docker build -t <repo-name> .
   ```

2. **Generate a fresh `.env` file** by running the setup script in fp-autotest:
   ```bash
   cd lokal-utvikling
   ./setup-lokal-utvikling.sh
   ```
   This creates/regenerates `lokal-utvikling/docker-compose-lokal/.env` with the latest remote image references.

3. **Edit `.env`** to point to the locally built image instead of the remote one.
   Change the relevant `*_IMAGE` variable from the GAR reference to the local tag.
   Example for fp-sak:
   ```
   # Change from:
   FPSAK_IMAGE=europe-north1-docker.pkg.dev/.../navikt/fp-sak:latest
   # To:
   FPSAK_IMAGE=fp-sak:latest
   ```

4. **Start all services** via docker-compose (all services are needed regardless of which suite you run):
   ```bash
   cd lokal-utvikling/docker-compose-lokal
   docker compose up --detach
   ```
   Wait for all services to be healthy: `docker compose ps`

5. **Run the tests** (from the fp-autotest root):
   ```bash
   mvn test -P <suite> -Dtest=<TestClass>
   ```

### Application → Image Variable and Suite Mapping

| Repository | Docker build tag | .env variable | Docker Compose service | Recommended test suites |
|------------|-----------------|---------------|----------------------|------------------------|
| fp-sak | fp-sak | FPSAK_IMAGE | fpsak | `fpsak`, `verdikjede` |
| fp-abakus | fp-abakus | FPABAKUS_IMAGE | fpabakus | `fpsak`, `verdikjede` |
| fp-kalkulus | fp-kalkulus | FPKALKULUS_IMAGE | fpkalkulus | `fpkalkulus`, `fpsak`, `verdikjede` |
| fptilbake | fptilbake | FPTILBAKE_IMAGE | fptilbake | `fptilbake`, `verdikjede` |
| fpoppdrag | fpoppdrag | FPOPPDRAG_IMAGE | fpoppdrag | `verdikjede` |
| fp-formidling | fp-formidling | FPFORMIDLING_IMAGE | fpformidling | `verdikjede` |
| fp-dokgen | fp-dokgen | FPDOKGEN_IMAGE | fpdokgen | `verdikjede` |
| fp-risk | fp-risk | FPRISK_IMAGE | fprisk | `verdikjede` |
| fp-mottak | fp-mottak | FPMOTTAK_IMAGE | fpmottak | `verdikjede` |
| fp-oversikt | fp-oversikt | FPOVERSIKT_IMAGE | fpoversikt | `verdikjede` |
| fp-soknad | fp-soknad | FPSOKNAD_IMAGE | fpsoknad | `verdikjede` |
| fplos | fplos | FPLOS_IMAGE | fplos | `fplos` |
| fp-inntektsmelding | fp-inntektsmelding | FPINNTEKTSMELDING_IMAGE | fpinntektsmelding | `verdikjede` |
| fp-tilgang | fp-tilgang | FPTILGANG_IMAGE | fptilgang | `verdikjede` |

### Prerequisites

**All services must be running** before executing any test suite. Start the full environment with:
```bash
cd lokal-utvikling
./setup-lokal-utvikling.sh
cd docker-compose-lokal
docker compose up --detach
```
This starts all services (oracle, postgres, vtp, fpsak, fpabakus, fpkalkulus, fplos, fpformidling, fpdokgen, fpoppdrag, fptilbake, fprisk, fpmottak, fpsoknad, fpoversikt, fpinntektsmelding, fptilgang).

Wait for all services to be healthy before running tests:
```bash
docker compose ps
```

For IDE debugging (run specific apps outside Docker):
```bash
cd lokal-utvikling
./setup-lokal-utvikling.sh fpsak     # run fpsak in IDE, rest in Docker
cd docker-compose-lokal
docker compose up --detach --scale fpsak=0
```

### Service Lifecycle Management

**Default behavior:** Shut down all services after tests complete unless the user explicitly asks to keep them running for more tests.

#### After running tests
- If tests **pass** and user has no more tests: shut down services.
- If tests **fail** and user wants to fix and retry: keep services running.
- If user says "run more tests" or "keep running": leave services up.

#### Shutdown (default after tests complete)
```bash
cd lokal-utvikling/docker-compose-lokal
docker compose down
```

#### Rebuild cycle (code change → rebuild → retest)
When the user makes code changes and wants to retest:
1. Shut down services: `docker compose down`
2. Rebuild the changed app (in its repo): `mvn clean install -DskipTests && docker build -t <repo-name> .`
3. Start all services again: `docker compose up --detach`
4. Re-run the tests

Alternatively, restart only the changed service (faster but less safe):
```bash
docker compose up --detach --force-recreate <service-name>
```

#### On session exit
When the Copilot session ends or the user is done:
1. Shut down all services: `docker compose down`
2. This removes containers but preserves images for next session.

**Ask the user** before shutting down if there is any ambiguity about whether more tests will be run.

---

## Aksjonspunkt Bekreftelse Classes → Test Method Mapping

Each AksjonspunktBekreftelse subclass represents a user action (confirming/resolving an aksjonspunkt).
Tests that instantiate these classes are testing that aksjonspunkt's handling.
The standard 5015 (ForeslåVedtak) and 5016 (FatterVedtak) are used by nearly all tests and omitted below.
Format: `ClassName#methodName` — run with `mvn test -P <suite> -Dtest="ClassName#methodName"`

| Aksjonspunkt | Code | Test Methods |
|-------------|------|-------------|
| SjekkTerminbekreftelseBekreftelse | 5001 | Termin#farSøkerTermin, Termin#morSøkerTerminGodkjent, Termin#morSøkerTerminOvertyrt, VerdikjedeEngangsstonad#MorTredjelandsborgerSøkerEngangsStønadTest, VerdikjedeEngangsstonad#mor_innsyn_verifsere, VerdikjedeForeldrepenger#morSykepengerKunYtelseTest |
| VurderSoknadsfristBekreftelse | 5007 | Soknadsfrist#behandleSøknadsfristOgSentTilbakePåGrunnAvFodsel, Soknadsfrist#behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist |
| VurderOmsorgsovertakelseVilkårAksjonspunktDto | 5018 | Adopsjon#farSøkerAdopsjonAvvist, Adopsjon#farSøkerAdopsjonGodkjent, Adopsjon#morSøkerAdopsjonAvvist, Adopsjon#morSøkerAdopsjonGodkjent, Adopsjon#morSøkerAdopsjonOverstyrt, Aksjonspunkter#aksjonspunkt_ADOPSJONSSOKNAD_ENGANGSSTONAD_5011, Revurdering#manueltOpprettetRevurderingSendVarsel, TilbakekrevingES#opprettTilbakekrevingManuelt, VerdikjedeForeldrepenger#FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG, VerdikjedeForeldrepenger#FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe, VerdikjedeForeldrepenger#mor_adopsjon_sykdom_uke_3_til_8_automatisk_invilget |
| VarselOmRevurderingBekreftelse | 5026 | Revurdering#manueltOpprettetRevurderingSendVarsel, TilbakekrevingES#opprettTilbakekrevingManuelt |
| SjekkManglendeFødselBekreftelse | 5027 | Aksjonspunkter#aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER, BeregningVerdikjede#morFødselForSentRefusjonskrav, Fodsel#morSøker2Barn1Registrert, Fodsel#morSøkerFødselAvvist, Fodsel#morSøkerFødselOverstyrt, MorOgFarSammen#kreverDokumentasjonBeggeRett, Soknadsfrist#behandleSøknadsfristOgSentTilbakePåGrunnAvFodsel, Soknadsfrist#behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist, Termin#morSøkerTermin25DagerTilbakeITid, VerdikjedeForeldrepenger#farSettesPåVentPåManglendeVedleggOgEttersenderVedleggSomFørerTilKomplettbehandlingOgAtDenTasAvVent |
| ForeslåVedtakManueltBekreftelse | 5028 | Fodsel#farSøkerFødselAleneomsorgMenErGiftOgBorMedAnnenpart, Fodsel#farSøkerFødselMedEttArbeidsforhold, Fodsel#farSøkerFødselRegistrert, Fodsel#medmorSøkerFødsel, Fodsel#morSøkerFødselMedEttArbeidsforhold_papirsøknad, Fplos#enkelSaksmarkering, Førstegangsbehandling#morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter, Førstegangsbehandling#mor_søker_svp_ett_arbeidsforhold_endrer_ingen_tilrettelegging, Førstegangsbehandling#mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging, Førstegangsbehandling#revurder_svp_pga_innvilget_fp, MorOgFarSammen#farUtsetterOppstartRundtFødsel, MorOgFarSammen#far_skal_ikke_miste_perioder_til_mor_ved_sniking, MorOgFarSammen#kobletSakFarUtsetterAlt, MorOgFarSammen#kobletSakFarUtsetterStartdato, MorOgFarSammen#kobletSakMorSøkerEtterFar, Revurdering#ikke_avslag_pa_innvilget_perioder_pga_søknadsfrist_i_revurdering, SammenhengendeUttak#endringssøknadMedUtsettelse, Termin#MorSøkerMedEttArbeidsforholdInntektsmeldingPåGjennopptattSøknad, TilbakekrevingSVP#opprettTilbakekrevingManuelt, ToTetteOgMinsterettTester#nytt_barn_opphører_gammel_sak_pga_minsterett_oppbrukt, VerdikjedeForeldrepenger#FarTestMorSyk, VerdikjedeForeldrepenger#MorSøkerMedDagpengerTest, VerdikjedeForeldrepenger#farFårJustertUttakVedFødselshendelse, VerdikjedeForeldrepenger#farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest, VerdikjedeForeldrepenger#farUtsetterOppstartRundtFødselSøkerTermin, VerdikjedeSvangerskapspenger#morSøkerDelvisTilretteleggingMedInntektOver6GTest, VerdikjedeSvangerskapspenger#morSøkerFørstForATOgSenereForSNTest, VerdikjedeSvangerskapspenger#morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest, VerdikjedeSvangerskapspenger#morSøkerIngenTilretteleggingInntektOver6GTest, VerdikjedeSvangerskapspenger#mor_innsyn_verifsere |
| AvklarFaktaVergeBekreftelse | 5030 | Fodsel#morSøkerFødselMedVerge, TilbakekrevingES#tilbakeKrevingMedVerge |
| VurdereAnnenYtelseFørVedtakBekreftelse | 5033 | ToTetteOgMinsterettTester#mor_og_far_beholder_minsteretten_ved_to_tette_og_kan_ta_ut_denne_etter_fødel_av_siste_barn, ToTetteOgMinsterettTester#nytt_barn_opphører_gammel_sak_pga_minsterett_oppbrukt, VerdikjedeForeldrepenger#morSøkerFødselMottarForLite |
| VurderingAvKlageNfpBekreftelse | 5035 | Klage#avvistAvBelutterNFP, Klage#klageMedholUgunstNFP, Klage#klageMedholdNFP, VerdikjedeForeldrepenger#morSøkerFødselMottarForLite |
| VurderingAvInnsynBekreftelse | 5037 | Innsyn#behandleInnsynMorAvvist, Innsyn#behandleInnsynMorGodkjent |
| VurderBeregnetInntektsAvvikBekreftelse | 5038 | Aksjonspunkter#aksjonspunkt_VURDER_FARESIGNALER_KODE_5095, ArbeidsforholdVarianter#morSøkerTerminUtenAktiviteterIAareg, BeregningVerdikjede#morFødselForSentRefusjonskrav, Fodsel#morSøkerFødselMedEttArbeidsforholdOgFrilans_VurderOpptjening_VurderFaktaOmBeregning_AvvikIBeregning, Fodsel#morSøkerFødselMedEttArbeidsforhold_AvvikIBeregning, Fodsel#morSøkerFødselMedPrivatpersonSomArbeidsgiver, Fodsel#morSøkerFødselMedToArbeidsforhold_AvvikIBeregning, Fplos#morSøkerTerminUtenAktiviteterIAareg, TilbakekrevingFP#opprettOgBehandleTilbakekrevingAutomatisk, TilbakekrevingFP#opprettTilbakekrevingAutomatisk, VerdikjedeForeldrepenger#testcase_mor_fødsel, Ytelser#morSøkerFødselMottarSykepengerOgInntekter |
| VurderVarigEndringEllerNyoppstartetSNBekreftelse | 5039 | VerdikjedeForeldrepenger#morSelvstendigNæringsdrivendeTest |
| PapirSøknadForeldrepengerBekreftelse | 5040 | Aksjonspunkter#aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5040, Fodsel#morSøkerFødselMedEttArbeidsforhold_papirsøknad, VerdikjedeForeldrepenger#morSykepengerKunYtelseTest |
| VurderSoknadsfristForeldrepengerBekreftelse | 5043 | MorOgFarSammen#berørtSakOpphør, MorOgFarSammen#kreverDokumentasjonBeggeRett, Revurdering#fortsatt_tape_avslåtte_perioder_pga_søknadsfrist_i_revurdering |
| FordelBeregningsgrunnlagBekreftelse | 5046 | BeregningVerdikjede#SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G, Fplos#SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G |
| VurderPerioderOpptjeningBekreftelse | 5051 | ArbeidsforholdVarianter#morSøkerTerminUtenAktiviteterIAareg, Fodsel#morSøkerFødselMedPrivatpersonSomArbeidsgiver, Fodsel#morSøkerFødselStillingsprosent0, Fplos#morSøkerTerminUtenAktiviteterIAareg |
| KontrollerRevuderingsbehandling | 5055 | TilbakekrevingFP#opprettOgBehandleTilbakekrevingAutomatisk, TilbakekrevingFP#opprettTilbakekrevingAutomatisk |
| PapirSøknadEndringForeldrepengerBekreftelse | 5057 | Fodsel#morSøkerFødselMedEttArbeidsforhold_papirsøknad |
| VurderFaktaOmBeregningBekreftelse | 5058 | Aksjonspunkter#aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER, ArbeidsforholdVarianter#morSøkerTerminUtenAktiviteterIAareg, BeregningVerdikjede#kun_ytelse_med_vurdering_av_besteberegning, BeregningVerdikjede#morFødselForSentRefusjonskrav, Fplos#morSøkerTerminUtenAktiviteterIAareg, VerdikjedeForeldrepenger#FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG, VerdikjedeForeldrepenger#morSykepengerKunYtelseTest, VerdikjedeForeldrepenger#morSøkerFødselMottarForLite, Ytelser#morSøkerFødselMottarSykepenger |
| VurderRefusjonBeregningsgrunnlagBekreftelse | 5059 | VerdikjedeForeldrepenger#FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG |
| AvklarFaktaAleneomsorgBekreftelse | 5060 | Fodsel#farSøkerFødselAleneomsorgMenErGiftOgBorMedAnnenpart, VerdikjedeForeldrepenger#FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG, VerdikjedeForeldrepenger#testcase_mor_fødsel |
| KontrollerBesteberegningBekreftelse | 5062 | VerdikjedeForeldrepenger#MorSøkerMedDagpengerTest |
| MerkOpptjeningUtlandDto | 5068 | Aksjonspunkter#aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_, VerdikjedeForeldrepenger#farBfhrMinsterettasdasdaOgUttakTest |
| FastsettUttaksperioderManueltBekreftelse | 5071 | RegresjonPreWLB#farSøkerImfFødselMenMorErIkkeSykEllerInnlagt, SammenhengendeUttak#endringssøknad_med_aksjonspunkt_i_uttak, VerdikjedeForeldrepenger#FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe, VerdikjedeForeldrepenger#farBfhrMinsterettasdasdaOgUttakTest, VerdikjedeForeldrepenger#morSelvstendigNæringsdrivendeTest, VerdikjedeForeldrepenger#morSykepengerKunYtelseTest, VerdikjedeForeldrepenger#morSøkerTerminFårInnvilgetOgSåKommerDetEnDødfødselEtterTermin |
| KontrollerRealitetsbehandlingEllerKlage | 5073 | VerdikjedeForeldrepenger#morSøkerFødselMottarForLite |
| VurderUttakDokumentasjonBekreftelse | 5074 | MorOgFarSammen#kreverDokumentasjonBeggeRett, MorOgFarSammen#morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant, RegresjonPreWLB#BFHRMorUføreTrekkerDagerFortløpendeNårVilkårIkkeErOppfylt, RegresjonPreWLB#farSøkerImfFødselMenMorErIkkeSykEllerInnlagt, SammenhengendeUttak#utsettelse_med_avvik, VerdikjedeForeldrepenger#FarTestMorSyk, VerdikjedeForeldrepenger#farBfhrMinsterettOgUttakTest, VerdikjedeForeldrepenger#farBfhrMinsterettasdasdaOgUttakTest, VerdikjedeForeldrepenger#farBhfrTest, VerdikjedeForeldrepenger#farSøkerForeldrepengerTest, VerdikjedeForeldrepenger#farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest, VerdikjedeForeldrepenger#mor_fødsel_sykdom_innefor_første_6_ukene_utsettelse |
| FastsetteUttakKontrollerOpplysningerOmDødDto | 5076 | VerdikjedeForeldrepenger#morSelvstendigNæringsdrivendeTest, VerdikjedeForeldrepenger#morSøkerTerminFårInnvilgetOgSåKommerDetEnDødfødselEtterTermin |
| KlageFormkravNfp | 5082 | Klage#avvisFormkravNFP, Klage#avvistAvBelutterNFP, Klage#klageMedholUgunstNFP, Klage#klageMedholdNFP, VerdikjedeForeldrepenger#morSøkerFødselMottarForLite |
| VurderTilbakekrevingVedNegativSimulering | 5084 | MorOgFarSammen#berørtSakOpphør, MorOgFarSammen#farUtsetterOppstartRundtFødsel, MorOgFarSammen#far_skal_ikke_miste_perioder_til_mor_ved_sniking, MorOgFarSammen#kobletSakFarUtsetterAlt, MorOgFarSammen#kobletSakFarUtsetterStartdato, Revurdering#opprettRevurderingManuelt, SammenhengendeUttak#endringssøknadMedUtsettelse, TilbakekrevingES#opprettTilbakekrevingManuelt, TilbakekrevingFP#opprettOgBehandleTilbakekrevingAutomatisk, TilbakekrevingFP#opprettTilbakekrevingAutomatisk, ToTetteOgMinsterettTester#nytt_barn_opphører_gammel_sak_pga_minsterett_oppbrukt, VerdikjedeForeldrepenger#FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG, VerdikjedeForeldrepenger#FarTestMorSyk, VerdikjedeForeldrepenger#farFårJustertUttakVedFødselshendelse, VerdikjedeForeldrepenger#farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest, VerdikjedeForeldrepenger#farUtsetterOppstartRundtFødselSøkerTermin, VerdikjedeForeldrepenger#morSelvstendigNæringsdrivendeTest |
| ArbeidInntektsmeldingBekreftelse | 5085 | ArbeidsforholdVarianter#morSøkerTerminUtenAktiviteterIAareg, Fplos#morSøkerTerminUtenAktiviteterIAareg |
| AvklarFaktaAnnenForeldreHarRett | 5086 | Fodsel#farSøkerFødselMedEttArbeidsforhold, RegresjonPreWLB#BFHRMorUføreTrekkerDagerFortløpendeNårVilkårIkkeErOppfylt, RegresjonPreWLB#farSøkerImfFødselMenMorErIkkeSykEllerInnlagt, VerdikjedeForeldrepenger#FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe, VerdikjedeForeldrepenger#farBfhrMinsterettasdasdaOgUttakTest, VerdikjedeForeldrepenger#farSettesPåVentPåManglendeVedleggOgEttersenderVedleggSomFørerTilKomplettbehandlingOgAtDenTasAvVent, VerdikjedeForeldrepenger#farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest |
| AvklarFaktaFødselOgTilrettelegging | 5091 | Fplos#enkelSaksmarkering, Førstegangsbehandling#morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter, Førstegangsbehandling#mor_søker_svp_ett_arbeidsforhold_endrer_ingen_tilrettelegging, Førstegangsbehandling#mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging, Førstegangsbehandling#revurder_svp_pga_innvilget_fp, TilbakekrevingSVP#opprettTilbakekrevingManuelt, VerdikjedeSvangerskapspenger#morSøkerDelvisTilretteleggingMedInntektOver6GTest, VerdikjedeSvangerskapspenger#morSøkerFulltUttakForEttAvToArbeidsforholdTest, VerdikjedeSvangerskapspenger#morSøkerFørstForATOgSenereForSNTest, VerdikjedeSvangerskapspenger#morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest, VerdikjedeSvangerskapspenger#morSøkerIngenTilretteleggingInntektOver6GTest, VerdikjedeSvangerskapspenger#mor_innsyn_verifsere |
| BekreftSvangerskapspengervilkår | 5092 | Fplos#enkelSaksmarkering, Førstegangsbehandling#morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter, Førstegangsbehandling#mor_søker_svp_ett_arbeidsforhold_endrer_ingen_tilrettelegging, Førstegangsbehandling#mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging, Førstegangsbehandling#revurder_svp_pga_innvilget_fp, TilbakekrevingSVP#opprettTilbakekrevingManuelt, VerdikjedeSvangerskapspenger#morSøkerDelvisTilretteleggingMedInntektOver6GTest, VerdikjedeSvangerskapspenger#morSøkerFulltUttakForEttAvToArbeidsforholdTest, VerdikjedeSvangerskapspenger#morSøkerFørstForATOgSenereForSNTest, VerdikjedeSvangerskapspenger#morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest, VerdikjedeSvangerskapspenger#morSøkerIngenTilretteleggingInntektOver6GTest, VerdikjedeSvangerskapspenger#mor_innsyn_verifsere |
| VurderFaresignalerDto | 5095 | Aksjonspunkter#aksjonspunkt_VURDER_FARESIGNALER_KODE_5095 |
| VurderMedlemskapsvilkårForutgåendeBekreftelse | 5102 | Fodsel#morSøkerFødselFlereBarn, Fodsel#morSøkerFødselMedVerge, Medlemskap#morSøkerFødselUregistrert, Medlemskap#morSøkerFødselUtenlandsadresse, TilbakekrevingES#tilbakeKrevingMedVerge, VerdikjedeEngangsstonad#MorTredjelandsborgerSøkerEngangsStønadTest, VerdikjedeEngangsstonad#mor_innsyn_verifsere |
| AvklarAnnenforelderEøsPerioder | 5103 | VerdikjedeForeldrepenger#farBfhrMinsterettasdasdaOgUttakTest |
| OverstyrFodselsvilkaaret | 6003 | Fodsel#morSøkerFødselOverstyrt, MorOgFarSammen#berørtSakOpphør, Termin#morSøkerTerminOvertyrt |
| OverstyrMedlemskapsvilkaaret | 6005 | Medlemskap#morSøkerFødselErUtvandret, Revurdering#opprettRevurderingManuelt |

---

## Test Catalog

## Suite: fpsak

### Adopsjon.java (fpsak/engangsstonad/Adopsjon.java)
**Tags:** fpsak, engangsstonad

#### 1. `morSøkerAdopsjonGodkjent`
- **DisplayName:** "Mor søker adopsjon - godkjent"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `morSøkerAdopsjonAvvist`
- **DisplayName:** "Mor søker adopsjon - avvist - barn er over 15 år"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `morSøkerAdopsjonOverstyrt`
- **DisplayName:** "Mor søker adopsjon med overstyrt vilkår"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `farSøkerAdopsjonGodkjent`
- **DisplayName:** "Far søker adopsjon - godkjent"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `farSøkerAdopsjonAvvist`
- **DisplayName:** "Far søker adopsjon av ektefelles barn"
- **Key aksjonspunkter (non-5015/5016):** `5018` (VURDER_OMSORGSOVERTAKELSEVILKÅRET)

### Fodsel.java (fpsak/engangsstonad/Fodsel.java)
**Tags:** fpsak, engangsstonad

#### 1. `morSøkerFødselGodkjent`
- **DisplayName:** "Mor søker fødsel - godkjent"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `morSøkerFødselAvvist`
- **DisplayName:** "Mor søker fødsel - avvist"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `farSøkerFødselRegistrert`
- **DisplayName:** "Far søker registrert fødsel"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `morSøkerFødselOverstyrt`
- **DisplayName:** "Mor søker fødsel overstyrt vilkår"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `morSøkerFødselFlereBarn`
- **DisplayName:** "Mor søker fødsel med flere barn"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `morSøkerFødselMedVerge`
- **DisplayName:** "Mor søker fødsel med verge"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 7. `morSøkerUregistrertFødselMindreEnn14DagerEtter`
- **DisplayName:** "Mor søker uregistrert fødsel mindre enn 14 dager etter fødsel"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 8. `medmorSøkerFødsel`
- **DisplayName:** "Medmor søker fødsel"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Innsyn.java (fpsak/engangsstonad/Innsyn.java)
**Tags:** fpsak, engangsstonad

#### 1. `behandleInnsynMorGodkjent`
- **DisplayName:** "Behandle innsyn for mor - godkjent"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `behandleInnsynMorAvvist`
- **DisplayName:** "Behandle innsyn for mor - avvist"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Klage.java (fpsak/engangsstonad/Klage.java)
**Tags:** fpsak, engangsstonad

#### 1. `klageMedholdNFP`
- **DisplayName:** "Behandle klage via NFP - medhold"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `avvistAvBelutterNFP`
- **DisplayName:** "Behandle klage via NFP - avvist av beslutter"
- **Key aksjonspunkter (non-5015/5016):** `5035` (MANUELL_VURDERING_AV_KLAGE_NFP)

### Medlemskap.java (fpsak/engangsstonad/Medlemskap.java)
**Tags:** fpsak, engangsstonad

#### 1. `morSøkerFødselErUtvandret`
- **DisplayName:** "Mor søker fødsel er utvandret"
- **Key aksjonspunkter (non-5015/5016):** `6005` (OVERSTYRING_AV_MEDLEMSKAPSVILKÅRET)

#### 2. `morSøkerFødselUregistrert`
- **DisplayName:** "Mor søker med personstatus uregistrert"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `morSøkerFødselUtenlandsadresse`
- **DisplayName:** "Mor søker med utenlandsk adresse og ingen registert inntekt"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Revurdering.java (fpsak/engangsstonad/Revurdering.java)
**Tags:** fpsak, engangsstonad

#### 1. `manueltOpprettetRevurderingSendVarsel`
- **DisplayName:** "Manuelt opprettet revurdering"
- **Key aksjonspunkter (non-5015/5016):** `5018` (VURDER_OMSORGSOVERTAKELSEVILKÅRET)

### Soknadsfrist.java (fpsak/engangsstonad/Soknadsfrist.java)
**Tags:** foreldrepenger

#### 1. `behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist`
- **DisplayName:** "Behandle søknadsfrist og sent tilbake"
- **Key aksjonspunkter (non-5015/5016):** `5007` (MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET), `5027` (SJEKK_MANGLENDE_FØDSEL)

#### 2. `behandleSøknadsfristOgSentTilbakePåGrunnAvFodsel`
- **DisplayName:** "Behandle søknadsfrist og sent tilbake på grunn av fødsel"
- **Key aksjonspunkter (non-5015/5016):** `5007` (MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET), `5027` (SJEKK_MANGLENDE_FØDSEL)

### Termin.java (fpsak/engangsstonad/Termin.java)
**Tags:** fpsak, engangsstonad

#### 1. `morSøkerTerminGodkjent`
- **DisplayName:** "Mor søker termin - godkjent"
- **Key aksjonspunkter (non-5015/5016):** `5001` (SJEKK_TERMINBEKREFTELSE)

#### 2. `morSøkerTerminOvertyrt`
- **DisplayName:** "Mor søker termin overstyrt vilkår"
- **Key aksjonspunkter (non-5015/5016):** `6003` (OVERSTYRING_AV_FØDSELSVILKÅRET)

#### 3. `farSøkerTermin`
- **DisplayName:** "Far søker termin"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `settBehandlingPåVentOgGjenopptaOgHenlegg`
- **DisplayName:** "Setter behandling på vent og gjennoptar og henlegger"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `morSøkerTermin25DagerTilbakeITid`
- **DisplayName:** "Mor søker termin 25 dager etter fødsel"
- **Key aksjonspunkter (non-5015/5016):** `5027` (SJEKK_MANGLENDE_FØDSEL)

### Aksjonspunkter.java (fpsak/foreldrepenger/Aksjonspunkter.java)
**Tags:** util

#### 1. `aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5040`
- **DisplayName:** "REGISTRER_PAPIRSØKNAD_FORELDREPENGER"
- **Key aksjonspunkter (non-5015/5016):** `5040` (REGISTRER_PAPIRSØKNAD_FORELDREPENGER)

#### 2. `aksjonspunkt_ADOPSJONSSOKNAD_FORELDREPENGER_5004`
- **DisplayName:** "AVKLAR_ADOPSJONSDOKUMENTAJON"
- **Key aksjonspunkter (non-5015/5016):** `5018` (VURDER_OMSORGSOVERTAKELSEVILKÅRET)

#### 3. `aksjonspunkt_ADOPSJONSSOKNAD_ENGANGSSTONAD_5011`
- **DisplayName:** "MANUELL_VURDERING_AV_OMSORGSVILKÅRET"
- **Key aksjonspunkter (non-5015/5016):** (none)
- **Standard APs:** `5015` (FORESLÅ_VEDTAK)

#### 4. `aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5089`
- **DisplayName:** "VURDER_OPPTJENINGSVILKÅRET"
- **Key aksjonspunkter (non-5015/5016):** `5089` (VURDER_OPPTJENINGSVILKÅRET)

#### 5. `aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER`
- **DisplayName:** "5058 – VURDER_FAKTA_FOR_ATFL_SN"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_`
- **DisplayName:** "AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE"
- **Key aksjonspunkter (non-5015/5016):** `5068` (AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE)

#### 7. `aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_5027`
- **DisplayName:** "SJEKK_MANGLENDE_FØDSEL"
- **Key aksjonspunkter (non-5015/5016):** `5027` (SJEKK_MANGLENDE_FØDSEL)

#### 8. `aksjonspunkt_VURDER_FARESIGNALER_KODE_5095`
- **DisplayName:** "5095 – VURDER_FARESIGNALER_KODE"
- **Key aksjonspunkter (non-5015/5016):** (none)

### ArbeidsforholdVarianter.java (fpsak/foreldrepenger/ArbeidsforholdVarianter.java)
**Tags:** fpsak, foreldrepenger

#### 1. `utenArbeidsforholdMenMedInntektsmelding`
- **DisplayName:** "Mor søker fødsel, men har ikke arbeidsforhold i AAREG, sender inntektsmelding"
- **Key aksjonspunkter (non-5015/5016):** `5085` (VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING)

#### 2. `morSøkerTerminUtenAktiviteterIAareg`
- **DisplayName:** "Mor søker fødsel, men har ikke arbeidsforhold i AAREG. Legger til fiktivt arbeidsforhold."
- **Key aksjonspunkter (non-5015/5016):** `5038` (FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS), `5051` (VURDER_PERIODER_MED_OPPTJENING), `5058` (VURDER_FAKTA_FOR_ATFL_SN), `5085` (VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING), `5089` (VURDER_OPPTJENINGSVILKÅRET)

### BeregningVerdikjede.java (fpsak/foreldrepenger/BeregningVerdikjede.java)
**Tags:** fpsak

#### 1. `morSøkerFødselMedEttArbeidsforhold`
- **DisplayName:** "Mor søker fødsel med 1 arbeidsforhold og tre bortfalte naturalytelser på forskjellige tidspunkt"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `morSøkerFødselMedFullAAPOgArbeidsforhold`
- **DisplayName:** "Mor søker fødsel med full AAP og et arbeidsforhold som tilkommer etter skjæringstidspunktet"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `morSøkerFødselMedFullAAPOgArbeidsforholdSomErAktivtPåStp`
- **DisplayName:** "Mor søker fødsel med full AAP og et arbeidsforhold."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `kun_ytelse_med_vurdering_av_besteberegning`
- **DisplayName:** "Mor med kun ytelse på skjæringstidspunktet og dagpenger i opptjeningsperioden"
- **Key aksjonspunkter (non-5015/5016):** `5058` (VURDER_FAKTA_FOR_ATFL_SN)

#### 5. `SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G`
- **DisplayName:** "SN med gradering og Arbeidsforhold med refusjon over 6G"
- **Key aksjonspunkter (non-5015/5016):** `5046` (FORDEL_BEREGNINGSGRUNNLAG)
- **Method Tags:** beregning

#### 6. `morFødselForSentRefusjonskrav`
- **DisplayName:** "Mor med for sent refusjonskrav."
- **Key aksjonspunkter (non-5015/5016):** `5027` (SJEKK_MANGLENDE_FØDSEL), `5038` (FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS), `5058` (VURDER_FAKTA_FOR_ATFL_SN)

#### 7. `ATFL_samme_org_med_lønnendring_uten_inntektsmelding`
- **DisplayName:** "ATFL i samme org med lønnsendring"
- **Key aksjonspunkter (non-5015/5016):** `5058` (VURDER_FAKTA_FOR_ATFL_SN), `7003` (AUTO_VENTER_PÅ_KOMPLETT_SØKNAD), `7030` (AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE)

#### 8. `vurder_mottar_ytelse_vurder_lonnsendring`
- **DisplayName:** "Uten inntektsmelding, med lønnsendring de siste 3 månedene"
- **Key aksjonspunkter (non-5015/5016):** `5058` (VURDER_FAKTA_FOR_ATFL_SN), `7003` (AUTO_VENTER_PÅ_KOMPLETT_SØKNAD), `7030` (AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE)

#### 9. `toArbeidsforholdSammeOrgEttStarterEtterStp`
- **DisplayName:** "To arbeidsforhold samme org."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 10. `morSøkerFødselMedEttArbeidsforholdOgFrilans_VurderOpptjening_VurderFaktaOmBeregning_AvvikIBeregning`
- **DisplayName:** "Mor fødsel med frilans som eneste inntekt"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Fodsel.java (fpsak/foreldrepenger/Fodsel.java)
**Tags:** fpsak, foreldrepenger

#### 1. `morSøkerFødselMedEttArbeidsforholdOgFrilans_VurderOpptjening_VurderFaktaOmBeregning_AvvikIBeregning`
- **DisplayName:** "Mor fødsel med arbeidsforhold og frilans. Vurderer opptjening og beregning. Finner avvik"
- **Key aksjonspunkter (non-5015/5016):** `5038` (FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS)

#### 2. `morSøkerFødselMedToArbeidsforhold_AvvikIBeregning`
- **DisplayName:** "Mor søker fødsel med 2 arbeidsforhold i samme organisasjon og avvik i beregning"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `morSøkerFødselMedEttArbeidsforhold_AvvikIBeregning`
- **DisplayName:** "Mor søker fødsel med 1 arbeidsforhold og avvik i beregning"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `morSøkerFødselMedToArbeidsforholdISammeOrganisasjon`
- **DisplayName:** "Mor søker fødsel med 2 arbeidsforhold i samme organisasjon"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `morSøkerFødselMedEttArbeidsforhold`
- **DisplayName:** "Mor søker fødsel med 1 arbeidsforhold"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `morSøkerFødselMedToArbeidsforhold`
- **DisplayName:** "Mor søker fødsel med 2 arbeidsforhold med inntekt over 6G"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 7. `farSøkerFødselMedEttArbeidsforhold`
- **DisplayName:** "Far søker fødsel med 1 arbeidsforhold"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 8. `morSøkerFødselMedToArbeidsforholdISammeOrganisasjonEnInntektsmelding`
- **DisplayName:** "Mor søker fødsel med 2 arbeidsforhold i samme organisasjon med 1 inntektsmelding"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 9. `morSøkerFødselMedEttArbeidsforhold_papirsøknad`
- **DisplayName:** "Mor søker fødsel med 1 arbeidsforhold, Papirsøkand"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 10. `morSøkerFødselMedPrivatpersonSomArbeidsgiver`
- **DisplayName:** "Mor søker fødsel med privatperson som arbeidsgiver, avvik i beregning"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 11. `farSøkerFødselAleneomsorgMenErGiftOgBorMedAnnenpart`
- **DisplayName:** "(none)"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 12. `morSøkerFødselStillingsprosent0`
- **DisplayName:** "Mor søker fødsel har stillingsprosent 0"
- **Key aksjonspunkter (non-5015/5016):** `5051` (VURDER_PERIODER_MED_OPPTJENING)

#### 13. `morSøkerGraderingOgUtsettelseMedToArbeidsforhold_utenAvvikendeInntektsmeldinger`
- **DisplayName:** "Mor søker gradering. Med to arbeidsforhold. Uten avvikende inntektsmelding"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 14. `morSøkerFødselAleneomsorgKunEnHarRett`
- **DisplayName:** "Mor søker fødsel med aleneomsorg"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 15. `morSøker2Barn1Registrert`
- **DisplayName:** "Mor søker fødsel for 2 barn med 1 barn registrert"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 16. `morSøkerUregistrertEtterFør2Uker`
- **DisplayName:** "Mor søker uregistrert fødsel før det har gått 1 uke"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Klage.java (fpsak/foreldrepenger/Klage.java)
**Tags:** fpsak, foreldrepenger

#### 1. `klageMedholUgunstNFP`
- **DisplayName:** "Klage med Medhold Ugunst NFP"
- **Key aksjonspunkter (non-5015/5016):** `5035` (MANUELL_VURDERING_AV_KLAGE_NFP)

#### 2. `avvisFormkravNFP`
- **DisplayName:** "Klage avvist i formkrav av NFP"
- **Key aksjonspunkter (non-5015/5016):** (none)

### MorOgFarSammen.java (fpsak/foreldrepenger/MorOgFarSammen.java)
**Tags:** fpsak, foreldrepenger

#### 1. `morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant`
- **DisplayName:** "Mor og far koblet sak, kant til kant"
- **Key aksjonspunkter (non-5015/5016):** `5074` (VURDER_UTTAK_DOKUMENTASJON_KODE)

#### 2. `far_skal_ikke_miste_perioder_til_mor_ved_sniking`
- **DisplayName:** "Mor og far koblet sak, mors endringssøknad sniker"
- **Key aksjonspunkter (non-5015/5016):** `5084` (VURDER_FEILUTBETALING_KODE)

#### 3. `farOgMor_fødsel_ettArbeidsforholdHver_overlappendePeriode`
- **DisplayName:** "Far og mor søker fødsel med overlappende uttaksperiode"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `kobletSakIngenEndring`
- **DisplayName:** "Koblet sak endringssøknad ingen endring"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `kobletSakFarUtsetterAlt`
- **DisplayName:** "Koblet sak. Far utsetter alt/gir fra seg alt. Far ny 1gang"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `farUtsetterOppstartRundtFødsel`
- **DisplayName:** "Koblet sak. Far utsetter oppstart rundt fødsel"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 7. `kobletSakFarUtsetterStartdato`
- **DisplayName:** "Koblet sak. Far utsetter fra start med senere uttaksdato"
- **Key aksjonspunkter (non-5015/5016):** `7008` (AUTO_VENT_PGA_FOR_TIDLIG_SØKNAD)

#### 8. `berørtSakOpphør`
- **DisplayName:** "Mor får revurdering fra endringssøknad vedtak opphører"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 9. `kobletSakMorSøkerEtterFar`
- **DisplayName:** "Koblet sak mor søker etter far og sniker i køen"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 10. `kreverDokumentasjonBeggeRett`
- **DisplayName:** "Mor og far søker. Krever dokumentasjon når far søker hvor mor er i 50% stilling."
- **Key aksjonspunkter (non-5015/5016):** `5074` (VURDER_UTTAK_DOKUMENTASJON_KODE)

### RegresjonPreWLB.java (fpsak/foreldrepenger/RegresjonPreWLB.java)
**Tags:** fpsak, foreldrepenger

#### 1. `BFHRMorUføreTrekkerDagerFortløpendeNårVilkårIkkeErOppfylt`
- **DisplayName:** "Bare far har rett. Mor Ufør. Perioder uten at mor er i aktivitet skal trekke fra foreldrepenger uten aktivitetskrav."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `farSøkerImfFødselMenMorErIkkeSykEllerInnlagt`
- **DisplayName:** "Far søker rundt fødsel, saksbehandler finner ut at mor ikke er syk eller innlagt, vil føre til avslag i perioden"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Revurdering.java (fpsak/foreldrepenger/Revurdering.java)
**Tags:** fpsak, foreldrepenger

#### 1. `opprettRevurderingManuelt`
- **DisplayName:** "Revurdering opprettet manuelt av saksbehandler."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `endringssøknad`
- **DisplayName:** "Endringssøknad med ekstra uttaksperiode."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `endringssøknadMedGradering`
- **DisplayName:** "Endringssøknad med gradering"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `ikke_avslag_pa_innvilget_perioder_pga_søknadsfrist_i_revurdering`
- **DisplayName:** "Ikke få avslåg på innvilget perioder pga søknadsfrist"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `fortsatt_tape_avslåtte_perioder_pga_søknadsfrist_i_revurdering`
- **DisplayName:** "Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering"
- **Key aksjonspunkter (non-5015/5016):** (none)

### SammenhengendeUttak.java (fpsak/foreldrepenger/SammenhengendeUttak.java)
**Tags:** fpsak, foreldrepenger

#### 1. `utsettelse_med_avvik`
- **DisplayName:** "Utsettelse av forskjellige årsaker"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `endringssøknadMedUtsettelse`
- **DisplayName:** "Endringssøknad med utsettelserList"
- **Key aksjonspunkter (non-5015/5016):** `5084` (VURDER_FEILUTBETALING_KODE)

#### 3. `endringssøknad_med_aksjonspunkt_i_uttak`
- **DisplayName:** "Mor endringssøknad med aksjonspunkt i uttak"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `utsettelser_og_gradering_fra_førstegangsbehandling_skal_ikke_gå_til_manuell_behandling_ved_endringssøknad`
- **DisplayName:** "Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Termin.java (fpsak/foreldrepenger/Termin.java)
**Tags:** fpsak, foreldrepenger

#### 1. `MorSøkerMedEttArbeidsforholdInntektsmeldingFørSøknad`
- **DisplayName:** "Mor søker med ett arbeidsforhold. Inntektmelding innsendt før søknad"
- **Key aksjonspunkter (non-5015/5016):** `7013` (AUTO_VENT_PÅ_SØKNAD)

#### 2. `MorSøkerMedEttArbeidsforholdInntektsmeldingPåGjennopptattSøknad`
- **DisplayName:** "Mor søker sak behandlet før inntektsmelding mottatt"
- **Key aksjonspunkter (non-5015/5016):** `5028` (FORESLÅ_VEDTAK_MANUELT), `7003` (AUTO_VENTER_PÅ_KOMPLETT_SØKNAD), `7030` (AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE)

#### 3. `morSøkerTerminEttArbeidsforhold_avvikIGradering`
- **DisplayName:** "Mor søker termin med avvik i gradering"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `morSokerTerminUtenFPFFperiode`
- **DisplayName:** "Mor søker termin uten FPFF"
- **Key aksjonspunkter (non-5015/5016):** (none)

### ToTetteOgMinsterettTester.java (fpsak/foreldrepenger/ToTetteOgMinsterettTester.java)
**Tags:** fpsak, foreldrepenger

#### 1. `nytt_barn_opphører_gammel_sak_pga_minsterett_oppbrukt`
- **DisplayName:** "Mor brukt opp minsterett. Nytt barn opphører gammel sak fom 3 uker før termin og avslår perioder etter dette"
- **Key aksjonspunkter (non-5015/5016):** `5033` (VURDERE_ANNEN_YTELSE_FØR_VEDTAK), `5084` (VURDER_FEILUTBETALING_KODE)

#### 2. `mor_og_far_beholder_minsteretten_ved_to_tette_og_kan_ta_ut_denne_etter_fødel_av_siste_barn`
- **DisplayName:** "Mor og far beholder minsteretten ved to tette og kan ta ut denne etter fødsel av siste barn"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Ytelser.java (fpsak/foreldrepenger/Ytelser.java)
**Tags:** fpsak, foreldrepenger

#### 1. `morSøkerFødselMottarSykepenger`
- **DisplayName:** "Mor søker fødsel og mottar sykepenger"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `morSøkerFødselMottarSykepengerOgInntekter`
- **DisplayName:** "Mor søker fødsel og mottar sykepenger og inntekter"
- **Key aksjonspunkter (non-5015/5016):** (none)

### Førstegangsbehandling.java (fpsak/svangerskapspenger/Førstegangsbehandling.java)
**Tags:** fpsak, svangerskapspenger

#### 1. `morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter`
- **DisplayName:** "Mor søker SVP med to arbeidsforhold - hel tilrettelegging"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging`
- **DisplayName:** "Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `mor_søker_svp_ett_arbeidsforhold_endrer_ingen_tilrettelegging`
- **DisplayName:** "Mor søker SVP med ett arbeidsforhold - halv og så endring til ingen tilrettelegging. Full refusjon"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `revurder_svp_pga_innvilget_fp`
- **DisplayName:** "Mor søker SVP og FP - revurder SVP"
- **Key aksjonspunkter (non-5015/5016):** (none)


---

## Suite: fpkalkulus

### ArbeidNæringFrilansTest.java (fpkalkulus/foreldrepenger/ArbeidNæringFrilansTest.java)
**Tags:** fpkalkulus

#### 1. `fp_arbeid_næring_avvik_på_begge`
- **DisplayName:** "Foreldrepenger - arbeidsforhold og selvstendig næringsdrivende"
- **Key aksjonspunkter (non-5015/5016):** (none)

### ArbeidstakerTest.java (fpkalkulus/foreldrepenger/ArbeidstakerTest.java)
**Tags:** fpkalkulus

#### 1. `foreldrepenger_arbeidsforhold_avvik`
- **DisplayName:** "Foreldrepenger - ett arbeidsforhold og frilans med avvik"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `foreldrepenger_arbeidsforhold_tilkommer_på_skjæringstidspunktet`
- **DisplayName:** "Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet og søker refusjon"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `foreldrepenger_arbeidsforhold_tilkommer_mer_refusjon_enn_brutto`
- **DisplayName:** "Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet, refusjon > brutto ved stp"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `foreldrepenger_arbeidstaker_uten_inntektsmelding_frilans_samme_org`
- **DisplayName:** "Foreldrepenger - arbeidstaker uten inntektsmelding og frilans i samme organisasjon"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `foreldrepenger_arbeidstaker_tilkommet_refusjon`
- **DisplayName:** "Foreldrepenger - arbeidstaker uten inntektsmelding, sender inntektsmelding for revurdering"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `foreldrepenger_arbeidstaker_tilkommet_okt_refusjon`
- **DisplayName:** "Foreldrepenger - arbeidstaker med revurdering, økt refusjon i revurdering."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 7. `foreldrepenger_at_sn_refusjon_for_virksomhet_med_2_arbeidsforhold_og_overstyring_av_beregningaktiviteter`
- **DisplayName:** "Foreldrepenger - Refusjon for virksomhet med 2 arbeidsforhold som avslutter og tilkommer på hver sin side av skjæringstidspunktet"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 8. `fp_at_sn_bg_over_6G_bg_fra_arbeid_under_6G_med_full_refusjon`
- **DisplayName:** "Foreldrepenger - AT/SN med totalt BG over 6G. BG og refusjon fra AG under 6G"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 9. `fp_at_sn_med_avvik_beregningsgrunnlag_skal_omfordeles_automatisk_grunnet_refusjon`
- **DisplayName:** "Foreldrepenger - AT og SN med varig endring og avvik. Automatisk fordeling av beregningsgrunnlag fra AT til SN"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 10. `fp_arbeid_avslutter_dagen_før_stp`
- **DisplayName:** "Foreldrepenger - Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 11. `fp_arbeidsforhold_med_permisjon`
- **DisplayName:** "Foreldrepenger - Arbeidstaker med 2 arbeidsforhold, permisjon fra det ene."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 12. `fp_flere_arbeidsforhold_samme_org_en_med_perm`
- **DisplayName:** "Foreldrepenger - Arbeidstaker med 2 arbeidsforhold i samme bedrift, permisjon fra det ene."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 13. `fp_tilkommet_arbeid_og_fordeling_av_naturalytelse`
- **DisplayName:** "Foreldrepenger - Tilkommet arbeid med refusjon samtidig som naturalytelse hos en annen arbeidsgiver"
- **Key aksjonspunkter (non-5015/5016):** (none)

### BesteberegningTest.java (fpkalkulus/foreldrepenger/BesteberegningTest.java)
**Tags:** fpkalkulus

#### 1. `besteberegning_for_arbeidstaker_med_dagpenger_i_opptjeningsperioden_bruker_ikke_seks_beste_måneder`
- **DisplayName:** "Besteberegning - Arbeidstaker med dagpenger i opptjeningsperioden. Beregning etter kap 8 gir bedre resultat."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `besteberegning_for_arbeidstaker_med_dagpenger_i_opptjeningsperioden_bruker_seks_beste_måneder`
- **DisplayName:** "Besteberegning - Arbeidstaker med dagpenger i opptjeningsperioden. Seks beste måneder gir best resultat."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `besteberegning_med_dagpenger_på_skjæringstidspunktet`
- **DisplayName:** "Besteberegning - Arbeidstaker med dagpenger på skjæringstidspunktet"
- **Key aksjonspunkter (non-5015/5016):** (none)

### GraderingTest.java (fpkalkulus/foreldrepenger/GraderingTest.java)
**Tags:** fpkalkulus

#### 1. `fp_to_arbeidsforhold_med_gradering_og_refusjon`
- **DisplayName:** "Foreldrepenger - Søker gradering for arbeid med refusjonskrav."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `fp_to_arbeidsforhold_gradering_uten_refusjon_og_under_6G_refusjon_totalt`
- **DisplayName:** "Foreldrepenger - Søker gradering for arbeid uten refusjonskrav og under 6G total refusjon."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `fp_to_arbeidsforhold_gradering_uten_refusjon_og_over_6G_refusjon_totalt`
- **DisplayName:** "Foreldrepenger - Søker gradering for arbeid uten refusjonskrav og over 6G total refusjon."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `fp_arbeid_uten_refusjon_tilkommet_arbeid_med_gradering_uten_refusjon`
- **DisplayName:** "Foreldrepenger - Søker gradering for tilkommet arbeid uten refusjonskrav."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `fp_arbeid_uten_refusjon_tilkommet_arbeid_med_gradering_med_refusjon`
- **DisplayName:** "Foreldrepenger - Søker gradering for tilkommet arbeid med refusjonskrav."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `fp_arbeid_uten_refusjon_tilkommet_arbeid_med_gradering_med_refusjon_i_perioder_uten_gradering`
- **DisplayName:** "Foreldrepenger - Søker gradering for tilkommet arbeid med refusjonskrav i perioder uten gradering."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 7. `foreldrepenger_søker_gradering_for_næring_med_arbeid_over_6G`
- **DisplayName:** "Foreldrepenger - Søker gradering for næring med arbeidsinntekt over 6G."
- **Key aksjonspunkter (non-5015/5016):** (none)

### MilitærTest.java (fpkalkulus/foreldrepenger/MilitærTest.java)
**Tags:** fpkalkulus

#### 1. `foreldrepenger_militær_settes_til_3g_dekningsgrad_80`
- **DisplayName:** "Foreldrepenger - Kun militær i opptjeningen"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `foreldrepenger_arbeid_under_3g_militær_dekker_rest`
- **DisplayName:** "Foreldrepenger - Militær og arbeidstaker i opptjening."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `foreldrepenger_frilans_over_3g_ingenting_til_militær`
- **DisplayName:** "Foreldrepenger - Militær og frilans i opptjening."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `foreldrepenger_arbeid_næring_over_3g_0_til_militær`
- **DisplayName:** "Foreldrepenger - Arbeidstaker, næring og militær i opptjeningen."
- **Key aksjonspunkter (non-5015/5016):** (none)

### ArbeidstakerTest.java (fpkalkulus/svangerskapspenger/ArbeidstakerTest.java)
**Tags:** fpkalkulus

#### 1. `svangerskapspenger_arbeidstaker_full_refusjon`
- **DisplayName:** "Svangerskapspenger - arbeidstaker med inntektsmelding med full refusjon"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `svp_søkt_refusjon_før_start_av_permisjon_og_avvik`
- **DisplayName:** "Svangerskapspenger - Arbeistaker med avvik og søkt refusjon før start av ytelse"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `svp_arbeid_avslutter_dagen_før_stp`
- **DisplayName:** "Svangerskapspenger - Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `svp_tilkommet_arbeidsforhold_refusjon_i_deler_av_uttak`
- **DisplayName:** "Svangerskapspenger - Tilkommet arbeidsforhold med refusjon i deler av uttaket"
- **Key aksjonspunkter (non-5015/5016):** (none)


---

## Suite: fplos

### Fplos.java (fplos/Fplos.java)
**Tags:** fplos

#### 1. `enkelSaksmarkering`
- **DisplayName:** "Saksmarkering i fpsak gir oppgaveegenskap i LOS"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G`
- **DisplayName:** "SN får saksmarkering Næring i LOS"
- **Key aksjonspunkter (non-5015/5016):** (none)
- **Method Tags:** beregning

#### 3. `morSøkerTerminUtenAktiviteterIAareg`
- **DisplayName:** "Fødsel og fiktivt arbeidsforhold gir bare beslutter-egenskap i LOS."
- **Key aksjonspunkter (non-5015/5016):** `5038` (FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS), `5051` (VURDER_PERIODER_MED_OPPTJENING), `5058` (VURDER_FAKTA_FOR_ATFL_SN), `5085` (VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING), `5089` (VURDER_OPPTJENINGSVILKÅRET)

#### 4. `opprettSaksliste`
- **DisplayName:** "Enkel sjekk på at opprettelse av saksliste ikke gir feil"
- **Key aksjonspunkter (non-5015/5016):** (none)


---

## Suite: fptilbake

### TilbakekrevingES.java (fptilbake/engangsstonad/TilbakekrevingES.java)
**Tags:** tilbakekreving, fptilbake

#### 1. `opprettTilbakekrevingManuelt`
- **DisplayName:** "1. Førstegangssøknad innvilges. Revurdering opprettes som fører til avslag. Saksbehandler oppretter tilbakekreving."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `tilbakeKrevingMedVerge`
- **DisplayName:** "2. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling med verge"
- **Key aksjonspunkter (non-5015/5016):** (none)

### TilbakekrevingFP.java (fptilbake/foreldrepenger/TilbakekrevingFP.java)
**Tags:** tilbakekreving, fptilbake

#### 1. `opprettTilbakekrevingManuelt`
- **DisplayName:** "1. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling og revurdering"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `opprettTilbakekrevingAutomatisk`
- **DisplayName:** "2. Oppretter en tilbakekreving automatisk etter negativ simulering på fpsak revurdering"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `opprettOgBehandleTilbakekrevingAutomatisk`
- **DisplayName:** "3. Oppretter og behandler en tilbakekreving helt-automatisk"
- **Key aksjonspunkter (non-5015/5016):** (none)

### TilbakekrevingRevurdering.java (fptilbake/foreldrepenger/TilbakekrevingRevurdering.java)
**Tags:** tilbakekreving, fptilbake

#### 1. `opprettTilbakekrevingManuelt`
- **DisplayName:** "Oppretter en tilbakekreving og deretter tilbakekreving revurdering manuelt etter Fpsak-førstegangsbehandling og revurdering"
- **Key aksjonspunkter (non-5015/5016):** (none)

### TilbakekrevingSVP.java (fptilbake/svangerskapspenger/TilbakekrevingSVP.java)
**Tags:** tilbakekreving, fptilbake

#### 1. `opprettTilbakekrevingManuelt`
- **DisplayName:** "1. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling og revurdering"
- **Key aksjonspunkter (non-5015/5016):** (none)


---

## Suite: verdikjedetester

### AdressebeskyttelseOgSkjermetPersonTester.java (verdikjedetester/AdressebeskyttelseOgSkjermetPersonTester.java)
**Tags:** verdikjede

#### 1. `adressebeskyttet_strengt_fortrolig_kun_saksbehandles_av_sakbehanlder_med_strengt_fortrolig_ad_gruppe`
- **DisplayName:** "(none)"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `skjermet_person_må_behandles_av_saksbehandler_med_egen_ansatt_ad_rolle`
- **DisplayName:** "(none)"
- **Key aksjonspunkter (non-5015/5016):** (none)

### VerdikjedeEngangsstonad.java (verdikjedetester/VerdikjedeEngangsstonad.java)
**Tags:** verdikjede

#### 1. `MorTredjelandsborgerSøkerEngangsStønadTest`
- **DisplayName:** "1: Mor er tredjelandsborger og søker engangsstønad"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `mor_innsyn_verifsere`
- **DisplayName:** "2: Verifiser innsyn har korrekt data"
- **Key aksjonspunkter (non-5015/5016):** (none)

### VerdikjedeForeldrepenger.java (verdikjedetester/VerdikjedeForeldrepenger.java)
**Tags:** verdikjede

#### 1. `testcase_mor_fødsel`
- **DisplayName:** "(none)"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `morSelvstendigNæringsdrivendeTest`
- **DisplayName:** "2: Mor selvstendig næringsdrivende, varig endring. Søker dør etter behandlingen er ferdigbehandlet."
- **Key aksjonspunkter (non-5015/5016):** `5084` (VURDER_FEILUTBETALING_KODE)

#### 3. `morSykepengerKunYtelseTest`
- **DisplayName:** "3: Mor, sykepenger, kun ytelse, papirsøknad"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `farSøkerForeldrepengerTest`
- **DisplayName:** "4: Far søker resten av fellesperioden og hele fedrekvoten med gradert uttak og opphold mellom disse."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `farSøkerSomFrilanserOgTarUt2UkerIfmFødsel`
- **DisplayName:** "5: Far søker fellesperiode og fedrekvote som frilanser. Tar ut 2 uker ifm fødsel."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest`
- **DisplayName:** "(none)"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 7. `FarTestMorSyk`
- **DisplayName:** "7: Far har AAP og søker overføring av gjennværende mødrekvoten fordi mor er syk."
- **Key aksjonspunkter (non-5015/5016):** `5084` (VURDER_FEILUTBETALING_KODE)

#### 8. `MorSøkerFor2BarnHvorHunFårBerørtSakPgaFar`
- **DisplayName:** "8: Mor har tvillinger og søker om hele utvidelsen."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 9. `MorSøkerMedDagpengerTest`
- **DisplayName:** "9: Mor søker med dagpenger som grunnlag, besteberegnes automatisk"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 10. `FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG`
- **DisplayName:** "10: Far, aleneomsorg, søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 11. `FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe`
- **DisplayName:** "11: Far søker adopsjon hvor han søker hele fedrekvoten og fellesperiode, og får berørt sak pga mor"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 12. `morSøkerFødselMottarForLite`
- **DisplayName:** "12: Mor søker fødsel og mottar sykepenger uten inntektskilder, får avslag, klager og får medhold."
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 13. `morSøkerTerminFårInnvilgetOgSåKommerDetEnDødfødselEtterTermin`
- **DisplayName:** "13: Mor søker på termin og får innvilget, men etter termin mottas det en dødfødselshendelse"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 14. `mor_fødsel_sykdom_innefor_første_6_ukene_utsettelse`
- **DisplayName:** "14: Mor, fødsel, sykdom uke 5 til 10, må søke om utsettelse fra uke 5-6"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 15. `mor_adopsjon_sykdom_uke_3_til_8_automatisk_invilget`
- **DisplayName:** "15: Mor, adopsjon, sykdom uke 3 til 8, trenger ikke søke utsettelse for uke 3 til 6"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 16. `farBhfrTest`
- **DisplayName:** "(none)"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 17. `mor_innsyn_verifsere`
- **DisplayName:** "17: Mor happy case - verifiser innsyn har korrekt data"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 18. `farUtsetterOppstartRundtFødselSøkerTermin`
- **DisplayName:** "18: Koblet sak. Far utsetter oppstart rundt fødsel, søker termin og med fødselshendelse"
- **Key aksjonspunkter (non-5015/5016):** `5084` (VURDER_FEILUTBETALING_KODE)

#### 19. `farFårJustertUttakVedFødselshendelse`
- **DisplayName:** "19: Far får justert uttaket rundt termin etter fødselshendelse"
- **Key aksjonspunkter (non-5015/5016):** `5084` (VURDER_FEILUTBETALING_KODE)

#### 20. `farSettesPåVentPåManglendeVedleggOgEttersenderVedleggSomFørerTilKomplettbehandlingOgAtDenTasAvVent`
- **DisplayName:** "20: Far søker termin hvor han velger SEND_SENERE på terminbekreftelse. Havner på vent pga kompletthet. Far ettersender og behandlingen forsetter"
- **Key aksjonspunkter (non-5015/5016):** `7003` (AUTO_VENTER_PÅ_KOMPLETT_SØKNAD)

#### 21. `farBfhrMinsterettOgUttakTest`
- **DisplayName:** "(none)"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 22. `farBfhrMinsterettasdasdaOgUttakTest`
- **DisplayName:** "22: Far søker med mor rett og har tatt ut mødrekvote og deler av fellesperioden i EØS."
- **Key aksjonspunkter (non-5015/5016):** `5103` (AVKLAR_UTTAK_I_EØS_FOR_ANNENPART_KODE)

### VerdikjedeSvangerskapspenger.java (verdikjedetester/VerdikjedeSvangerskapspenger.java)
**Tags:** verdikjede

#### 1. `morSøkerIngenTilretteleggingInntektOver6GTest`
- **DisplayName:** "1: Mor søker fullt uttak med inntekt under 6G"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 2. `morSøkerDelvisTilretteleggingMedInntektOver6GTest`
- **DisplayName:** "2: Mor søker gradert uttak med inntekt over 6G"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 3. `morSøkerFulltUttakForEttAvToArbeidsforholdTest`
- **DisplayName:** "3: Mor søk fullt uttak for ett av to arbeidsforhold i samme virksomhet"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 4. `morSøkerFørstForATOgSenereForSNTest`
- **DisplayName:** "4: Mor kombinert AT/SN søker i to omganger"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 5. `morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest`
- **DisplayName:** "5: Mor har flere AG og søker fullt uttak for begge AFene"
- **Key aksjonspunkter (non-5015/5016):** (none)

#### 6. `mor_innsyn_verifsere`
- **DisplayName:** "6: Verifiser innsyn har korrekt data"
- **Key aksjonspunkter (non-5015/5016):** (none)


---

## Summary

- **Total test classes:** 36
- **Total test methods:** 174

### Aksjonspunkt Usage Summary

| Code | Constant Name | # Methods |
|------|---------------|-----------|
| 5001 | SJEKK_TERMINBEKREFTELSE | 1 |
| 5007 | MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET | 2 |
| 5015 | FORESLÅ_VEDTAK | 1 |
| 5018 | VURDER_OMSORGSOVERTAKELSEVILKÅRET | 3 |
| 5027 | SJEKK_MANGLENDE_FØDSEL | 5 |
| 5028 | FORESLÅ_VEDTAK_MANUELT | 1 |
| 5033 | VURDERE_ANNEN_YTELSE_FØR_VEDTAK | 1 |
| 5035 | MANUELL_VURDERING_AV_KLAGE_NFP | 2 |
| 5038 | FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS | 4 |
| 5040 | REGISTRER_PAPIRSØKNAD_FORELDREPENGER | 1 |
| 5046 | FORDEL_BEREGNINGSGRUNNLAG | 1 |
| 5051 | VURDER_PERIODER_MED_OPPTJENING | 3 |
| 5058 | VURDER_FAKTA_FOR_ATFL_SN | 6 |
| 5068 | AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE | 1 |
| 5074 | VURDER_UTTAK_DOKUMENTASJON_KODE | 2 |
| 5084 | VURDER_FEILUTBETALING_KODE | 7 |
| 5085 | VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING | 3 |
| 5089 | VURDER_OPPTJENINGSVILKÅRET | 3 |
| 5103 | AVKLAR_UTTAK_I_EØS_FOR_ANNENPART_KODE | 1 |
| 6003 | OVERSTYRING_AV_FØDSELSVILKÅRET | 1 |
| 6005 | OVERSTYRING_AV_MEDLEMSKAPSVILKÅRET | 1 |
| 7003 | AUTO_VENTER_PÅ_KOMPLETT_SØKNAD | 4 |
| 7008 | AUTO_VENT_PGA_FOR_TIDLIG_SØKNAD | 1 |
| 7013 | AUTO_VENT_PÅ_SØKNAD | 1 |
| 7030 | AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE | 3 |
