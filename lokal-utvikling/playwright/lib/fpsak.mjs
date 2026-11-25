// fpsak-frontend: status, aksjonspunkter, uttak og vedtak.
import { tekstlinjer } from './browser.mjs';

const kropp = async (p) => (await tekstlinjer(p.locator('body'))).join(' / ');

async function ventEtterBekreft(p, knapp) {
    await knapp.waitFor({ state: 'hidden', timeout: 10000 }).catch(() => {});
    await p.locator('.navds-loader, .aksel-loader, [aria-busy="true"]').first().waitFor({ state: 'hidden', timeout: 5000 }).catch(() => {});
}

export async function lastPåNytt(p) {
    await p.reload({ waitUntil: 'domcontentloaded' });
    await p.getByText(/Behandlingsstatus|Velg behandling/).first().waitFor({ timeout: 15000 });
    await p.waitForLoadState('networkidle');
}

// Velger behandling av gitt type når saken har flere ('Revurdering' | 'Førstegangsbehandling'). `nr` velger blant
// flere av samme type i rekkefølgen fpsak viser dem (0 = første i listen). VIS_BEHANDLINGER=1 skriver ut listen.
export async function velgBehandling(p, type = 'Revurdering', { nr = 0 } = {}) {
    if (!(await p.getByText('Velg behandling').count())) return;
    if (process.env.VIS_BEHANDLINGER === '1') console.log('BEHANDLINGER', (await tekstlinjer(p.locator('body'))).join(' / ').slice(0, 1500));
    await p.getByText(type, { exact: true }).nth(nr).click();
    await p.getByText('Velg behandling').waitFor({ state: 'hidden', timeout: 10000 }).catch(() => {});
    await p.getByText('Behandlingsstatus').first().waitFor({ timeout: 15000 }).catch(() => {});
}

export async function status(p) {
    const t = await kropp(p);
    return {
        sak: t.match(/\d+ - (Løpende|Under behandling|Avsluttet|Opprettet)/)?.[0],
        behandling: t.match(/(Førstegangsbehandling|Revurdering)[^]*?Resultat \/ [^/]*/)?.[0]?.replace(/ \/ /g, ' '),
        aksjonspunkt: [...new Set(t.match(/(Vurder|Kontroller|Avklar|Ikke gyldig grunn)[^/]*/g) ?? [])],
        kanFatte: t.includes('Fatt vedtak'),
    };
}

// Behandlingen fra fpsak-API-et, samme kall som fpsak-frontend: { uuid, status (UTRED, OPPRE, FVED, AVSLU ...), påVent, venteårsak,
// frist, aksjonspunkt } der aksjonspunkt er de åpne (status OPPR) med kode og tekst fra siden når den finnes.
// Siden må vise saken (fagsak/<saksnummer>). nr velger blant flere av samme type, nyeste først.
// Bruk denne, ikke teksten på siden: historikken har «Behandlingen er satt på vent» også etter at behandlingen er gjenopptatt.
export async function behandlingFraApi(p, type = 'Revurdering', { nr = 0 } = {}) {
    const { origin, pathname } = new URL(p.url());
    const sak = pathname.match(/fagsak\/(\d+)/)?.[1];
    if (!sak) throw new Error(`Siden viser ikke en sak: ${pathname}`);
    const full = await (await p.request.get(`${origin}/fpsak/api/fagsak/full?saksnummer=${sak}`)).json();
    const kode = type === 'Førstegangsbehandling' ? 'BT-002' : 'BT-004';
    const valgt = full.behandlinger.filter((b) => b.type === kode).sort((x, y) => String(y.opprettet).localeCompare(String(x.opprettet)))[nr];
    if (!valgt) return null;
    const b = await (await p.request.get(`${origin}/fpsak/api/behandling?uuid=${valgt.uuid}`)).json();
    return {
        uuid: valgt.uuid,
        status: b.status,
        påVent: !!b.behandlingPåVent,
        venteårsak: b.venteÅrsakKode,
        frist: b.fristBehandlingPåVent,
        aksjonspunkt: (b.aksjonspunkt ?? []).filter((a) => a.status === 'OPPR').map((a) => a.definisjon),
    };
}

