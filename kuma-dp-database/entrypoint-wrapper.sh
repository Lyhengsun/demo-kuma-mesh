#!/bin/bash
set -e

/usr/local/bin/kumactl install transparent-proxy \
  --kuma-dp-user kuma-data-plane-proxy \
  --redirect-dns \
  > ${LOG_FOLDER:-""}/logs-transparent-proxy-install-${DATAPLANE_VAR_NAME}.log 2>&1 &

# Check if the IP address variable is set and not empty
if [ -n "$DATAPLANE_VAR_ADDRESS" ]; then
    # 1. Mention that the domain is being ignored
    if [ -n "$DATAPLANE_VAR_DOMAIN" ]; then
        echo "Information: DATAPLANE_VAR_ADDRESS ($DATAPLANE_VAR_ADDRESS) is provided. Ignoring DATAPLANE_VAR_DOMAIN ($DATAPLANE_VAR_DOMAIN)."
    fi

    # 2. Assign the IP directly as the networking address
    RESOLVED_IP="$DATAPLANE_VAR_ADDRESS"

else
    # 3. Fallback: If address is missing, attempt to resolve the domain
    echo "Warning: DATAPLANE_VAR_ADDRESS is empty. Attempting to resolve $DATAPLANE_VAR_DOMAIN..."

    # Use 'getent' to resolve (checks /etc/hosts and DNS)
    RESOLVED_IP=$(getent hosts "$DATAPLANE_VAR_DOMAIN" | awk '{ print $1 }' | head -n 1)

    if [ -n "$RESOLVED_IP" ]; then
        echo "Resolved $DATAPLANE_VAR_DOMAIN to: $DATAPLANE_NETWORKING_ADDRESS"
    else
        echo "Error: Could not resolve domain '$DATAPLANE_VAR_DOMAIN' and no address was provided."
        exit 1
    fi
fi

# Start kuma-dp in the background using the variables
echo "Starting kuma-dp connecting to $CP_ADDRESS..."
runuser --user kuma-data-plane-proxy -- \
  /usr/local/bin/kuma-dp run \
  --cp-address="$KUMA_CP_ADDRESS" \
  --ca-cert-file="$KUMA_CA_CERT_FILE" \
  --dataplane-file="$KUMA_DATAPLANE_FILE"  \
  --dataplane-token-file="$KUMA_DATAPLANE_TOKEN_FILE" \
  --dataplane-var name="$DATAPLANE_VAR_NAME" \
  --dataplane-var address="$RESOLVED_IP" \
  --dataplane-var port="$DATAPLANE_VAR_PORT" \
  --dataplane-var servicePort="$DATAPLANE_VAR_SERVICE_PORT" \
  --binary-path /usr/local/bin/envoy \
  > ${LOG_FOLDER:-""}/logs-data-plane-proxy-${DATAPLANE_VAR_NAME}.log 2>&1 &

echo "Sleeping for 10 seconds to allow the dataplane proxy to start..."
sleep 10

# Call the original Postgres entrypoint
exec docker-entrypoint.sh "$@"