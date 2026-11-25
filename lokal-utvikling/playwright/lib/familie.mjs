// Leser familier laget med fp-autotest ManuellSoknad (se scripts/familie.sh). Fødselsnummer skrives aldri ut.
import fs from 'node:fs';
import path from 'node:path';
import { STATE_DIR } from './browser.mjs';

export function familie(navn) {
    const fil = path.join(STATE_DIR, `familie-${navn}.txt`);
    if (!fs.existsSync(fil)) throw new Error(`Fant ikke ${fil}. Lag den med: scripts/familie.sh ${navn} ...`);
    const linjer = fs.readFileSync(fil, 'utf8').split('\n');
    const verdi = (k) => linjer.find((l) => l.startsWith(`${k}=`))?.split('=')[1];
    const fødsel = linjer.find((l) => l.startsWith('barn født '))?.replace('barn født ', '').trim();
    const termin = linjer.find((l) => l.startsWith('termin '))?.split(' ')[1];
    const norsk = (iso) => (iso ? iso.split('-').reverse().join('.') : undefined);
    return { mor: verdi('mor'), far: verdi('far'), medfar: verdi('medfar'), sak: verdi('sak'), fødselsdato: norsk(fødsel), termindato: norsk(termin) };
}
