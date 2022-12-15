#!/bin/bash

echo "Prev Current context => $(kubectl config current-context)"
kubectl config use-context $ANOUNCY_KUBECTL_NAME
echo "Next Current context => $(kubectl config current-context)"


source "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy/values-variables.txt"
#TODO maybe we dont have to do this
export user_deployment_image=$user_deployment_image
export announce_deployment_image=$announce_deployment_image
export listing_deployment_image=$listing_deployment_image
export vote_deployment_image=$vote_deployment_image
export region_deployment_image=$region_deployment_image
export persist_deployment_image=$persist_deployment_image
export location_deployment_image=$location_deployment_image
echo "---- HELM UPDATE STARTED ----"

cd "/tmp/" && envsubst '$user_deployment_image,$announce_deployment_image,$listing_deployment_image,$vote_deployment_image,$region_deployment_image,$persist_deployment_image,$location_deployment_image' < "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy/values.yaml" > "values.yaml"
cd "/tmp/" && helm upgrade --cleanup-on-fail anouncy-1664399111 "$ANOUNCY_PROJECT_PATH/ops/helm/anouncy" --version=0.1.0 --values "values.yaml"

echo "---- SLEEPING 10 sec ----"
sleep 10
kubectl get po
echo "---- LOGS ----"
gtimeout 35 kubectl logs -f --tail 50 $(kubectl get pods -o jsonpath='{range .items[*]}{"\n"}{.metadata.name}{"\t"}{range .spec.containers[*]}{.image}{", "}{end}{end}' | grep "$1" | awk '{ print $1 }')
echo "---- LOGS END ----"
kubectl get po
echo "---- HELM UPDATE COMPLETE ----"
exit 0
