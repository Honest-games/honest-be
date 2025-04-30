# Honest backend

## Profiles
- `prod` - Production
- `local` - Local development
- `local-dc` - Local development in Docker Compose

Profile `docker` is set if launch is in docker.

## Local start commands

Docker up:
```shell
docker compose --env-file <env_file> -f ./docker-compose-local.yml -p -d --build
```

Diplodoc:
```shell
yfm -i ./docs -o ./src/main/resources/static/docs
docker compose --env-file <env_file> -f ./docker-compose-local.yml -p -d --build
```
