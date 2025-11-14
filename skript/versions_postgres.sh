#!/opt/homebrew/bin/bash

set -euo pipefail
IFS=$'\n\t'

target_dir="$1"
cd "$target_dir" || exit 1
declare -A version_counts

files=$(fd -H  --extension yml --extension yaml --extension json '' "$target_dir")
for filepath in $files; do
    version=$(rg -oP 'POSTGRES_\K[0-9]+' "$filepath" || echo "")

    if [[ -z "$version" ]]; then continue; fi
    if [[ -z "${version_counts[$version]:-}" ]]; then
        version_counts["$version"]=1
    else
        ((version_counts["$version"]++))
    fi
    echo "version: $version, path: $(realpath "$filepath")"
done

total=0
for count in "${version_counts[@]}"; do
    ((total += count))
done

echo
echo "Totalt: $total"
for version in "${!version_counts[@]}"; do
    echo "Postgres ""$version"": ""${version_counts[$version]}"""
done | sort --reverse
