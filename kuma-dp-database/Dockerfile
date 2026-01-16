# Use the official Postgres image as the base
FROM postgres:18

# Install any required dependencies (e.g., curl to download kuma)
RUN apt-get update && apt-get install -y curl iptables iproute2 && rm -rf /var/lib/apt/lists/*

# download and install Kuma
RUN curl --location https://kuma.io/installer.sh | VERSION="2.13.0" sh - && \
    mv kuma-2.13.0/bin/* /usr/local/bin/ && \
    rm -rf kuma-2.13.0 && \
    useradd -u 5678 -U kuma-data-plane-proxy

# (Optional) Add a custom wrapper script to run both Postgres and kuma-dp
COPY entrypoint-wrapper.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/entrypoint-wrapper.sh

ENTRYPOINT ["entrypoint-wrapper.sh"]
CMD ["postgres"]