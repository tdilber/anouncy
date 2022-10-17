helm repo add minio-official https://charts.min.io
kubectl create namespace blobstorage
helm install prod-minio -n blobstorage minio-official/minio --version 4.0.15 --values "$ANOUNCY_PROJECT_PATH/ops/helm/minio/values.yaml"
