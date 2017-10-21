#!/usr/bin/env bash

BASEDIR="$( cd "$(dirname "$0")" && pwd )"
docker run                                                  \
    --rm -it                                                \
    --name pagerank-poc-imposm                              \
    --link pagerank-poc-postgis:postgis                     \
    -v ~/Downloads/osm:/osm:ro                              \
    -v ${BASEDIR}/mappings:/mappings:ro                     \
    pcic/imposm3                                            \
        import                                              \
        -mapping /mappings/mappings.json                    \
        -connection postgis://postgres:postgres@postgis/osm \
        --read /osm/thailand-latest.osm.pbf                 \
        --write