// Uttaksresultatet fra API-et (samme kall som uttakssteget): { type, status, dto, søker, annen }, der søker og annen er perioder
// som tekst: «fom–tom RESULTAT konto trekk=<dager> utb=<grad> [arbeid=<prosent>] [samtidig=<prosent>] [utsettelse/opphold/overføring] årsak».
// Nyeste behandling av typen (nr = 1 gir den nest nyeste). Finnes ingen revurdering, brukes førstegangsbehandlingen.
export async function uttakFraApi(p, type = 'Revurdering', { nr = 0 } = {}) {
    const { origin, pathname } = new URL(p.url());
    const sak = pathname.match(/fagsak\/(\d+)/)?.[1];
    const full = await (await p.request.get(`${origin}/fpsak/api/fagsak/full?saksnummer=${sak}`)).json();
    const kode = (x) => (x && typeof x === 'object' ? x.kode : x);
    const sorter = (bt) => full.behandlinger.filter((b) => kode(b.type) === bt).sort((x, y) => String(y.opprettet).localeCompare(String(x.opprettet)));
    let b = sorter(type === 'Førstegangsbehandling' ? 'BT-002' : 'BT-004')[nr];
    let valgtType = type;
    if (!b) [b, valgtType] = [sorter('BT-002')[0], 'Førstegangsbehandling'];
    const beh = await (await p.request.get(`${origin}/fpsak/api/behandling?uuid=${b.uuid}`)).json();
    const dto = await (await p.request.get(`${origin}/fpsak/api/behandling/uttak/resultat-perioder?uuid=${b.uuid}`)).json();
    const tekst = (x) => {
        const akt = x.aktiviteter ?? [];
        const konto = [...new Set(akt.map((a) => kode(a.stønadskontoType)).filter(Boolean))].join('+') || '-';
        const trekk = akt.map((a) => a.trekkdagerDesimaler ?? a.trekkdager).join('+');
        const utb = akt.map((a) => a.utbetalingsgrad).join('+');
        const arbeid = akt.filter((a) => a.prosentArbeid > 0).map((a) => a.prosentArbeid).join('+');
        return [
            `${x.fom}–${x.tom}`,
            kode(x.periodeResultatType),
            konto,
            `trekk=${trekk}`,
            `utb=${utb}`,
            arbeid && `arbeid=${arbeid}`,
            x.samtidigUttak && `samtidig=${x.samtidigUttaksprosent ?? 'ja'}`,
            x.graderingInnvilget && 'gradert',
            kode(x.utsettelseType) && kode(x.utsettelseType) !== '-' && `utsettelse=${kode(x.utsettelseType)}`,
            kode(x.oppholdÅrsak) && kode(x.oppholdÅrsak) !== '-' && `opphold=${kode(x.oppholdÅrsak)}`,
            kode(x.overføringÅrsak) && kode(x.overføringÅrsak) !== '-' && `overføring=${kode(x.overføringÅrsak)}`,
            kode(x.periodeResultatÅrsak),
        ].filter(Boolean).join(' ');
    };
    return { type: valgtType, status: kode(beh.status), dto, søker: (dto.perioderSøker ?? []).map(tekst), annen: (dto.perioderAnnenpart ?? []).map(tekst) };
}

// «VENT_ÅPEN_BEHANDLING til 2026-12-04» når behandlingen er på vent (fra API-et), ellers null.
export async function påVent(p, type = 'Revurdering', opt = {}) {
    const b = await behandlingFraApi(p, type, opt);
    if (!b?.påVent) return null;
    return [b.venteårsak ?? 'på vent', b.frist && `til ${b.frist}`].filter(Boolean).join(' ');
}

// Poller behandlingen (siden lastes på nytt) til den trenger noe eller er ferdig. Status og aksjonspunkt kommer fra API-et.
// Gir { status (fra siden), api (behandlingFraApi), grunn, påVent }:
//   avsluttet | fatte («Fatt vedtak») | godkjenning («Til godkjenning») | aksjonspunkt (åpent, se api.aksjonspunkt) |
//   vent eller uendret: samme tilstand i `uendret` sjekker (FPSAK_UENDRET, standard 6, ca. 30 s med pause 5 s), 'vent' når
//   behandlingen er satt på vent (påVent har årsaken). En inntektsmelding fra familie.sh kommer ofte noen sekunder etter søknaden,
//   så vent gis samme frist. Lokalt går behandling fort: står den stille i 30 s, er det nesten alltid et funn.
export async function ventPåBehandling(p, type = 'Revurdering', { nr = 0, uendret = Number(process.env.FPSAK_UENDRET ?? 6), pause = 5000 } = {}) {
    let forrige;
    let likt = 0;
    for (;;) {
        await velgBehandling(p, type, { nr });
        const s = await status(p);
        const api = await behandlingFraApi(p, type, { nr });
        const vent = api?.påVent ? [api.venteårsak ?? 'på vent', api.frist && `til ${api.frist}`].filter(Boolean).join(' ') : null;
        if (api?.status === 'AVSLU' || (!api && /Avsluttet/.test(s.behandling ?? ''))) return { status: s, api, grunn: 'avsluttet' };
        if (!vent && s.kanFatte) return { status: s, api, grunn: 'fatte' };
        if (!vent && (await p.getByRole('button', { name: 'Til godkjenning' }).count())) return { status: s, api, grunn: 'godkjenning' };
        if (!vent && api?.aksjonspunkt.length) return { status: s, api, grunn: 'aksjonspunkt' };
        const nå = JSON.stringify(api ?? s);
        if (vent && nå !== forrige) console.log('VENTER', vent);
        likt = nå === forrige ? likt + 1 : 1;
        forrige = nå;
        if (likt >= uendret) return vent ? { status: s, api, grunn: 'vent', påVent: vent } : { status: s, api, grunn: 'uendret' };
        await p.waitForTimeout(pause);
        await lastPåNytt(p);
    }
}

