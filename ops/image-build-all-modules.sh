#!/bin/bash

echo "Module user Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "user" "$ANOUNCY_DOCKER_HOST/anouncy-user:$(uuidgen)"
if [[ "$?" -ne 0 ]] ; then
  echo 'user build fail'; exit $?
fi

echo "Module announce Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "announce" "$ANOUNCY_DOCKER_HOST/anouncy-announce:$(uuidgen)" "skip-common-build"
if [[ "$?" -ne 0 ]] ; then
  echo 'announce build fail'; exit $?
fi

echo "Module listing Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "listing" "$ANOUNCY_DOCKER_HOST/anouncy-listing:$(uuidgen)" "skip-common-build"
if [[ "$?" -ne 0 ]] ; then
  echo 'listing build fail'; exit $?
fi

echo "Module vote Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "vote" "$ANOUNCY_DOCKER_HOST/anouncy-vote:$(uuidgen)" "skip-common-build"
if [[ "$?" -ne 0 ]] ; then
  echo 'vote build fail'; exit $?
fi

echo "Module region Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "region" "$ANOUNCY_DOCKER_HOST/anouncy-region:$(uuidgen)" "skip-common-build"
if [[ "$?" -ne 0 ]] ; then
  echo 'region build fail'; exit $?
fi

echo "Module persist Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "persist" "$ANOUNCY_DOCKER_HOST/anouncy-persist:$(uuidgen)" "skip-common-build"
if [[ "$?" -ne 0 ]] ; then
  echo 'persist build fail'; exit $?
fi

echo "Module location Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "location" "$ANOUNCY_DOCKER_HOST/anouncy-location:$(uuidgen)" "skip-common-build"
if [[ "$?" -ne 0 ]] ; then
  echo 'location build fail'; exit $?
fi

echo "Module search Starting"
sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "search" "$ANOUNCY_DOCKER_HOST/anouncy-search:$(uuidgen)" "skip-common-build"
if [[ "$?" -ne 0 ]] ; then
  echo 'search build fail'; exit $?
fi

