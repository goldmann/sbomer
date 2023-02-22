# SBOMer

## Development

### JDK

This project is developed using JDK 17. Not that any features require it, but why not?

You can use https://sdkman.io/ to install and manage JDKs:

```
sdk install java 17.0.6-tem
```

When you enter the project directory you can run:

```
sdk env
```

And you're all set!

> You can add `$HOME/.sdkman/etc/config` following entry:
>
> ```
> sdkman_auto_env=true
> ```
>
> And have the JDK set automatically when entering the directory.

### Local database

First log it into [redhat.io registry](https://access.redhat.com/terms-based-registry) and then run the command below:

```
podman run -it --rm -e POSTGRESQL_USER=username -e POSTGRESQL_PASSWORD=password -e POSTGRESQL_DATABASE=sbomer -p 5432:5432 registry.redhat.io/rhel9/postgresql-13@sha256:31fbd226db60cb48ff169491a8b88e69ec727c575ba76dc6095c87f70932b777
```

> **NOTE**
>
> Please note that this is an ephemeral container. All data will be destroyed when you stop the container.

### Running the service locally

To run the service locally, just run:

```
./mvnw quarkus:dev
```

## Documentation

See [the documentation](docs/index.md).

## Kubernetes deployment

Kustomize deployment scripts are available:

```
kubectl apply -k k8s/overlays/production
```

This will run the service using **published** images.