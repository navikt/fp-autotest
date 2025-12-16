#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

repo_list="$1"

if [[ ! -f "$repo_list" ]]; then
    echo "repo_list er ikke en fil"
    echo "$0 <repo_dir> <repo_list>"
    exit 1
fi


while IFS= read -r repo; do    
    result=$(gh api /repos/"$repo"/pulls?state=open | jq -r '.[].title')
    if [[ -n "$result" ]]; then
        echo "Åpne pull requests for $repo:"
        echo "$result"
        echo
    fi
    
done <"$repo_list"
