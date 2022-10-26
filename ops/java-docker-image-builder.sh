#!/bin/bash
if [ -z "$1" ]
then
   echo "Please enter module name like 'user'"
   exit 1
fi

cd "$ANOUNCY_PROJECT_PATH/backend/common/" && mvn clean install package
cd "$ANOUNCY_PROJECT_PATH/backend/$1/" && mvn clean install package

if [[ "$?" -ne 0 ]] ; then
  echo 'module compile fail'; exit $?
fi

if [ -z "$2" ]
then
   imageName="$ANOUNCY_DOCKER_HOST/anouncy-$1:$(uuidgen)"
else
   imageName="$2"
fi

docker login $ANOUNCY_DOCKER_HOST -u $ANOUNCY_DOCKER_USERNAME --password-stdin $ANOUNCY_DOCKER_PASSWORD
echo "Docker Image Name => $imageName"
echo "---------------------------------------------------"
echo "Docker Build Starting"
docker build --platform x86_64 -t $imageName -f "$ANOUNCY_PROJECT_PATH/ops/docker/Dockerfile" "$ANOUNCY_PROJECT_PATH/backend/$1/"
echo "Docker Build End"
if [[ "$?" -ne 0 ]] ; then
  echo 'Docker Build Fail'; exit $?
fi
echo "---------------------------------------------------"
echo "Docker Push Starting"
docker push $imageName
if [[ "$?" -ne 0 ]] ; then
  echo 'Docker Push Fail'; exit $?
fi
echo "Docker Push End Successfully"
echo "Docker Pushed Image Name => $imageName"
echo "---------------------------------------------------"

exit 0
