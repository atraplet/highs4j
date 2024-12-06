#! /bin/bash

# main
USAGE="\
Usage: generate_includes path_to_highs_built_source_package path_to_jextract_binary"

# read command line arguments
if [ $# -eq 2 ]; then
  HIGHS4J=$(dirname "${0}")/../
  HIGHS="${1}"
  JEXTRACT="${2}"
else
  echo "$USAGE"
  exit 1
fi

# define variables
TMP_INCLUDES="${HIGHS4J}"/bindings/tmp_includes.txt
INCLUDES="${HIGHS4J}"/bindings/includes.txt

# dump included symbols
rm -f "${TMP_INCLUDES}"
rm -f "${INCLUDES}"
${JEXTRACT} \
  --include-dir "${HIGHS}"/src \
  --include-dir "${HIGHS}"/build \
  --dump-includes "${TMP_INCLUDES}" \
  "${HIGHS}"/src/interfaces/highs_c_api.h

# select symbols
grep "highs" "${TMP_INCLUDES}" >"${INCLUDES}"
rm -f "${TMP_INCLUDES}"
