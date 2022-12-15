#!/bin/bash
if [ -z "$1" ]
then
   echo "Please enter module name like 'user'"
   exit 1
fi
echo "---- BEGIN ALL CI ----"
baseImageName="$ANOUNCY_DOCKER_HOST/anouncy-$1:$(uuidgen)"

sh "$ANOUNCY_PROJECT_PATH/ops/image-build.sh" "$1" "$baseImageName"

echo "---- FILE REPLACE OPERATION END ----"
sh "$ANOUNCY_PROJECT_PATH/ops/helm-upgrade.sh" "$baseImageName"
echo "---- END ALL CI ----"
