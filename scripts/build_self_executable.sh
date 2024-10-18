#!/usr/bin/env bash

set -o pipefail
set -o nounset
set -o errexit

if [[ "$#" != 2 ]] ; then
  echo "ERROR: two arguments: ${0} <source jar file> <target jar file>"
  exit 1
fi
readonly SOURCE_JAR="${1}"
readonly TARGET_JAR="${2}"
echo -e '#!/usr/bin/env bash \nexec java -jar "$0" "$@"' | cat - "${SOURCE_JAR}" > "${TARGET_JAR}"
chmod +x "${TARGET_JAR}"
