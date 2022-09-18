#!/bin/bash

echo "Prev Current context => $(kubectl config current-context)"
kubectl config use-context $ANOUNCY_KUBECTL_NAME
echo "Next Current context => $(kubectl config current-context)"

cd "/tmp/" && envsubst '$ANOUNCY_DOCKER_HOST,$ANOUNCY_DOCKER_USERNAME,$ANOUNCY_DOCKER_PASSWORD' < "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy/values.yaml" > "values.yaml"
cd "/tmp/" && helm upgrade --cleanup-on-fail anouncy-1663527515 "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy" --version=0.1.0 --values "values.yaml"
