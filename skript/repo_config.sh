#!/opt/homebrew/bin/bash

set -euo pipefail
IFS=$'\n\t'

target_dir="$1"
cd "$target_dir" || exit 1

for folder in $(fd --absolute-path --type directory --max-depth=1); do
    if ! fd --has-results --type file --hidden LICENSE "$folder"; then
        echo "Mangler LICENSE: $folder"
    fi
done
echo ""

for folder in $(fd --absolute-path --type directory --max-depth=1); do
    if ! fd --has-results --type file --hidden CODEOWNERS "$folder"; then
        echo "Mangler CODEOWNERS: $folder"
    fi
done
echo ""

for folder in $(fd --absolute-path --type directory --max-depth=1); do
    if ! fd --has-results --type file --hidden README.md "$folder" --max-depth 1; then
        echo "Mangler README: $folder"
    fi
done
echo ""

for path in $(fd --absolute-path --max-depth 2 "README.md"); do

    message=""
    if ! rg --ignore-case --quiet "Spørsmål knyttet til" "$path"; then
        message+="- Mangler ekstern kontaktinfo\n"
    fi
    if ! rg --ignore-case --quiet "Interne henvendelser" "$path"; then
        message+="- Mangler intern kontaktinfo\n"
    fi

    if ! rg --ignore-case --quiet --fixed-strings "https://nav-it.slack.com/archives" "$path"; then
        message+="- Mangler klikkbar slack-lenke\n"
    fi

    if [[ -n "$message" ]]; then
        printf "%s\n" "$path"
        printf "%b\n" "$message"
    fi

done
