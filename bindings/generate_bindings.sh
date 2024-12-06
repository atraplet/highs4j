#! /bin/bash

# main
USAGE="\
Usage: generate_bindings path_to_highs_built_source_package path_to_jextract_binary"

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
rm -rf "${HIGHS4J}"/src/main/java/com/ustermetrics/highs4j/bindings/

# generate bindings
$JEXTRACT \
  --include-dir "${HIGHS}"/src \
  --include-dir "${HIGHS}"/build \
  --target-package com.ustermetrics.highs4j.bindings \
  --output "${HIGHS4J}"/src/main/java \
  @"${HIGHS4J}"/bindings/includes.txt "${HIGHS}"/src/interfaces/highs_c_api.h
