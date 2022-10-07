kubectl create namespace logging
kubectl apply -f -n logging "$ANOUNCY_PROJECT_PATH/ops/helm/elk/elk-password-secret.yaml"
helm install logging-elasticsearch bitnami/elasticsearch --version 19.3.0 -n logging --values "$ANOUNCY_PROJECT_PATH/ops/helm/elk/elastic-search/values.yaml"
helm install logging-logstash bitnami/logstash --version 5.1.2 -n logging --values "$ANOUNCY_PROJECT_PATH/ops/helm/elk/logstash/values.yaml"
helm install logging-kibana bitnami/kibana --version 10.2.2 -n logging --values "$ANOUNCY_PROJECT_PATH/ops/helm/elk/kibana/values.yaml"
