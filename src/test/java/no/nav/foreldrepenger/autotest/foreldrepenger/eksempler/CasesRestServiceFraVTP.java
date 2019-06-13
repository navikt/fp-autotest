package no.nav.foreldrepenger.autotest.foreldrepenger.eksempler;


/*
@Api(tags = {"Cases"})
@Path("/api/cases")
public class CasesRestService extends FpsakTestBase {

    public CasesRestService() throws Exception {
        super();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Aksjonspunkt stopper på fødsel", notes = (""))
    @Path("/aksjonspunktStoppFoedsel")
    public Response aksjonspunktStoppFoedsel(){
        try {
            TestscenarioDto testscenario = opprettScenario("160");

            String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
            LocalDate fødselsdato = LocalDate.now().minusWeeks(4);
            LocalDate fpStartdatoMor = fødselsdato.minusWeeks(4);

            ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fødselsdato);
            fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
            long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
            List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
            fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
            saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
            saksbehandler.hentFagsak(saksnummerMor);
            return Response.ok(saksnummerMor).build();
        } catch (Exception e) {
            String message = "Error: " + e.toString();
            return Response.ok(message).build();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Mor og far søker etter fødselen, kant til kant.", notes = ("sender et case til FPSAK"))
    @Path("/mor-og-far-søker-etter-fødsel-kant-til-kant")
    public Response testcase_morOgFarSøkerEtterFødsel_kantTilKantsøknad(@Context UriInfo uriInfo) {

        try {
            TestscenarioDto testscenario = opprettScenario("82");

            String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
            LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
            LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

            ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fødselsdato);
            fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
            long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
            List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
            fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
            saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
            saksbehandler.hentFagsak(saksnummerMor);
            saksbehandler.velgFørstegangsbehandling();
            saksbehandler.ventTilBehandlingsstatus("AVSLU");

            String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
            String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
            LocalDate fpStartDatoFar = fødselsdato.plusWeeks(10).plusDays(1);

            Fordeling fordeling = new ObjectFactory().createFordeling();
            fordeling.setAnnenForelderErInformert(true);
            List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
            perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fpStartDatoFar.plusWeeks(6).minusDays(1)));
            perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartDatoFar.plusWeeks(6), fpStartDatoFar.plusWeeks(10)));

            ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farAktørId, morAktørId, fødselsdato, LocalDate.now(), fordeling);
            fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

            long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
            List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
            fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);
            saksbehandler.hentFagsak(saksnummerFar);
            saksbehandler.velgFørstegangsbehandling();
            return Response.ok(saksnummerMor).build();
        } catch (Exception e) {
            String message = "Error: " + e.toString();
            return Response.ok(message).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Mor er tom for dager.", notes = ("sender et case til FPSAK"))
    @Path("/mor-er-tom-for-dager")
    public Response testcase_mor_tom_for_dager(@Context UriInfo uriInfo) {
        try {
            TestscenarioDto testscenario = opprettScenario("50");

            String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
            LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
            LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

            Fordeling fordeling = new ObjectFactory().createFordeling();
            fordeling.setAnnenForelderErInformert(true);
            List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
            perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
            perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
            perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(23).minusDays(1)));
            perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(23), fødselsdato.plusWeeks(33).minusDays(1)));
            perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(33), fødselsdato.plusWeeks(34).minusDays(1)));

            ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fordeling, fødselsdato);
            fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
            long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
            List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
            fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
            return Response.ok(saksnummerMor).build();
        } catch (Exception e) {
            String message = "Error: " + e.toString();
            return Response.ok(message).build();
        }
    }


}
*/
