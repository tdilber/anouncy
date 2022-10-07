helm repo add traefik https://helm.traefik.io/traefik
kubectl create namespace traefik
kubectl apply -f "$ANOUNCY_PROJECT_PATH/ops/helm/traefik/traefik-middleware-config.yaml"
helm install ingress-traefik traefik/traefik --version 10.24.3 -n traefik --values "$ANOUNCY_PROJECT_PATH/ops/helm/traefik/traefik-config.yaml"
