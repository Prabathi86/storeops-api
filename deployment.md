# Deploying StoreOps API to OpenShift (OCP)

This guide builds the Spring Boot application image from the repository `Dockerfile`
and deploys it with the Helm chart in `helm/storeops`.

## Prerequisites

- Access to an OpenShift cluster and the `oc` CLI.
- Docker or Podman (or another OCI image builder).
- Helm 3.
- Java 17 and Maven to build the application JAR.
- Permission to push an image to a registry reachable by the cluster.

Log in and select the target project:

```sh
oc login https://api.<cluster-domain>:6443
oc project <namespace>
```

## Build and publish the container image

The Maven build produces `target/storeops.jar` (`finalName` is `storeops`). The
Dockerfile uses the Red Hat UBI 9 OpenJDK 17 runtime, listens on port 8080, and runs as
UID 1001, which is suitable for OpenShift's non-root execution model.

```sh
mvn clean package
docker build -t <registry>/<namespace>/storeops:<tag> .
docker push <registry>/<namespace>/storeops:<tag>
```

For example, when using Podman:

```sh
podman build -t <registry>/<namespace>/storeops:<tag> .
podman push <registry>/<namespace>/storeops:<tag>
```

Authenticate to the image registry before pushing. If the image is in a private
registry, configure an image pull secret in the OpenShift project and attach it to the
service account used by the workload:

```sh
oc create secret docker-registry storeops-registry \
  --docker-server=<registry> \
  --docker-username=<username> \
  --docker-password=<password>
oc secrets link default storeops-registry --for=pull
```

Do not put registry credentials or application secrets in the Dockerfile, Helm values,
or source control.

## Validate and install the Helm chart

The chart is at `helm/storeops`; its current defaults set one replica, a `ClusterIP`
service on port 8080, and the image `docker.io/prabathi/storeops:1.0.2`. Override the
image for the image built above:

```sh
helm lint helm/storeops
helm template storeops helm/storeops \
  --namespace <namespace> \
  --set image.repository=<registry>/<namespace>/storeops \
  --set image.tag=<tag>
```

Install or upgrade the release:

```sh
helm upgrade --install storeops helm/storeops \
  --namespace <namespace> \
  --create-namespace \
  --set image.repository=<registry>/<namespace>/storeops \
  --set image.tag=<tag> \
  --wait \
  --timeout 5m
```

For repeatable environments, put deployment-specific non-secret values in a separate
values file and pass it with `-f <environment-values.yaml>`. Pass sensitive values
through OpenShift Secrets; do not commit them.

Check rollout and pod health:

```sh
oc rollout status deployment/storeops --timeout=5m
oc get deployments,pods,services
oc logs deployment/storeops
```

The chart's Deployment probes target `/actuator/health/readiness` and
`/actuator/health/liveness` on port 8080. The application includes Spring Boot
Actuator and enables the health probe endpoints.

## Expose the service outside the cluster

The chart creates a `ClusterIP` service named `storeops`. An OpenShift Route is not
currently rendered by the chart, even though `route.enabled` and `route.host` appear
in `values.yaml`. Create a Route separately after installation:

```sh
oc expose service/storeops --port=8080
oc get route storeops
```

To specify a hostname:

```sh
oc expose service/storeops --port=8080 --hostname=<app-hostname>
```

Use the Route's `https://` URL if the cluster's default Route TLS termination is
enabled. Configure TLS policy explicitly if the application requires a particular
termination mode.

## Configuration and chart notes

Review these chart details before relying on non-default configuration:

- `values.yaml` declares `replicaCount`, `resources`, `env`, `config`, `secret`,
  `livenessProbe`, `readinessProbe`, `route.enabled`, and `route.host`, but the current
  templates do not consume most of these values. The Deployment hard-codes one
  replica and probe timings/paths, and its only configured environment variable is
  `CONSUMER_REFRESH_URL`.
- The Deployment template references `.Values.consumerRefreshUrl`, which is not
  currently defined in the default `values.yaml`. Set it explicitly if the application
  depends on it:

  ```sh
  --set consumerRefreshUrl=<url>
  ```

- `env.SPRING_PROFILES_ACTIVE` in `values.yaml` is not currently rendered into the
  Deployment. If the Spring profile must be set, update the Deployment template to
  create a `SPRING_PROFILES_ACTIVE` environment variable or configure it through an
  approved environment-specific overlay.
- The sample `secret.DB_USERNAME` and `secret.DB_PASSWORD` values are not rendered
  from a Kubernetes Secret by the current template. Do not treat those chart values
  as secret management; create and reference an OpenShift Secret before deploying
  credentials.
- The current values do not configure an Ingress/Route template. Route creation is a
  separate `oc expose` step as shown above.

Before production use, update the chart to wire the desired replica count, resource
requests/limits, profiles, Secret references, configurable health probes, and an
optional OpenShift Route template, then verify the rendered output with `helm template`.

## Rollback and removal

Roll back to the previous Helm revision:

```sh
helm history storeops --namespace <namespace>
helm rollback storeops <revision> --namespace <namespace> --wait
```

Uninstall the release when it is no longer needed:

```sh
helm uninstall storeops --namespace <namespace>
```

The Route created separately with `oc expose` is not managed by Helm; remove it
separately if required:

```sh
oc delete route storeops
```
