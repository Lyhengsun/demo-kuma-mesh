# build jar file
FROM gradle:9.1.0-jdk21-alpine AS build

WORKDIR /app

COPY gradle gradle
# Download dependencies first (cached unless build files change)
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# Then copy source and build (only invalidated when source changes)
COPY src src
RUN gradle bootJar --no-daemon --parallel --build-cache

# copy jar file
FROM eclipse-temurin:21-jre-noble AS run

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

# Install any required dependencies (e.g., curl to download kuma)
RUN apt-get update && apt-get install -y curl iptables iproute2 dnsutils && rm -rf /var/lib/apt/lists/*

# download and install Kuma
RUN cd / && \
    curl --location https://kuma.io/installer.sh | VERSION="2.13.0" sh - && \
    mv kuma-2.13.0/bin/* /usr/local/bin/ && \
    rm -rf kuma-2.13.0 && \
    useradd -u 5678 -U kuma-data-plane-proxy

# (Optional) Add a custom wrapper script to run both Postgres and kuma-dp
COPY entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["entrypoint.sh"]

CMD ["-Xmx1G", "-jar", "/app/app.jar"]