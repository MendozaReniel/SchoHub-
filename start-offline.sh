#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"
JAVA_HOME="$ROOT/.tools/jdk-17.0.21+9"
JAR="$ROOT/target/schohub-backend-1.0.0.jar"

if [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "Bundled JDK not found at $JAVA_HOME" >&2
  echo "Edit JAVA_HOME in this script, or install JDK 17." >&2
  exit 1
fi

if [ ! -f "$JAR" ]; then
  echo "Prebuilt jar not found at $JAR" >&2
  echo "Build it once with ./run-backend.sh, or: mvn -DskipTests package" >&2
  exit 1
fi

echo "Starting SchoHub from the prebuilt jar - no Maven, no internet needed."
echo "Open http://localhost:8080 in your browser. Press Ctrl+C to stop."
exec "$JAVA_HOME/bin/java" -jar "$JAR" "$@"
