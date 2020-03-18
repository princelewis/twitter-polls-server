builder:
    docker build -t twitter/mvn-builder:latest --cache-from twitter/mvn-builder:latest . -f Dockerfile-dep

start:
    docker-compose up --build