// Behandler behandlingen av typen til vedtak: løser «Vurder faresignalene» (ikke reelle), «Vurder om den andre forelderen har rett»
// (harRett), «Kontroller dokumentasjon» (dokumentasjon, standard Godkjent), fatter vedtak og godkjenner som Birger Beslutter
// (også når saken allerede står på «Fatter vedtak», aksjonspunkt 5016).
// På vent eller uendret status i ca. 30 s, eller et aksjonspunkt den ikke løser, skrives ut som FUNN, og resultatet fra ventPåBehandling returneres.
// fpsak er `fpsak` fra run.mjs. hvem er bare en etikett i loggen.
export async function behandleTilVedtak(fpsak, sak, { type = 'Førstegangsbehandling', hvem = '', harRett = true, dokumentasjon = 'Godkjent', maks = 10 } = {}) {
    const p = await fpsak(sak);
    const navn = `${hvem ? `${hvem} ` : ''}${sak} ${type}`;
    let r;
    for (let i = 0; i < maks; i++) {
        r = await ventPåBehandling(p, type);
        console.log(`STATUS ${navn}`, JSON.stringify(r.status));
        const ap = r.status.aksjonspunkt;
        if (r.grunn === 'avsluttet') return r;
        if (r.grunn === 'vent') {
            console.log(`FUNN ${navn}: ${r.påVent}`);
            return r;
        }
        if (r.grunn === 'uendret') {
            console.log(`FUNN ${navn}: står på ${r.api?.status ?? (r.status.behandling ?? '').replace(/^.*Behandlingsstatus /, '').trim()} uten åpent aksjonspunkt`);
            return r;
        }
        if (r.grunn === 'fatte') await fattVedtak(p);
        else if (r.grunn === 'godkjenning') {
            await tilGodkjenning(p);
            const b = await fpsak(sak, { bruker: 'Birger Beslutter' });
            await velgBehandling(b, type);
            await godkjennVedtak(b);
        } else if (ap.some((a) => a.startsWith('Vurder faresignalene'))) await vurderFaresignaler(p);
        else if (ap.some((a) => a.startsWith('Vurder om den andre forelderen har rett'))) await bekreftAnnenForelderHarRett(p, undefined, { harRett });
        else if (ap.some((a) => a.startsWith('Kontroller dokumentasjon')) || r.api?.aksjonspunkt.includes('5074')) {
            console.log(`AKSJONSPUNKT ${navn}: Kontroller dokumentasjon (${dokumentasjon})`);
            // 5074 i en revurdering (f.eks. overføring): panelet er lukket, og teksten vises ikke før prosessteget åpnes.
            if (!ap.some((a) => a.startsWith('Kontroller dokumentasjon'))) {
                await p.getByRole('button', { name: 'Uttaksdokumentasjon', exact: true }).first().click().catch(() => {});
                await p.getByRole('radio', { name: dokumentasjon, exact: true }).first().waitFor({ timeout: 5000 }).catch(() => {});
            }
            await kontrollerDokumentasjon(p, { vurdering: dokumentasjon });
        } else if (r.api?.aksjonspunkt.includes('5016')) {
            // Fatter vedtak: saken står hos beslutter (f.eks. etter en avbrutt kjøring der «Til godkjenning» er klikket).
            const b = await fpsak(sak, { bruker: 'Birger Beslutter' });
            await velgBehandling(b, type);
            await godkjennVedtak(b);
        } else if (r.api?.aksjonspunkt.includes('5015') && i < maks - 1) {
            // Foreslå vedtak: vedtakspanelet åpnes ikke alltid av seg selv.
            await p.getByRole('button', { name: 'Vedtak', exact: true }).first().click().catch(() => {});
            await p.waitForTimeout(1500);
            if (await p.getByRole('button', { name: /Til godkjenning|Fatt vedtak/ }).count()) continue;
            console.log(`FUNN ${navn}: «Foreslå vedtak» er åpent, men verken «Fatt vedtak» eller «Til godkjenning» vises`);
            return r;
        } else {
            console.log(`FUNN ${navn}: åpent aksjonspunkt ${r.api?.aksjonspunkt.join(', ')} løses ikke automatisk. På siden: ${ap.join(' | ')}`);
            return r;
        }
        await lastPåNytt(p);
    }
    console.log(`FUNN ${navn}: ikke avsluttet etter ${maks} runder`);
    return r;
}

