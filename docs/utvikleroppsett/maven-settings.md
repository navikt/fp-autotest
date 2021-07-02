# Mavens "settings.xml"

1. Lag en fil kalt "settings.xml" med XML-en under og lagre denne i "~/.m2".
2. Oppdater med dine verdier for "DITT_GITHUBBRUKERNAVN_HER" og "DITT_GENERERTE_PERSONAL_ACCESS_TOKEN_HER".

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>internal-repo</id>
            <activation>
                <activeByDefault>false</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <id>nexus-internal</id>
                    <name>NAV internal Nexus</name>
                    <url>https://repo.adeo.no/repository/maven-public</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>nexus-internal</id>
                    <name>NAV internal Nexus</name>
                    <url>https://repo.adeo.no/repository/maven-public</url>
                </pluginRepository>
            </pluginRepositories>
        </profile>
        <profile>
            <id>github</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <id>github-tjenestespesifikasjoner</id>
                    <name>GitHub tjenestespesifikasjoner Apache Maven Packages</name>
                    <url>https://maven.pkg.github.com/navikt/tjenestespesifikasjoner</url>
                </repository>
            </repositories>
        </profile>
    </profiles>
    <servers>
        <server>
            <id>github-tjenestespesifikasjoner</id>
            <username>DITT_GITHUBBRUKERNAVN_HER</username>
            <password>DITT_GENERERTE_PERSONAL_ACCESS_TOKEN_HER</password>
        </server>
    </servers>
</settings>
```