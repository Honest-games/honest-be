docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t logotipiwe/node:18-alpine-diplodoc-4-57-19 \
  --push .