export async function uttak(p) {
    await p.getByRole('button', { name: 'Uttak', exact: true }).first().click().catch(() => {});
    await p.getByText('Disponible stønadsdager').first().waitFor({ timeout: 10000 }).catch(() => {});
    const t = await kropp(p);
    const i = t.indexOf('Disponible stønadsdager');
    return i >= 0 ? t.slice(i, t.indexOf('FAKTA OM', i)) : t.slice(0, 1500);
}

// Aksjonspunktet «Vurder om den andre forelderen har rett» (kommer når far søker før mor).
// harRett: false svarer «Nei» på alle spørsmålene i panelet (rett i Norge, EØS, uføretrygd). Nye spørsmål kan dukke opp etter hvert svar.
export async function bekreftAnnenForelderHarRett(p, begrunnelse = 'Annen forelder har rett.', { harRett = true } = {}) {
    if (harRett) {
        const ja = p.getByRole('radio', { name: 'Ja', exact: true });
        await ja.nth((await ja.count()) - 1).check();
    } else {
        for (let i = 0; i < 6; i++) {
            const nei = p.getByRole('radio', { name: 'Nei', exact: true });
            let besvart = false;
            for (let j = 0; j < (await nei.count()); j++) {
                if (!(await nei.nth(j).isChecked())) {
                    await nei.nth(j).check();
                    await p.waitForTimeout(500);
                    besvart = true;
                    break;
                }
            }
            if (!besvart) break;
        }
    }
    await p.getByRole('textbox').last().fill(harRett ? begrunnelse : 'Annen forelder har ikke rett.');
    const bekreftKnapp = p.getByRole('button', { name: 'Bekreft og fortsett' });
    await bekreftKnapp.click();
    await ventEtterBekreft(p, bekreftKnapp);
}

// Aksjonspunktet «Vurder faresignalene» (5095). Standard: faresignalene vurderes ikke som reelle.
// Panelet «Faresignaler oppdaget» er et ExpansionCard. Vi åpner det bare når skjemaet ikke vises.
export async function vurderFaresignaler(p, { reelle = false, underkategori, begrunnelse = 'Faresignalene er vurdert i lokal test.' } = {}) {
    const resultat = p.getByText('Resultat av vurderingen');
    await resultat.waitFor({ timeout: 5000 }).catch(() => {});
    if (!(await resultat.isVisible())) await p.getByText('Faresignaler oppdaget').first().click();
    await resultat.waitFor({ timeout: 10000 });
    await p.getByRole('textbox', { name: 'Vurdering' }).fill(begrunnelse);
    await p.getByText(reelle ? 'Faresignalene vurderes som reelle' : 'Faresignalene vurderes ikke som reelle', { exact: true }).click();
    if (reelle) await p.getByText(underkategori ?? /innvilget med redusert beregningsgrunnlag/).first().click();
    const bekreftKnapp = p.getByRole('button', { name: 'Bekreft og fortsett' });
    await bekreftKnapp.click();
    await ventEtterBekreft(p, bekreftKnapp);
}

// Aksjonspunktet «Kontroller dokumentasjon» (uttaksdokumentasjon, f.eks. aktivitetskrav). Vurderer alle perioder som står «Til vurdering».
// vurdering: 'Godkjent' | 'Ikke godkjent' | 'Mangler dokumentasjon'.
export async function kontrollerDokumentasjon(p, { vurdering = 'Godkjent', begrunnelse = 'Vurdert i lokal test.' } = {}) {
    for (let i = 0; i < 10; i++) {
        const valg = p.getByRole('radio', { name: vurdering, exact: true });
        if (!(await valg.count())) {
            const rad = p.getByText('Til vurdering').first();
            if (!(await rad.count())) break;
            await rad.click();
            await p.waitForTimeout(500);
        }
        await p.getByRole('radio', { name: vurdering, exact: true }).first().check();
        await p.getByRole('textbox', { name: 'Begrunnelse' }).first().fill(begrunnelse);
        await p.getByRole('button', { name: 'Oppdater' }).first().click();
        await p.getByRole('button', { name: 'Oppdater' }).first().waitFor({ state: 'hidden', timeout: 3000 }).catch(() => {});
        if (!(await p.getByText('Til vurdering').count())) break;
    }
    const bekreftKnapp = p.getByRole('button', { name: 'Bekreft og fortsett' }).first();
    await bekreftKnapp.click();
    await ventEtterBekreft(p, bekreftKnapp);
    await p.getByText('Kontroller dokumentasjon').waitFor({ state: 'hidden', timeout: 10000 }).catch(() => {});
}

