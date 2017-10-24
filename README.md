# OpenStreetMap PageRank

I will write a short article as soon as I have time. Briefly, this project was created to research the capabilities of PageRank algorithm on weighting linear geospatial graph without knowing any information except the coordinates. Why? Just for fun!

Volga river. Raw graph vs weighted graph. Each segment is a vertex, each intersection is an edge
![volga-river](https://raw.githubusercontent.com/tartakynov/osm-pagerank-scala/master/docs/volga-river.gif)

## Data preparation
The geospatial data is downloaded from OpenStreetMap but may have come from any other source.

I used [imposm3](https://github.com/omniscale/imposm3) to import linear geometries from OpenStreetMap .osm.pbf files into a PostGIS database running inside a Docker container. Bash scripts to start the containers with PostGIS and imposm3 can be found [here](https://github.com/tartakynov/osm-pagerank/tree/master/imposm).

## Create a graph
The way I created it, each linear geometry is a vertex on the graph. There is an edge between two vertices if their geometries intersect. The graph is undirected but it is still possible to traverse the graph by direction of the flow by checking the relation between geometries.

![pic1](https://raw.githubusercontent.com/tartakynov/osm-pagerank-scala/master/docs/pic1.png)

I store the graph in 2 CSV files - one of them contains vertices in the following format `id,geometry` where the geometry is represented as [WKT](https://en.wikipedia.org/wiki/Well-known_text). The second file contains edges as `id1,id2`. The reason why I chose CSV format is because it is extremely easy to implement and it's supported by [QGIS](http://www.qgis.org/en/site/) which I use for visualization.

The upper example is represented in CSV as follows:

`vertices.csv`
```csv
A,"LINESTRING(0 0, 0 1, 2 1)"
B,"LINESTRING(0 1, 0 2)"
C,"LINESTRING(1 1, 1 2)"
D,"LINESTRING(1 0, 1 1)"
E,"LINESTRING(2 1, 2 2)"
```

`edges.csv`
```csv
A,B
A,C
A,D
A,E
C,D
```



## Normalize the graph
???

## Calculate PageRank
???

Bangkok roads
![bangkok-transport](https://raw.githubusercontent.com/tartakynov/osm-pagerank/master/docs/bkk-transport.gif)
