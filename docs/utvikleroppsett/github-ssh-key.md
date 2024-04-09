# Oppsett for SSH-nøkler mot Github

1. Hvis du ikke har generert private key på maskinen din fra før, kjør kommandoen `ssh-keygen`.
2. Gå inn på Githubsiden for å [legge til SSH-nøkkel på din Githubbruker](https://github.com/settings/keys).
3. Trykk på "New SSH key".
4. Legg inn "Title", f.eks. "NAV".
5. Kopier innholdet av filen "~/.ssh/id_rsa.pub" og lim inn i "Key".
6. Velg "Add SSH key".
7. Trykk på "Enable SSO", "Authorize" (NAV) og følg anvisningene videre for å godkjenne token gjennom SSO.
8. Configurer git til å bruke nøkkelen din.
   ```bash
   git config --global gpg.format ssh
   git config --global user.signingkey ~/.ssh/id_rsa.pub 
   ```
