package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.OrganisasjonDto;

public class TestOrganisasjoner {


    public static final OrganisasjonDto NAV = new OrganisasjonDto(
            "889640782",
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "ARBEIDS- OG VELFERDSETATEN",
                    LocalDate.of(2003, 1, 1))
    );

    public static final OrganisasjonDto NAV_OSLO = new OrganisasjonDto(
            "992257822",
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER OSLO",
                    LocalDate.of(2003, 1, 1))
    );

    public static final OrganisasjonDto NAV_BERGEN = new OrganisasjonDto(
            "992260432",
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER BERGEN",
                    LocalDate.of(2003, 1, 1))
    );


    public static final OrganisasjonDto NAV_STORD = new OrganisasjonDto(
            "992260475",
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER STORD",
                    LocalDate.of(2003, 1, 1))
    );

    public static final OrganisasjonDto NAV_KLAGE_MIDT = new OrganisasjonDto(
            "991078045",
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV KLAGEINSTANS MIDT-NORGE",
                    LocalDate.of(2003, 1, 1))
    );


    public static final OrganisasjonDto NAV_YTELSE_BETALING = new OrganisasjonDto(
            "991013628",
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV UTBETALING YTELSE",
                    LocalDate.of(2003, 1, 1))
    );


    public static final OrganisasjonDto NYLIG_OPPSTATET = new OrganisasjonDto(
            "992261048",
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER TROMSØ",
                    LocalDate.now().minusDays(2))
    );

    private final List<OrganisasjonDto> ORGANISASJONER = new ArrayList<>();


    public TestOrganisasjoner() {
        ORGANISASJONER.addAll(List.of(NAV, NAV_OSLO, NAV_BERGEN, NAV_STORD, NAV_KLAGE_MIDT));
    }


    private final Random random = new Random();


    public String arbeidsforholdId() {
        return "ARB001-001";
    }

    public OrganisasjonDto tilfeldigOrg() {
        return ORGANISASJONER.remove(random.nextInt(0, ORGANISASJONER.size()));
    }

}
