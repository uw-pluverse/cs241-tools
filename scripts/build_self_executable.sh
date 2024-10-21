#!/usr/bin/env bash

set -o pipefail
set -o nounset
set -o errexit

bazel build //emulator/src/org/pluverse/cs241/emulator:emulator_deploy.jar

readonly SOURCE_JAR="../bazel-bin/emulator/src/org/pluverse/cs241/emulator/emulator_deploy.jar"

readonly TARGET_JAR="pluverse_emulator"
echo -e '#!/usr/bin/env bash \nexec java -jar "$0" "$@"' | cat - "${SOURCE_JAR}" > "${TARGET_JAR}"
chmod +x "${TARGET_JAR}"
