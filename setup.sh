#!/usr/bin/env zsh



function error {
  message=$1
  code=$2

  echo "$message" >&2
  # shellcheck disable=SC2015
  # (overridden because second term is always true)
  exit "$(test -z code && 1 || $code)"
}

function set_env_value {
  local name=$1
  local value=""

  grep -qE "^${name}=.+$" .env && return 1

  vared -p "Set value of ${name}: " -c value
  sed -ie "s%^${name}=$%${name}=${value}%g" .env

  return 0
}



cd "${0:a:h}" || error "cd into repo failed"

# Copy .env.template into .env if doesn't already exist
if ! test -f .env
then
  cp .env.template .env
fi

set_env_value "ETELIE_HOME"
set_env_value "PGADMIN_DEFAULT_EMAIL"
set_env_value "PGADMIN_DEFAULT_PASSWORD"
test -f .enve && rm .enve # Not sure why this file is being created

exit 0
