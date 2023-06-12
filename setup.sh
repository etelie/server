#!/usr/bin/env zsh

NEW_RELIC_VERSION="8.3.0"
OPENTELEMETRY_VERSION="1.26.0"

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

echo -n "Normalizing working directory..."
cd "${0:a:h}" || error "cd into repo failed"
echo "done"
echo "Working directory set to $(pwd)"

echo
echo -n "Checking environment variables..."
# Copy .env.template into .env if doesn't already exist
if ! test -f .env; then
  cp .env.template .env
  ln -s .env docker/server/.env
fi
echo "done"

set_env_value "ETELIE_HOME"
set_env_value "PGADMIN_DEFAULT_EMAIL"
set_env_value "PGADMIN_DEFAULT_PASSWORD"
test -f .enve && rm .enve # Not sure why this file is being created

if ! test -f newrelic/newrelic.jar; then
  echo
  echo "Downloading New Relic instrumentation package (version ${NEW_RELIC_VERSION})"
  curl -o newrelic/newrelic.zip -O "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/${NEW_RELIC_VERSION}/newrelic-java.zip" &&
    echo "Extracting New Relic archive into $(pwd)/newrelic/" &&
    unzip newrelic/newrelic.zip -x newrelic/newrelic.yml &&
    rm newrelic/newrelic.zip
fi

if ! test -f opentelemetry/opentelemetry-javaagent.jar; then
  echo
  echo "Downloading Open Telemetry agent package (version ${OPENTELEMETRY_VERSION})"
  curl \
    -o 'opentelemetry/opentelemetry-javaagent.jar' \
    -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OPENTELEMETRY_VERSION}/opentelemetry-javaagent.jar"
fi

exit 0
