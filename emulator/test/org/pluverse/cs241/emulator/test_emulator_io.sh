#!/usr/bin/env bash

set -o pipefail
set -o nounset
set -o errexit
set -o xtrace

if [[ "$#" != 2 ]] ; then
  echo "${0} <jar file> <mips program>"
  exit 1
fi

readonly JAR="${1}"
readonly MIPS_PROGRAM="${2}"
readonly STDOUT=$(mktemp)
trap EXIT "rm ${STDOUT}"

readonly EXPECTED="test"

java -jar "${JAR}" "${MIPS_PROGRAM}" <<< "${EXPECTED}" > "${STDOUT}"

readonly ACTUAL=$(cat "${STDOUT}")
if [[ "${EXPECTED}" == "${ACTUAL}" ]] ; then
  exit 0
else
  exit 1
fi

