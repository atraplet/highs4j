#! /bin/bash

# main
USAGE="\
Usage: generate_bindings path_to_highs_source_package path_to_jextract_binary"

# read command line arguments
if [ $# -eq 2 ]; then
  HIGHS4J=$(dirname "${0}")/../
  HIGHS="${1}"
  JEXTRACT="${2}"
else
  echo "$USAGE"
  exit 1
fi

# remove old bindings
rm -rf "${HIGHS4J}"/src/main/java/com/ustermetrics/HIGHS4J/bindings/

# generate bindings
$JEXTRACT \
  --define-macro DLONG \
  --define-macro LDL_LONG \
  --define-macro SuiteSparse_long="long long" \
  --define-macro SuiteSparse_long_max=9223372036854775801 \
  --define-macro SuiteSparse_long_idd="lld" \
  --include-dir "${HIGHS}"/external/SuiteSparse_config \
  --target-package com.ustermetrics.HIGHS4J.bindings \
  --output "${HIGHS4J}"/src/main/java \
  @"${HIGHS4J}"/bindings/includes.txt "${HIGHS}"/include/highs.h
