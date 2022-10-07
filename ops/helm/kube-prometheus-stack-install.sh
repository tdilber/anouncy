kubectl create namespace prometheus
helm install prod-prometheus -n prometheus prometheus-community/kube-prometheus-stack --version 39.9.0 --values "$ANOUNCY_PROJECT_PATH/ops/helm/kube-prometheus-stack/values.yaml"
