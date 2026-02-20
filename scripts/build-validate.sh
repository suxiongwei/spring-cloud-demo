#!/usr/bin/env bash
set -euo pipefail

# Keep local and CI validation command identical.
mvn -q -DskipTests validate
