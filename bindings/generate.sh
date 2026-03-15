#! /bin/bash

# Usage
USAGE="\
Usage: generate [--dump-includes]"

# Read command line arguments
if [ $# -eq 0 ] ; then
  DUMP_INCLUDES=false
elif [ $# -eq 1 ] && [ "${1}" = "--dump-includes" ]; then
  DUMP_INCLUDES=true
else
  echo "$USAGE"
  exit 1
fi

# Define project name, version, repo, and header file
PROJECT_NAME=highs4j
VERSION=v1.13.1
REPO="https://github.com/ERGO-Code/HiGHS"
HEADER_FILE=highs/interfaces/highs_c_api.h

# Define variables
ARTIFACT_ID=com.ustermetrics."${PROJECT_NAME}"
ARTIFACT_ID_DIR=$(echo "${ARTIFACT_ID}" | sed "s/\./\//g")

TMP_DIR=$(dirname "$(mktemp -u)")
REPO_DIR="${TMP_DIR}"/"${REPO##*/}"
HEADER_FILE_FULL_PATH="${REPO_DIR}"/"${HEADER_FILE}"

BINDINGS_DIR=$(dirname "$(realpath "${0}")")
PROJECT_ROOT="${BINDINGS_DIR}"/..
PATCHES_DIR="${PROJECT_ROOT}"/patches
INCLUDES_FILE="${BINDINGS_DIR}"/includes.txt
JAVA_SRC_DIR="${PROJECT_ROOT}"/src/main/java

if [[ "$OSTYPE" = "linux-gnu"* ]]; then
  JEXTRACT=jextract
elif [[ "$OSTYPE" = "msys"* ]]; then
  JEXTRACT=jextract.bat
else
  echo "Error: OS ${OSTYPE} not supported"
  exit 1
fi

# Clone and checkout repo
echo "Clone repository ${REPO} into ${REPO_DIR}"
rm -rf "${REPO_DIR}"
cd "${TMP_DIR}" || { echo "Error: Failed to change directory to ${TMP_DIR}"; exit 1; }
git clone "${REPO}" || { echo "Error: Failed to clone repository ${REPO}"; exit 1; }
cd "${REPO_DIR}" || { echo "Error: Failed to change directory to ${REPO_DIR}"; exit 1; }
git checkout "${VERSION}" || { echo "Error: Failed to checkout version ${VERSION}"; exit 1; }

# Apply patches
if [ -d "${PATCHES_DIR}" ]; then
  if ls "${PATCHES_DIR}"/*.patch 1> /dev/null 2>&1; then
    PATCHES=$(basename -a "${PATCHES_DIR}"/*.patch | tr "\n" " ")
    echo "Apply patch(es) ${PATCHES}"
    git apply "${PATCHES_DIR}"/*.patch || { echo "Error: Failed to apply patch(es) ${PATCHES}"; exit 1; }
  fi
fi

if [ "${DUMP_INCLUDES}" = "true" ]; then

  echo "Dump symbols"

  # Dump included symbols
  "${JEXTRACT}" \
    --define-macro HIGHSINT64=ON \
    --define-macro HIPO=ON \
    --include-dir "${REPO_DIR}"/highs \
    --dump-includes "${INCLUDES_FILE}" \
    "${HEADER_FILE_FULL_PATH}" || { echo "Error: Failed to dump symbols"; exit 1; }

  # Select symbols
  grep "HiGHS" "${INCLUDES_FILE}" \
    | sed "s/Extracted from: .*HiGHS/Extracted from: HiGHS/" \
    | sed "s/header: .*HiGHS/header: HiGHS/" >"${INCLUDES_FILE}".tmp && mv "${INCLUDES_FILE}".tmp "${INCLUDES_FILE}"

else

  echo "Generate bindings"

  # Remove old bindings
  rm -rf "${JAVA_SRC_DIR}"/"${ARTIFACT_ID_DIR}"/bindings

  # Generate bindings
  "${JEXTRACT}" \
    --define-macro HIGHSINT64=ON \
    --define-macro HIPO=ON \
    --include-dir "${REPO_DIR}"/highs \
    --target-package "${ARTIFACT_ID}".bindings \
    --header-class-name Highs_c_api_h \
    --output "${JAVA_SRC_DIR}" \
    @"${BINDINGS_DIR}"/includes.txt "${HEADER_FILE_FULL_PATH}" || { echo "Error: Failed to generate bindings"; exit 1; }

fi

# Cleanup
rm -rf "${REPO_DIR}"
