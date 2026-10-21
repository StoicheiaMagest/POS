#!/usr/bin/env bash
# Commit the given files with the given message and push to pos-system.
# Usage: scripts/commit-push.sh "<commit message>" <file> [file...]
set -euo pipefail

if [ $# -lt 2 ]; then
    echo "Usage: $0 \"<commit message>\" <file> [file...]" >&2
    exit 1
fi

BRANCH="$(git branch --show-current)"
if [ "$BRANCH" != "pos-system" ]; then
    echo "Refusing to commit: on branch '$BRANCH', only 'pos-system' is allowed." >&2
    exit 1
fi

MESSAGE="$1"
shift

# Definition of done: full suite green before any commit (AGENTS.md §5).
mvn -q test > /tmp/commit-push-mvn.log 2>&1 || {
    echo "Aborting: mvn test failed. Last lines:" >&2
    tail -25 /tmp/commit-push-mvn.log >&2
    exit 1
}

git add -- "$@"
git commit -m "$MESSAGE"
git push origin pos-system
