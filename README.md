# OpenStreetMap PageRank

![demo](https://raw.githubusercontent.com/tartakynov/osm-pagerank-scala/master/docs/volga.gif)

### Step 1. Prepare data

Use imposm3 to import osm pbf file into PostGIS database and then

##### vertices.csv
```sql
SELECT id, ST_AsText(geometry) FROM import.osm_waterways WHERE type = 'river' ORDER BY id
```

##### edges.csv
```sql
SELECT
  a.id, b.id
FROM
  import.osm_waterways a
  INNER JOIN import.osm_waterways b ON a.type = b.type AND ST_Intersects(a.geometry, b.geometry)
WHERE
  a.type = 'river'
  AND a.id < b.id
ORDER BY
  a.id
```

### Step 2. Normalize data
???

### Step 3. Calculate PageRank
???
