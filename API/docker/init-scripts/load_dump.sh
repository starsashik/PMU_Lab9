#!/bin/sh
set -eu

echo "Restoring database from dump..."

pg_restore \
  --exit-on-error \
  --no-owner \
  --no-privileges \
  --username="$POSTGRES_USER" \
  --dbname="$POSTGRES_DB" \
  /docker-entrypoint-initdb.d/dump.dump

echo "Restore completed"
