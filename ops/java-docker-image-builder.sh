#!/bin/bash
if [ -z "$1" ]
then
   echo "Please enter module name like 'user'"
   exit 1
fi

# TODO skip test close after test written
cd "$ANOUNCY_PROJECT_PATH/$1/" && mvn clean install package -DskipTests=true

if [[ "$?" -ne 0 ]] ; then
  echo 'module compile fail'; exit $rc
fi

baseImageName="anouncy-$1:$(uuidgen)"
imageName="$ANOUNCY_DOCKER_HOST/$baseImageName"
docker login $ANOUNCY_DOCKER_HOST -u $ANOUNCY_DOCKER_USERNAME --password-stdin $ANOUNCY_DOCKER_PASSWORD
echo "Docker Generated Image Name => $imageName"
echo "---------------------------------------------------"
echo "Docker Build Starting"
docker build --platform x86_64 -t $imageName -f "$ANOUNCY_PROJECT_PATH/ops/docker/Dockerfile" "$ANOUNCY_PROJECT_PATH/$1/"
echo "Docker Build End"
if [[ "$?" -ne 0 ]] ; then
  echo 'Docker Build Fail'; exit $rc
fi
echo "---------------------------------------------------"
echo "Docker Push Starting"
docker push $imageName
if [[ "$?" -ne 0 ]] ; then
  echo 'Docker Push Fail'; exit $rc
fi
echo "Docker Push End Successfully"
echo "Docker Pushed Image Name => $imageName"
echo "---------------------------------------------------"
