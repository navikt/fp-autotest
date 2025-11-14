#!/opt/homebrew/bin/bash

set -euo pipefail
IFS=$'\n\t'

target_dir="$1"

echo "$target_dir"

cd "$target_dir" || exit 1

declare -A ecosystem_counts
files=$(fd dependabot.yml --hidden)
for filepath in $files; do
    ecosystems=$(rg -oP 'package-ecosystem:\s*"?\K[^"\s]+' "$filepath")

    while IFS= read -r ecosystem; do
        if [[ -z "${ecosystem_counts[$ecosystem]:-}" ]]; then
            ecosystem_counts["$ecosystem"]=1
        else
            ((ecosystem_counts["$ecosystem"]++))
        fi

        printf "ecosystem: %-15s %s\n" "$ecosystem" "$(realpath "$filepath")"
    done <<<"$ecosystems"
    echo
done

total=0
for count in "${ecosystem_counts[@]}"; do
    ((total += count))
done
echo "Antall: $total"
for ecosystems in "${!ecosystem_counts[@]}"; do
    echo """$ecosystems"": ""${ecosystem_counts[$ecosystems]}"" projekt(er)"
done
echo ""

for folder in $(fd --type directory --max-depth=1); do
    if ! fd --has-results --type file --hidden dependabot.yml "$folder"; then
        echo "Dependabot er ikke satt opp: $folder"
    fi
done
