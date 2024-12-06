#! /bin/bash

# main
USAGE="\
Usage: generate_includes path_to_highs_source_package path_to_jextract_binary"

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
  --include-dir "${HIGHS}"/external/SuiteSparse_config \
  --dump-includes "${TMP_INCLUDES}" \
  "${HIGHS}"/include/highs.h

# select symbols
grep "highs\|fflush" "${TMP_INCLUDES}" | grep -v "SuiteSparse\|timer\|fflush_nolock\|fflush_unlocked" >"${INCLUDES}"
rm -f "${TMP_INCLUDES}"
