package no.nav.foreldrepenger.generator.familie.generator;

import no.nav.foreldrepenger.vtp.kontrakter.v2.OrganisasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PrivatArbeidsgiver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// TODO: Make it pick from a list. No duplicates!
public class TestOrganisasjoner {
    public static final OrganisasjonDto NAV = new OrganisasjonDto(
            new Orgnummer("889640782"),
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "ARBEIDS- OG VELFERDSETATEN",
                    LocalDate.of(2003, 1, 1),
                    LocalDate.of(2015, 1, 1))
    );

    public static final OrganisasjonDto NAV_OSLO = new OrganisasjonDto(
            new Orgnummer("992257822"),
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER OSLO",
                    LocalDate.of(2003, 1, 1),
                    LocalDate.of(2015, 1, 1))
    );

    public static final OrganisasjonDto NAV_BERGEN = new OrganisasjonDto(
            new Orgnummer("992260432"),
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER BERGEN",
                    LocalDate.of(2003, 1, 1),
                    LocalDate.of(2015, 1, 1))
    );


    public static final OrganisasjonDto NAV_STORD = new OrganisasjonDto(
            new Orgnummer("992260475"),
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER STORD",
                    LocalDate.of(2003, 1, 1),
                    LocalDate.of(2015, 1, 1))
    );


    public static final OrganisasjonDto NYLIG_OPPSTATET = new OrganisasjonDto(
            new Orgnummer("992261048"),
            new OrganisasjonDto.OrganisasjonsdetaljerDto(
                    "NAV FAMILIE- OG PENSJONSYTELSER TROMSØ",
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(2))
    );

    private final List<OrganisasjonDto> ORGANISASJONER = new ArrayList<>();


    public TestOrganisasjoner() {
        ORGANISASJONER.addAll(List.of(NAV, NAV_OSLO, NAV_BERGEN, NAV_STORD));
    }

    public static final PrivatArbeidsgiver PRIVAT_ARBEIDSGIVER = new PrivatArbeidsgiver(UUID.randomUUID());


    private final Random random = new Random();


    public String arbeidsforholdId() {
        return "ARB001-001";
    }

    public OrganisasjonDto tilfeldigOrg() {
        return ORGANISASJONER.remove(random.nextInt(0, ORGANISASJONER.size()));
    }

}
