#!/usr/bin/env bash

docker run -d                       \
    --name pagerank-poc-postgis     \
    -p 5432:5432                    \
    -e POSTGRES_PASSWORD=postgres   \
    -e POSTGRES_DB=osm              \
    mdillon/postgis:9.6
