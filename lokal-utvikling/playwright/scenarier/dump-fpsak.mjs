// Tekst og felt i fpsak, for å finne velgere. node run.mjs scenarier/dump-fpsak.mjs <sak> [klikk:Tekst,...]
// KLIKK='Behandlingsmeny,Opprett ny behandling' klikker knapper/lenker i rekkefølge før dump. BEHANDLING=Revurdering velger behandling.
// SETT som i dump-steg.mjs (fyllFelt), etter KLIKK. MAKS=<tegn> begrenser teksten (standard 4000).
import { tekstlinjer, fyllFelt } from '../lib/browser.mjs';
import * as F from '../lib/fpsak.mjs';

export default async ({ fpsak, args }) => {
    const [sak] = args;
    const p = await fpsak(sak);
    if (process.env.BEHANDLING) await F.velgBehandling(p, process.env.BEHANDLING);
    for (const k of (process.env.KLIKK ?? '').split(',').filter(Boolean)) {
        const knapp = p.getByRole('button', { name: k, exact: true }).or(p.getByRole('link', { name: k, exact: true })).or(p.getByRole('menuitem', { name: k, exact: true }));
        await knapp.first().click();
        await p.locator('.navds-loader, .aksel-loader, [aria-busy="true"]').first().waitFor({ state: 'hidden', timeout: 5000 }).catch(() => {});
    }
    if (process.env.SETT) await fyllFelt(p, process.env.SETT);
    const rot = (await p.getByRole('dialog').count()) ? p.getByRole('dialog').last() : p.locator('body');
    console.log('TEKST', (await tekstlinjer(rot)).join(' / ').slice(0, Number(process.env.MAKS ?? 4000)));
    const felt = await rot.locator('input,select,textarea,button').evaluateAll((e) =>
        e.map((x) => `${x.tagName}[${x.name || ''}]${x.type ? `(${x.type})` : ''}${x.labels?.[0] ? ` «${x.labels[0].innerText}»` : ''}${x.tagName === 'BUTTON' ? ` «${x.innerText}»` : ''}${x.tagName === 'SELECT' ? ` {${[...x.options].map((o) => o.text).join('|')}}` : ''}`),
    );
    console.log('FELT', felt.join(' ; '));
};
