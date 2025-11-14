#!/opt/homebrew/bin/bash

set -euo pipefail
IFS=$'\n\t'

target_dir="$1"
cd "$target_dir" || exit 1
declare -A version_counts

files=$(fd pom.xml --max-depth=2 "$target_dir")
for filepath in $files; do
    echo "Processing file: $filepath"
    # Extract parent artifactId and version
    parent_block=$(sed -n '/<parent>/,/<\/parent>/p' "$filepath" 2>/dev/null || echo "")

    if [[ -z "$parent_block" ]]; then continue; fi

    artifact_id=$(echo "$parent_block" | rg -oP '<artifactId>\K(fp-parent-app|fp-parent-lib|fp-bom)(?=</artifactId>)' || echo "")
    version=$(echo "$parent_block" | rg -oP '<version>\K[^<]+(?=</version>)' || echo "")

    if [[ -z "$artifact_id" || -z "$version" ]]; then continue; fi

    key="$artifact_id:$version"
    if [[ -z "${version_counts[$key]:-}" ]]; then
        version_counts["$key"]=1
    else
        ((version_counts["$key"]++))
    fi



    echo "$artifact_id version: $version, path: $(realpath "$filepath")"
done

total=0
for count in "${version_counts[@]}"; do
    ((total += count))
done

echo
echo "Total: $total"
for key in "${!version_counts[@]}"; do
    echo "$key: ${version_counts[$key]}"
done | sort --reverse