// Uttak-aksjonspunkt for valgt periode, f.eks. «Ikke gyldig grunn for uttak av denne stønadskontoen».
// opt: { konto: 'Foreldrepenger', oppfylt: true, årsak: /dager uten aktivitetskrav/ (regex på teksten), utbetalingsgrad: '100', begrunnelse }
// Panelet åpnes av seg selv når saken har uttak-aksjonspunkt. Et ekstra klikk på «Uttak» lukker det, så vi klikker bare når det er lukket.
export async function løsUttakPeriode(p, opt = {}) {
    const { konto, oppfylt = true, årsak, utbetalingsgrad, begrunnelse = 'Vurdert i lokal test.' } = opt;
    const fastsett = p.getByText('Fastsett resultat for perioden');
    await fastsett.waitFor({ timeout: 8000 }).catch(() => {});
    if (!(await fastsett.isVisible())) await p.getByRole('button', { name: 'Uttak', exact: true }).first().click();
    await fastsett.waitFor({ timeout: 15000 });
    if (konto) await p.getByRole('combobox', { name: 'Stønadskontotype' }).selectOption({ label: konto });
    await p.getByRole('radio', { name: oppfylt ? 'Oppfylt' : 'Ikke oppfylt', exact: true }).check();
    if (årsak) {
        const valg = p.getByRole('combobox', { name: oppfylt ? 'Årsak til innvilgelse' : 'Årsak til avslag' });
        const verdi = await valg.evaluate((e, kilde) => [...e.options].find((o) => new RegExp(kilde).test(o.text))?.value, årsak.source ?? årsak);
        if (!verdi) throw new Error(`Fant ingen årsak som matcher ${årsak}`);
        await valg.selectOption(verdi);
    }
    if (utbetalingsgrad) await p.getByRole('textbox', { name: 'Utbetalingsgrad (%)' }).fill(utbetalingsgrad);
    await p.getByRole('textbox', { name: 'Vurdering' }).fill(begrunnelse);
    await p.getByRole('button', { name: 'Oppdater' }).click();
    await p.waitForTimeout(1000);
    const bekreftKnapp = p.getByRole('button', { name: 'Bekreft og fortsett' });
    await bekreftKnapp.click();
    await ventEtterBekreft(p, bekreftKnapp);
}

// Pinnene i tidslinjen i uttakssteget (fødsel, søknad, «Startpunkt for revurdering» osv.). Popoveren finnes bare når
// pinnen er åpen, så hver pinne klikkes. Gir f.eks. ['Startpunkt for revurdering 12.04.2027', 'Fødsel 10.11.2026'].
export async function tidslinjePinner(p) {
    const pinner = p.locator('button[data-timeline-pin]');
    const res = [];
    for (let i = 0; i < (await pinner.count()); i++) {
        await pinner.nth(i).click();
        const popover = p.locator('[data-timeline-popover]').first();
        await popover.waitFor({ timeout: 3000 }).catch(() => {});
        const tekst = (await popover.innerText().catch(() => '')) || (await pinner.nth(i).getAttribute('aria-label')) || '?';
        res.push(tekst.replace(/\s*\n\s*/g, ' '));
        await p.keyboard.press('Escape');
        await p.waitForTimeout(200);
    }
    return res;
}

