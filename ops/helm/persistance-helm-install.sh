kubectl create namespace persist
helm install prod-redis -n persist bitnami/redis --version 17.3.1 --values "$ANOUNCY_PROJECT_PATH/ops/helm/redis/values.yaml"
helm install prod-postgresql -n persist bitnami/postgresql --version 11.9.5 --values "$ANOUNCY_PROJECT_PATH/ops/helm/postgres/values.yaml"

kubectl apply -n persist -f "$ANOUNCY_PROJECT_PATH/ops/helm/neo4j/neo4j-password-secret.yaml"
helm repo add equinor-charts https://equinor.github.io/helm-charts/charts/
helm install prod-neo4j  -n persist equinor-charts/neo4j-community --version 1.2.5 --values "$ANOUNCY_PROJECT_PATH/ops/helm/neo4j/values.yaml"
