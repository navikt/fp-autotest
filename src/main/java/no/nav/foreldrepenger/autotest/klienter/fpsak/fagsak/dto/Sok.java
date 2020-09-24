package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Sok {

    private String searchString;

    public Sok(@JsonProperty("søketekst") String søketekst) {
        super();
        this.searchString = søketekst;
    }

    public String getSearchString() {
        return searchString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sok sok = (Sok) o;
        return Objects.equals(searchString, sok.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchString);
    }

    @Override
    public String toString() {
        return "Sok{" +
                "searchString='" + searchString + '\'' +
                '}';
    }
}
