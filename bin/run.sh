#!/bin/bash
#
# NAME
# run - run the main function of a specific class
#
# SYNOPSIS
# run CLASSNAME
#
# DESCRIPTION
# The suffix Kt will be added automatically to the classname. So, for example,
# pass 'Kt010_HelloCoroutine' instead of 'Kt010_HelloCoroutineKt' as the
# classname to run the example in Kt010_HelloCoroutine.kt.
#
set -o errexit -o nounset

if [[ "$#" -ne 1 ]]; then
  echo >&2 "usage: $(basename $0) CLASSNAME"
  echo >&2 ""
  echo >&2 "example: $(basename $0) Kt010_HelloCoroutine"
  exit 1
fi
CLASSNAME="$1"

PROJECT_ROOT=$(dirname "$(dirname "$(realpath "$0")")")
cd "$PROJECT_ROOT"

exec ./gradlew run -Plaunch="$CLASSNAME"
