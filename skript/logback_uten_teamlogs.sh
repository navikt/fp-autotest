#!/opt/homebrew/bin/bash

set -euo pipefail
IFS=$'\n\t'

target_dir="$1"
cd "$target_dir" || exit 1

files=$(fd logback.xml "$target_dir")

for filepath in $files; do
    if ! grep -q "secureLogger" "$filepath"; then
        continue
    fi

    if ! grep -q "teamLogger" "$filepath" || ! grep -q 'appender-ref ref="teamLogger"' "$filepath"; then
        echo "Logger til secureLogger men ikke til teamLogger: $filepath"
        continue
    fi
done
