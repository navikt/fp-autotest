# Mavens "settings.xml"

1. Sett opp settings-security.xml

    * Kjør ```mvn --encrypt-master-password etvalgfrittpassord```
    * Under mappen "~/.m2" opprett en fil med navnet "settings-security.xml" med innholder:

        ```
        <settingsSecurity>
            <master>string som ble returnert fra kommando over</master>
        </settingsSecurity>
        ```
2.  Krypter din PAT (personal access token fra tidligere):

    ```mvn --encrypt-password DITT_GENERERTE_PERSONAL_ACCESS_TOKEN_HER```
3. Lag en fil kalt "settings.xml" med XML-en under og lagre denne i "~/.m2".

    * Oppdater med dine verdier for "DITT_GITHUBBRUKERNAVN_HER" og "DITT_KRYPTERT_PAT_FRA_STEG_2_HER.

```xml
<settings>
    <servers>
        <server>
            <id>github-package-registry-navikt</id>
            <username>DITT_GITHUBBRUKERNAVN_HER</username>
            <password>DITT_KRYPTERT_PAT_FRA_STEG_2_HER</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>default</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>github-package-registry-navikt</id>
                    <url>https://maven.pkg.github.com/navikt/maven-release</url>
                </repository>
                <repository>
                    <id>confluent</id>
                    <url>https://packages.confluent.io/maven/</url>
                </repository>
            </repositories>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>default</activeProfile>
    </activeProfiles>
</settings>
```
