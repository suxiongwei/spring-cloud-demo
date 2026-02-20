$ErrorActionPreference = "Stop"

# Keep local and CI validation command identical.
mvn -q -DskipTests validate
