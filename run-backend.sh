#!/usr/bin/env bash
set -e
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export JAVA_HOME="$ROOT/.tools/jdk-17.0.21+9"
export PATH="$JAVA_HOME/bin:$ROOT/.tools/apache-maven-3.9.9/bin:$PATH"
exec mvn -f "$ROOT/pom.xml" spring-boot:run "$@"
