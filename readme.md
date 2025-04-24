# Local setup

## Commands

Docker up:
```shell
docker compose --env-file <env_file> -f ./docker-compose-local.yml -p -d --build
```

Diplodoc:
```shell
yfm -i ./docs -o ./src/main/resources/static/docs
docker compose --env-file <env_file> -f ./docker-compose-local.yml -p -d --build
```
