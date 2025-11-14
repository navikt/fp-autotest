#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

repo_dir="$1"
repo_list="$2"

if [[ ! -d "$repo_dir" ]]; then
    echo "repo_dir er ikke en mappe"
    echo "$0 <repo_dir> <repo_list>"
    exit 1
fi

if [[ ! -f "$repo_list" ]]; then
    echo "repo_list er ikke en fil"
    echo "$0 <repo_dir> <repo_list>"
    exit 1
fi

clone_repos() {
    local base_dir="$1"
    local repo_list="$2"
    while IFS= read -r repo; do
        [[ -z "$repo" ]] && continue
        local name
        name=$(basename "$repo" .git)
        echo "Behandler repo: $name"
        local dest="$base_dir/$name"
        if [[ -d "$dest" ]]; then
            echo "Repo $name finnes allerede. Oppdaterer..."
            (
                cd "$dest" || exit 1
                git pull
            ) &
        else
            echo "Cloner $repo til $dest"
            gh repo clone "$repo" "$dest" &
        fi
    done <"$repo_list"
    wait
}

clone_repos "$repo_dir" "$repo_list"