// Periodene i tidslinjen i uttakssteget, f.eks. «Suksess fra 19.10.2026 til 06.11.2026» (Suksess = innvilget, Feil = avslått).
// Med detaljer klikkes hver periode, og konto, trekk og årsak tas med.
export async function uttakPerioder(p, { detaljer = false } = {}) {
    const tittel = p.getByText('Disponible stønadsdager').first();
    if (!(await tittel.isVisible().catch(() => false))) await p.getByRole('button', { name: 'Uttak', exact: true }).first().click();
    await tittel.waitFor({ timeout: 15000 });
    const knapper = p.locator('button[aria-label*=" fra "][aria-label*=" til "]');
    const res = [];
    for (let i = 0; i < (await knapper.count()); i++) {
        let linje = await knapper.nth(i).getAttribute('aria-label');
        if (detaljer) {
            await knapper.nth(i).click();
            await p.waitForTimeout(600);
            const t = await tekstlinjer(p.locator('body'));
            const resultat = t.find((s) => /^(Innvilget|Ikke oppfylt):/.test(s)) ?? '';
            const tabell = p.locator('table').filter({ hasText: 'Trekk antall uker/dager' }).first();
            const rader = (await tabell.count()) ? await tabell.locator('tbody tr').evaluateAll((r) => r.map((x) => [...x.querySelectorAll('td')].map((td) => (td.querySelector('input,select')?.value ?? td.innerText).trim()).join(' | '))) : [];
            linje += ` [${resultat}; aktivitet: ${rader.join(' ; ')}]`;
        }
        res.push(linje);
    }
    return res;
}

// Overstyrer resultatet for perioder i uttakssteget («Rediger uttaksperioder», blyantikonet). Behandlingen må være åpen.
// perioder: [{ fom: 'dd.mm.åååå' (start i tidslinjen), oppfylt: false, årsak: /regex/, uker, dager, utbetalingsgrad, konto }]
// Uten uker/dager beholdes trekket fpsak har foreslått (hele perioden). Avslag får utbetalingsgrad 0 hvis ikke annet er gitt.
export async function overstyrUttak(p, perioder, begrunnelse = 'Overstyrt i lokal test.') {
    const rediger = p.getByRole('button', { name: 'Rediger uttaksperioder' });
    if (!(await rediger.isVisible().catch(() => false))) await p.getByRole('button', { name: 'Uttak', exact: true }).first().click();
    await rediger.waitFor({ timeout: 15000 });
    await rediger.click();
    await p.waitForTimeout(800);
    for (const e of perioder) {
        await p.locator(`button[aria-label*="fra ${e.fom} "]`).first().click();
        await p.waitForTimeout(800);
        if (e.konto) await p.getByRole('combobox', { name: 'Stønadskontotype' }).selectOption({ label: e.konto });
        await p.getByRole('radio', { name: e.oppfylt === false ? 'Ikke oppfylt' : 'Oppfylt', exact: true }).check();
        await p.waitForTimeout(400);
        const valg = p.getByRole('combobox', { name: e.oppfylt === false ? 'Årsak til avslag' : 'Årsak til innvilgelse' });
        const tekster = await valg.evaluate((s) => [...s.options].map((o) => o.text).filter(Boolean));
        const kilde = e.årsak?.source ?? e.årsak;
        const treff = kilde ? tekster.find((t) => new RegExp(kilde).test(t)) : tekster[0];
        if (!treff) throw new Error(`Fant ingen årsak som matcher ${e.årsak}. Valg: ${tekster.join(' | ')}`);
        await valg.selectOption({ label: treff });
        if (e.uker !== undefined) await p.locator('input[name="aktiviteter.0.weeks"]').fill(String(e.uker));
        if (e.dager !== undefined) await p.locator('input[name="aktiviteter.0.days"]').fill(String(e.dager));
        const grad = e.utbetalingsgrad ?? (e.oppfylt === false ? '0' : undefined);
        if (grad !== undefined) await p.locator('input[name="aktiviteter.0.utbetalingsgrad"]').fill(String(grad));
        await p.getByRole('textbox', { name: 'Vurdering' }).fill(begrunnelse);
        await p.getByRole('button', { name: 'Oppdater' }).click();
        await p.waitForTimeout(1000);
        console.log(`UTTAK OVERSTYRT ${e.fom}: ${e.oppfylt === false ? 'ikke oppfylt' : 'oppfylt'}, ${treff}`);
    }
    await p.getByRole('button', { name: 'Bekreft og fortsett' }).first().click();
    await p.waitForTimeout(5000);
}

// Saksbehandler sender til beslutter (kommer etter manuelle aksjonspunkt, f.eks. løsUttakPeriode).
export async function tilGodkjenning(p) {
    const knapp = p.getByRole('button', { name: 'Til godkjenning' });
    await knapp.click();
    await ventEtterBekreft(p, knapp);
}

// Beslutter godkjenner alle punktene i totrinnskontrollen og vedtaket. `p` må være logget inn som en annen bruker
// enn saksbehandleren: fpsak(sak, { bruker: 'Birger Beslutter' }).
export async function godkjennVedtak(p) {
    const panel = p.getByRole('tabpanel', { name: 'Godkjenning' });
    await panel.waitFor({ timeout: 15000 });
    const godkjent = panel.getByRole('radio', { name: 'Godkjent', exact: true });
    for (let i = 0; i < (await godkjent.count()); i++) await godkjent.nth(i).check();
    const knapp = panel.getByRole('button', { name: 'Godkjenn vedtaket' });
    await knapp.click();
    await ventEtterBekreft(p, knapp);
}

