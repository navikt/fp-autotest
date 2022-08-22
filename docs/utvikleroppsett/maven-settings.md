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
            <id>github</id>
            <username>DITT_GITHUBBRUKERNAVN_HER</username>
            <password>DITT_KRYPTERT_PAT_FRA_STEG_2_HER</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>github</id>
             <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/navikt/fp-felles</url>
                </repository>
                <repository>
                     <id>confluent</id>
                     <url>https://packages.confluent.io/maven/</url>
                </repository>
            </repositories>
        </profile>
    </profiles>
</settings>
```
