#!/usr/bin/env zsh

cd "${0:a:h}" || error "cd into repo failed"

# Set environment variables to ./.env
if ! grep -q 'ETELIE_HOME' ./.env 2> /dev/null
then
  tmp=""
  vared -p "Set ETELIE_HOME path: " -c tmp
  echo "ETELIE_HOME=${tmp}" > ./.env
fi

# Run Docker compose
docker-compose \
  --project-name=etelie --file=./docker/docker-compose.yml --env-file=./.env \
  up --detach --remove-orphans

function error {
  message=$1
  code=$2
  echo "$message" >&2
  exit "$(test -z code && 1 || $code)"
}