// Behandlingsmeny > Opprett ny behandling > Revurdering. årsak er teksten i «Behandlingsårsak».
// fpsak oppretter revurderingen asynkront, så vi laster siden på nytt og velger den.
export async function opprettRevurdering(p, årsak = 'Nye opplysninger om uttak') {
    await p.getByRole('button', { name: 'Behandlingsmeny', exact: true }).click();
    await p.getByRole('button', { name: 'Opprett ny behandling', exact: true }).click();
    const dialog = p.getByRole('dialog');
    await dialog.getByRole('combobox', { name: 'Behandlingstype' }).selectOption({ label: 'Revurdering' });
    await dialog.getByRole('combobox', { name: 'Behandlingsårsak' }).selectOption({ label: årsak });
    await dialog.getByRole('button', { name: 'OK', exact: true }).click();
    await p.waitForTimeout(6000);
    await p.reload({ waitUntil: 'networkidle' });
    await p.waitForTimeout(1500);
    await velgBehandling(p, 'Revurdering');
}

// Samler feilmeldinger fra fpsak når en prosesstask har feilet (behandling/status svarer HTTP 418). Kall før navigering.
// Returnerer en funksjon som gir de unike feilene så langt: årsak og de første no.nav-rammene etter siste «Caused by».
export function fangProsessFeil(p) {
    const feil = new Set();
    p.on('response', async (r) => {
        if (r.status() !== 418 || !r.url().includes('/behandling/status')) return;
        const tekst = await r.text().catch(() => '');
        let j;
        try {
            j = JSON.parse(tekst);
            if (typeof j.message === 'string') j = JSON.parse(j.message);
        } catch {
            feil.add(tekst.slice(0, 800));
            return;
        }
        const linjer = (j.stacktrace ?? '').split('\n');
        const årsak = linjer.findLastIndex((l) => l.startsWith('Caused by'));
        const rammer = linjer.slice(Math.max(årsak, 0)).filter((l) => /^\s*at no\.nav\./.test(l)).slice(0, 6).map((l) => l.trim());
        feil.add([`${j.exceptionCauseClass}: ${j.exceptionCauseMessage}`, j.feilmelding, ...rammer].filter(Boolean).join('\n    '));
    });
    return () => [...feil];
}

// Faktapanelet «Saken»: «Endre startdato for foreldrepenger» (aksjonspunkt 6045). dato er dd.mm.åååå. Behandlingen må være åpen.
// Returnerer feilmeldinger som vises etter «Overstyr» (tom liste når ingen).
export async function overstyrStartdato(p, dato, begrunnelse = 'Startdato overstyrt i lokal test.') {
    const endre = p.getByRole('button', { name: 'Endre startdato for foreldrepenger' });
    if (!(await endre.isVisible().catch(() => false))) await p.getByRole('button', { name: 'Saken', exact: true }).last().click();
    await endre.waitFor({ timeout: 15000 });
    console.log('STARTDATO FØR', (await tekstlinjer(p.locator('main,body').first())).find((t) => /^[A-ZÆØÅ][a-zæøå]+dag \d+\. /.test(t)) ?? '?');
    await endre.click();
    await p.getByLabel('Startdato som skal benyttes (fra søknad eller fødsel)').fill(dato);
    await p.keyboard.press('Tab');
    await p.getByRole('textbox', { name: 'Begrunnelse' }).last().fill(begrunnelse);
    await p.getByRole('button', { name: 'Overstyr', exact: true }).click();
    await p.waitForTimeout(5000);
    return (await p.locator('.navds-alert,.aksel-alert,.navds-error-message,.aksel-error-message').allInnerTexts()).map((t) => t.trim()).filter(Boolean);
}

// Fakta om uttak: «Rediger fakta om uttak» (overstyring) i en åpen behandling. Viser bare perioder til søkeren i saken.
export async function åpneFaktaUttak(p) {
    const tittel = p.getByRole('heading', { name: 'Fakta om uttak' });
    if (!(await tittel.isVisible().catch(() => false))) {
        // Faktapanelet «Uttak» er nr. 2 av knappene med navnet (den første er prosessteget).
        await p.getByRole('button', { name: 'Uttak', exact: true }).last().click();
    }
    await tittel.waitFor({ timeout: 15000 });
}

