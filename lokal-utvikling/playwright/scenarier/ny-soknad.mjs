// Førstegangssøknad med standardsvar og standardplan.
// node run.mjs scenarier/ny-soknad.mjs <familienavn> <mor|far|medfar> [vite|master]
import * as S from '../lib/soknad.mjs';

export default async ({ som, familie, args }) => {
    const [navn, hvem, modus] = args;
    const fam = familie(navn);
    const farOgFar = !!fam.medfar;
    const annen = hvem === 'medfar' ? 'far' : hvem === 'far' ? (farOgFar ? 'medfar' : 'mor') : 'far';
    if (!fam[hvem] || !fam[annen]) throw new Error(`Familien ${navn} har ikke både ${hvem} og ${annen}`);
    const opt = modus ? { vite: modus === 'vite' } : {};
    const p = await som(`${navn}-${hvem}`, 'soknad', fam[hvem], opt);
    await S.slettUtkast(p);
    await S.start(p);
    // Fra PDL kan søknaden ikke vite hvem av to fedre som er annen part, så fnr fylles inn hvis feltet finnes
    const annenPart = { fnr: fam[annen], fornavn: 'Test', etternavn: annen === 'mor' ? 'Mor' : 'Far' };
    await S.fyllUt(p, {
        rolle: hvem === 'mor' ? 'mor' : 'far',
        termin: fam.fødselsdato ? undefined : fam.termindato,
        fødselTermin: fam.fødselsdato,
        annenPart: fam.fødselsdato && !farOgFar ? undefined : annenPart,
        vedPlan: async (p) => console.log('PLAN', await S.planSomTekst(p), '|', await S.planInfo(p)),
        send: true,
    });
};
