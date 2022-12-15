#!/bin/bash
if [ -z "$1" ]
then
   echo "Please enter module name like 'user'"
   exit 1
fi
echo "---- BEGIN BUILD CI ----"
if [ -z "$2" ]
then
   echo "Please enter baseImageName name"
   exit 1
fi
baseImageName="$2"
sh "$ANOUNCY_PROJECT_PATH/ops/java-docker-image-builder.sh" "$1" "$baseImageName" $3

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
echo "$replaceResult" > "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy/values-variables.txt"
