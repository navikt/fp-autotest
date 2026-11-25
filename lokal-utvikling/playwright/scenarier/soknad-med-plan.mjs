// Førstegangssøknad der planen endres før innsending. node run.mjs scenarier/soknad-med-plan.mjs <familie> <mor|far> [send]
// SLETT='<radstart>,...' sletter radene (alle delperioder). NY='fom-tom-KONTO[-FORELDER[-OVERFØRINGSÅRSAK]],...' legger til perioder uten å flytte resten.
//   FORELDER: MOR | FAR_MEDMOR | BEGGE (standard søkeren). OVERFØRINGSÅRSAK f.eks. SYKDOM_ANNEN_FORELDER, INSTITUSJONSOPPHOLD_ANNEN_FORELDER.
// OPPSTART=<OppstartValg> på fordelingssteget (far: standard IKKE_VED_FØDSEL). Datoer dd.mm.åååå.
import * as S from '../lib/soknad.mjs';

export default async ({ som, familie, args }) => {
    const [navn, hvem, send] = args;
    const fam = familie(navn);
    const p = await som(`${navn}-${hvem}`, 'soknad', fam[hvem]);
    await S.slettUtkast(p);
    await S.start(p);
    const steg = await S.fyllUt(p, {
        rolle: hvem,
        fødselTermin: fam.fødselsdato,
        termin: fam.fødselsdato ? undefined : fam.termindato,
        annenPart: fam.fødselsdato ? undefined : { fnr: fam[hvem === 'mor' ? 'far' : 'mor'] },
        oppstart: process.env.OPPSTART ?? (hvem === 'far' ? 'IKKE_VED_FØDSEL' : undefined),
        send: send === 'send',
        vedPlan: async (p) => {
            console.log('PLAN FØR', await S.planSomTekst(p), '|', await S.planInfo(p));
            for (const rad of (process.env.SLETT ?? '').split(',').filter(Boolean)) await S.slettPeriode(p, rad, { velg: /./ });
            for (const ny of (process.env.NY ?? '').split(',').filter(Boolean)) {
                const [fom, tom, konto, forelder = hvem === 'mor' ? 'MOR' : 'FAR_MEDMOR', overføringsårsak] = ny.split('-');
                await S.leggTilPeriode(p, { fom, tom, konto, forelder, overføringsårsak, flytt: false });
            }
            console.log('PLAN', await S.planSomTekst(p), '|', await S.planInfo(p));
        },
    });
    if (steg === 'oppsummering') console.log('OPPSUMMERING', await S.oppsummeringPlan(p));
    if (steg === 'oppsummering' || steg === 'kvitttering') return;
    console.log('STOPPET PÅ', steg);
};
