## Summary
This project focuses on learning about Kuma Mesh.

It will deploy a simple CRUD for products that communicate with a postgres database using Kuma Mesh.

## Pre-requisite
### Create a docker network
```shell
docker network create \
  --subnet 172.57.78.0/24 \
  --ip-range 172.57.78.0/24 \
  --gateway 172.57.78.254 \
  kuma-demo-network
```

## Quickstart (doesn't work for homelab-test branch)
### 1. Install the kumactl binary:
```shell
curl -L https://kuma.io/installer.sh | VERSION="2.13.0" sh -
```
Add the ```bin``` folder to the PATH if you want to use the ```kumactl``` command across the system.

For Linux, you can just move the binary to ```/usr/local/bin```
```shell
mv kuma-2.13.0/bin/* /usr/local/bin/  
```

Check and confirm that kumactl is installed:
```shell
kumactl version 2>/dev/null
```

### 2. Setup the kuma control plane:
```shell
docker compose -f docker-compose-kuma-cp.yml up -d
```
You can check the gui of the control plane at http://localhost:25681/gui/

Get admin token from control plane container:
```shell
docker exec --tty --interactive kuma-demo-control-plane \
  wget --quiet --output-document - \
  http://127.0.0.1:5681/global-secrets/admin-user-token \
  | jq --raw-output .data \
  | base64 --decode
```

Use the retrieved token to link kumactl to the control plane:
```shell
kumactl config control-planes add \
  --name kuma-demo \
  --address http://127.0.0.1:25681 \
  --auth-type tokens \
  --skip-verify \
  --auth-conf "token=<admin_token>"
```

Run this command to check if the connection is working:
```shell
kumactl get meshes
```
You should see a list of meshes with one entry called ```default```. This confirms the configuration is successful.

Output:
```shell
NAME      mTLS   LOCALITY   ZONEEGRESS   AGE
default   off    off        off          2m
```

Set the default mesh to use MeshServices in Exclusive mode and have mTLS enabled in Kuma Mesh:
```shell
kumactl apply -f mesh-config.yml
```

With mTLS enabled, by default, Kuma Mesh will deny all traffic for security purpose. You would need to apply some MeshTrafficPermission rules to allow for the API to access the database:
```shell
kumactl apply -f mesh-traffic.yml
```

Make sure to get rid of old token in the ```kuma-demo``` folder if there are any:
```shell
rm -rf kuma-demo/token-*
```
### 3. Setup and register the postgres database in Kuma Mesh
Generate a new token for the database data-plane:
```shell
kumactl generate dataplane-token \
  --workload postgres-db \
  --valid-for 720h \
  > "kuma-demo/token-db"
```

Start the database and wait for it to setup and register itself to the Kuma Control Plane:
```shell
docker compose -f docker-compose-kuma-dp-database.yml up -d
```
You can check if it is available on http://localhost:25681/gui/meshes/default/services/mesh-services and look at the ```State``` column of postgres-db

### 4. Setup and register the product service in Kuma Mesh
Generate a new token for the product service data-plane:
```shell
kumactl generate dataplane-token \
  --workload product-service \
  --valid-for 720h \
  > "kuma-demo/token-product-service"
```

Start the product service and wait for it to setup and register itself to the Kuma Control Plane (Make sure to wait for the database to finish):
```shell
docker compose -f docker-compose-kuma-dp-service.yml up -d
```
After it finished starting up, ```product-service``` should appear on the GUI for the Kuma Control Plane and the API should be running on http://localhost:9090/swagger-ui/index.html

## Extra Configuration
### Generate self-signed certificate for testing ( use https with internal domain name )
```shell
kumactl generate tls-certificate \
  --type=server \
  --hostname=<KUMA_CP_DNS_NAME> \
  --cert-file=./kuma-demo/tls.crt \
  --key-file=./kuma-demo/tls.key
```

Since ```tls.crt``` is a self-signed cert, it’s also a CA:
```shell
cp ./kuma-demo/tls.crt ./kuma-demo/ca.crt
```

Configure control-plane with the generated certificates:
```shell
KUMA_GENERAL_TLS_CERT_FILE=/tmp/tls.crt \
  KUMA_GENERAL_TLS_KEY_FILE=/tmp/tls.key \
  kuma-cp run
```
Note: ```docker-compose-kuma-cp.yml``` has been modified for this

Configure the data plane proxy with CA:
kuma-dp run \
  --cp-address=https://<KUMA_CP_DNS_NAME>:5678 \
  --ca-cert-file=/tmp/ca.crt \
  --dataplane-file=dp.yaml \
  --dataplane-token-file=/tmp/kuma-dp-redis-1-token