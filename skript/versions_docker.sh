#!/opt/homebrew/bin/bash

set -euo pipefail
IFS=$'\n\t'

target_dir="$1"
cd "$target_dir" || exit 1
declare -A version_counts

files=$(fd Dockerfile "$target_dir")
for filepath in $files; do
    while IFS= read -r line; do
        version=$(echo "$line" | awk '{print $2}' | sed 's/[[:space:]]\+AS[[:space:]]\+.*$//')

        if [[ -z "$version" ]]; then continue; fi
        if [[ -z "${version_counts[$version]:-}" ]]; then
            version_counts["$version"]=1
        else
            ((version_counts["$version"]++))
        fi
        echo "version: $version, path: $(realpath "$filepath")" 
    done < <(rg ^FROM "$filepath" || echo "")
done

total=0
for count in "${version_counts[@]}"; do
    ((total += count))
done

echo
echo "Totalt: $total"
for line in "${!version_counts[@]}"; do
    echo "Docker image: ""$line"": ""${version_counts[$line]}"""
done | sort --reverse
