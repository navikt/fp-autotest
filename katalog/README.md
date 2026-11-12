# Katalog over aksjonspunkt

Katalogen beskriver hvilke testdata som utløser et aksjonspunkt i fpsak, og hvordan en test eller testsak-verktøyet kommer dit. Den brukes av utviklere og KI-agenter som skal lage en sak for manuell testing eller en ny test.

Katalogen viser hvordan du lager en sak som stopper på et aksjonspunkt. Hvilke tester som dekker et aksjonspunkt, står i [AKSJONSPUNKT_MAPPING.md](../AKSJONSPUNKT_MAPPING.md).

## Finn aksjonspunkt etter tema

Utviklere beskriver oftest et skjermbilde eller et tema, ikke en kode.

| Tema og stikkord | Skjermbilde i fpsak | Aksjonspunkt | Velg koden når | Ytelser |
|---|---|---|---|---|
| Termin, terminbekreftelse | Fakta om fødsel | 5001 | søknaden gjelder termin, barnet er ikke født | ES, FP |
| Fødsel, barn mangler i folkeregisteret, flere barn enn registrert | Fakta om fødsel | 5027 | barnet er født, men mangler eller er færre i PDL | ES, FP |
| Adopsjon, omsorgsovertakelse, foreldreansvar | Fakta om omsorgsovertakelse | 5018 | | ES, FP |
| Opptjening, arbeidsforhold, aktiviteter før uttak | Fakta om opptjening | 5051 | | FP, SVP |
| Beregning: fakta om arbeid, frilans, selvstendig næringsdrivende | Fakta om beregning | 5058 | inntekten må avklares (frilans, SN, flere aktiviteter), før grunnlaget fastsettes | FP, SVP |
| Beregning: avvik mellom inntektsmelding og a-inntekt | Beregning | 5038 | over 25 % avvik mellom inntektsmelding og a-inntekt | FP, SVP |
| Beregning: varig endret eller nyoppstartet næring | Beregning | 5039 | SN med varig endret eller nyoppstartet næring | FP, SVP |
| Aleneomsorg | Omsorg og rett | 5060 | søker oppgir aleneomsorg | FP |
| Annen forelders rett, mor har rett, ikke rett | Omsorg og rett | 5086 | søker oppgir at annen forelder har rett eller ikke rett, uten aleneomsorg | FP |
| Uttak: aktivitetskrav, mors aktivitet, utsettelse, overføring, tidlig fedrekvote | Fakta om uttaksdokumentasjon | 5074 | | FP |
| Svangerskapspenger: tilrettelegging, arbeidsforhold | Inngangsvilkår SVP | 5091 | alltid først i SVP | SVP |
| Svangerskapspenger: vilkåret | Svangerskapspenger | 5092 | etter at 5091 er løst | SVP |

## Flyt for førstegangsbehandling

Aksjonspunktene er sortert etter rekkefølgen på behandlingsstegene i `ForeldrepengerModellProducer` i fp-sak.

| Behandlingssteg | Aksjonspunkt |
|---|---|
| SØKERS_RELASJON_TIL_BARN | [5001 Avklar terminbekreftelse](aksjonspunkt/5001.md) |
| SØKERS_RELASJON_TIL_BARN | [5018 Vurder vilkår for adopsjon eller foreldreansvar](aksjonspunkt/5018.md) |
| SØKERS_RELASJON_TIL_BARN | [5027 Sjekk manglende fødsel](aksjonspunkt/5027.md) |
| VURDER_OPPTJENINGSVILKÅR | [5051 Vurder perioder med opptjening](aksjonspunkt/5051.md) |
| KONTROLLER_FAKTA_BEREGNING | [5058 Vurder fakta for arbeidstaker, frilans og selvstendig næringsdrivende](aksjonspunkt/5058.md) |
| FORESLÅ_BEREGNINGSGRUNNLAG | [5038 Fastsett beregningsgrunnlag](aksjonspunkt/5038.md) |
| FORTSETT_FORESLÅ_BEREGNINGSGRUNNLAG | [5039 Vurder varig endret/nyoppstartet næring selvstendig næringsdrivende](aksjonspunkt/5039.md) |
| KONTROLLER_OMSORG_RETT | [5060 Manuell kontroll av aleneomsorg](aksjonspunkt/5060.md) |
| KONTROLLER_OMSORG_RETT | [5086 Avklar annen forelder har rett](aksjonspunkt/5086.md) |
| FAKTA_UTTAK_DOKUMENTASJON | [5074 Vurder uttaksdokumentasjon](aksjonspunkt/5074.md) |

5001, 5018 og 5027 gjelder også engangsstønad.

## Svangerskapspenger

| Behandlingssteg | Aksjonspunkt |
|---|---|
| VURDER_TILRETTELEGGING | [5091 Vurder tilrettelegging svangerskapspenger](aksjonspunkt/5091.md) |
| VURDER_SVANGERSKAPSPENGERVILKÅR | [5092 Avklar svangerskapspengervilkåret](aksjonspunkt/5092.md) |

## Format

Hver fil i `aksjonspunkt/` har frontmatter med kode, navn, behandlingssteg, skjermlenke, ytelser, totrinn, bekreftelsesklasse og `basert_på`: testen «Minste oppskrift» bygger på. Alle tester som dekker koden, står i [AKSJONSPUNKT_MAPPING.md](../AKSJONSPUNKT_MAPPING.md). Deretter følger:

- **Når kommer det:** hva i testdataene som utløser aksjonspunktet.
- **Valg å avklare:** valgene som avgjør hvilken variant saken blir, med standardverdi og CLI-parameter. Agenten spør bare om disse.
- **Minste oppskrift:** de stegene som må til, med metodene fra `src/main`.
- **Standardløsning:** hvordan aksjonspunktet løses om saken skal videre.
- **Fallgruver:** det som ofte går galt.

Navn, steg, skjermlenke og totrinn kommer fra `AksjonspunktDefinisjon` i fp-sak. «Når kommer det» er hentet fra testene og må kontrolleres av noen med fagkunnskap. Oppdater `sist_verifisert` når en oppføring er kontrollert.
