#!/bin/bash
if [ -z "$1" ]
then
   echo "Please enter module name like 'user'"
   exit 1
fi
echo "---- BEGIN ALL CI ----"
baseImageName="$ANOUNCY_DOCKER_HOST/anouncy-$1:$(uuidgen)"
sh "$ANOUNCY_PROJECT_PATH/ops/java-docker-image-builder.sh" "$1" "$baseImageName"

if [[ "$?" -ne 0 ]] ; then
  echo 'docker build fail'; exit $?
fi

echo "---- FILE REPLACE OPERATION STARTED ----"
variables="$ANOUNCY_PROJECT_PATH/ops/helm/anouncy/values-variables.txt"
replaceResult=""
while IFS= read -r line
do
  if [[ "$line"  == "$1_deployment_image="* ]] ; then
    replaceResult+="$1_deployment_image=$baseImageName\n"
    echo "$1_deployment_image=$baseImageName"
  elif [[ -n "$line" ]] ; then
    replaceResult+="$line\n"
    echo "$line"
  fi
done < "$variables"

truncate -s 0 "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy/values-variables.txt"
echo -e "$replaceResult" > "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy/values-variables.txt"

echo "---- FILE REPLACE OPERATION END ----"
sh "$ANOUNCY_PROJECT_PATH/ops/helm-upgrade.sh" "$baseImageName"
echo "---- END ALL CI ----"