// Periodene i «Fakta om uttak» som tekst, én rad per periode: «fom - tom / dager / type / kilde».
export async function faktaUttakPerioder(p) {
    const rader = p.locator('table').filter({ hasText: 'Kilde' }).first().locator('tbody tr');
    const res = [];
    for (let i = 0; i < (await rader.count()); i++) {
        const t = (await rader.nth(i).innerText()).split(/[\n\t]/).map((s) => s.trim()).filter(Boolean).join(' / ');
        if (/^\d{2}\.\d{2}\.\d{4} - /.test(t)) res.push(t);
    }
    return res;
}

// Overstyrer «Fakta om uttak»: «Rediger fakta om uttak», endrer perioder og bekrefter. Behandlingen må være åpen.
// Har panelet et åpent aksjonspunkt (f.eks. «Første periode starter ikke på avklart startdato»), er det redigerbart uten blyantikonet.
// endringer: [{ endre: 'dd.mm.åååå' (fom i raden), fom, tom }, { slett: 'dd.mm.åååå' }, { ny: true, fom, tom, konto: 'Fellesperioden' }]
// konto er teksten i «Stønadskonto»: Fedrekvoten, Fellesperioden, Foreldrepenger, Foreldrepenger før fødsel, Mødrekvoten.
export async function overstyrFaktaUttak(p, endringer, begrunnelse = 'Overstyrt i lokal test.') {
    await åpneFaktaUttak(p);
    const panel = p.locator('section,div').filter({ has: p.getByRole('heading', { name: 'Fakta om uttak' }) }).filter({ has: p.locator('table') }).last();
    const rediger = panel.getByRole('button', { name: 'Rediger fakta om uttak' });
    if (await rediger.isVisible().catch(() => false)) await rediger.click();
    await p.waitForTimeout(500);
    const datoer = async (fom, tom) => {
        if (fom) await panel.getByLabel('Periode fra').fill(fom);
        if (tom) await panel.getByLabel('Periode til').fill(tom);
        await p.keyboard.press('Tab');
        await p.waitForTimeout(300);
    };
    const oppdater = async () => {
        await panel.getByRole('button', { name: 'Oppdater', exact: true }).click();
        await p.waitForTimeout(800);
        if (await panel.getByRole('button', { name: 'Oppdater', exact: true }).count()) {
            const feil = (await panel.locator('.navds-error-message,.aksel-error-message').allInnerTexts()).filter(Boolean);
            throw new Error(`Perioden ble ikke oppdatert: ${feil.join(' | ')}`);
        }
    };
    const rad = (fom) => panel.locator('tbody tr').filter({ hasText: new RegExp(`^\\s*${fom.replace(/\./g, '\\.')} - `) }).first();
    for (const e of endringer) {
        if (e.ny) {
            await panel.getByRole('button', { name: 'Legg til periode' }).click();
            await p.waitForTimeout(500);
            await datoer(e.fom, e.tom);
            await panel.locator(`input[name=arsakstype][value=${e.type ?? 'UTTAK'}]`).check();
            await p.waitForTimeout(300);
            if (e.konto) await panel.getByRole('combobox', { name: 'Stønadskonto' }).selectOption({ label: e.konto });
            await oppdater();
        } else {
            const fra = e.endre ?? e.slett;
            await rad(fra).locator('button').first().click();
            await p.waitForTimeout(600);
            if (e.slett) {
                await panel.getByRole('button', { name: 'Slett periode' }).click();
                await p.waitForTimeout(500);
                // Bekreftelsen «Vil du slette periode?» ligger i en modal.
                await p.getByRole('dialog').getByRole('button', { name: /^(Ja|Slett|OK)/ }).first().click();
                await p.waitForTimeout(600);
                continue;
            }
            await datoer(e.fom, e.tom);
            if (e.konto) await panel.getByRole('combobox', { name: 'Stønadskonto' }).selectOption({ label: e.konto });
            await oppdater();
        }
    }
    const feil = (await panel.locator('.navds-error-summary,.aksel-error-summary').allInnerTexts()).filter(Boolean);
    if (feil.length) throw new Error(`Fakta om uttak: ${feil.join(' | ')}`);
    await panel.getByRole('textbox', { name: 'Begrunn endringene' }).fill(begrunnelse);
    await panel.getByRole('button', { name: 'Bekreft og fortsett' }).click();
    await p.waitForTimeout(5000);
}

export async function fattVedtak(p) {
    const bekreftKnapp = p.getByRole('button', { name: 'Fatt vedtak' });
    await bekreftKnapp.click();
    await ventEtterBekreft(p, bekreftKnapp);
}
