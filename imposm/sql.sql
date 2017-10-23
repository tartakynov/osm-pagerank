-- bounding box is created using QGIS with coordinate capture plugin
-------------

SELECT
    r.id,
    ST_AsText(r.geometry) as geometry
FROM
    import.osm_roads r
WHERE
    ST_Intersects(r.geometry, ST_Envelope(ST_GeomFromText('LINESTRING(11158491.523 1594059.717, 11238889.508 1510311.816)', 3857)))
ORDER BY
    r.id
-------------

SELECT
  a.id, b.id
FROM
  import.osm_roads a
  INNER JOIN import.osm_roads b ON ST_Intersects(a.geometry, b.geometry) AND a.id < b.id
WHERE
  ST_Intersects(a.geometry, ST_Envelope(ST_GeomFromText('LINESTRING(11158491.523 1594059.717, 11238889.508 1510311.816)', 3857)))
ORDER BY
  a.id
