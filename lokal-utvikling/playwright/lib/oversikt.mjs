// Foreldrepengeoversikt: les status og plan for en sak.
import { tekstlinjer } from './browser.mjs';

export async function forside(p) {
    return (await tekstlinjer(p.locator('#app,#maincontent,main').first())).join(' / ');
}

// Åpner første sak (eller saken med saksnummer) og returnerer status og plan.
export async function sak(p, saksnummer) {
    if (saksnummer) await p.goto(`${new URL(p.url()).origin}/foreldrepenger/oversikt/sak/${saksnummer}`, { waitUntil: 'networkidle' });
    else await p.locator('a').filter({ hasText: 'Foreldrepenger' }).first().click();
    await p.waitForLoadState('networkidle');
    await p.waitForTimeout(3000);
    const t = await tekstlinjer(p.locator('#app,#maincontent,main').first());
    const fra = t.findIndex((s) => s === 'Din plan' || s === 'Dette har du søkt om');
    const til = t.findIndex((s, i) => i > fra && /lagt til i planen|Snarveier/.test(s));
    return {
        url: p.url(),
        status: t[t.indexOf('Din sak') + 1],
        topp: t.slice(0, Math.max(0, t.indexOf('Dette skjer i saken'))).join(' / '),
        hendelser: t.slice(t.indexOf('Dette skjer i saken') + 1, t.indexOf('Vis hele prosessen')).join(' / '),
        plan: fra >= 0 ? t.slice(fra + 1, til + 1).join(' / ') : '(ingen plan)',
    };
}
