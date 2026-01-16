#!/bin/bash
set -e

/usr/local/bin/kumactl install transparent-proxy \
  --kuma-dp-user kuma-data-plane-proxy \
  --redirect-dns \
  --exclude-inbound-ports 8080 \
  > ${LOG_FOLDER:-""}/logs-transparent-proxy-install-${DATAPLANE_VAR_NAME}.log 2>&1

# Start kuma-dp in the background using the variables
echo "Starting kuma-dp connecting to $KUMA_CP_ADDRESS..."
runuser --user kuma-data-plane-proxy -- \
  /usr/local/bin/kuma-dp run \
  --cp-address="$KUMA_CP_ADDRESS" \
  --dataplane-file="$KUMA_DATAPLANE_FILE"  \
  --dataplane-token-file="$KUMA_DATAPLANE_TOKEN_FILE" \
  --dataplane-var name="$DATAPLANE_VAR_NAME" \
  --dataplane-var address="$DATAPLANE_VAR_ADDRESS" \
  --dataplane-var port="$DATAPLANE_VAR_PORT" \
  --binary-path /usr/local/bin/envoy \
  > ${LOG_FOLDER:-""}/logs-data-plane-proxy-${DATAPLANE_VAR_NAME}.log 2>&1 &

echo "Sleeping for 10 seconds to allow the dataplane proxy to start..."
sleep 10

nslookup postgres-db.svc.mesh.local

exec java "$@"
