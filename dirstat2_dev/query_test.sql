-- ext, ssize, num_files, num_errors, num_empty, biggest_bytes biggest_path

-- below /		from 3
-- below /data		from 7
-- below /data/var	from 11
-- below /etc/profile	from 13

WITH tmp AS (
	SELECT
		SUBSTRING(
			SUBSTRING(
				files.path
				FROM 2
			)
			FROM '^[^/]*$'
		) AS entry,
--			FOR (CASE
--				WHEN POSITION('/'
--					IN SUBSTRING(files.path FROM 2)) = 0
--				THEN CHAR_LENGTH(files.path)
--				ELSE POSITION('/'
--					IN SUBSTRING (files.path FROM 2)) - 1
--			END)
--		) AS entry,
		COUNT(CASE WHEN files.reg THEN 1 ELSE NULL END) AS num_files,
		SUM(files.file_size) AS ssize,
		COUNT(etype) AS num_errors,
		COUNT(CASE WHEN files.file_size = 0 THEN 1 ELSE NULL END) AS
									empty,
		MAX(files.file_size) AS biggest_bytes
	FROM files
	LEFT JOIN errors
		ON files.scan = errors.scan AND files.path = errors.path
	WHERE files.path LIKE '/%' 
	GROUP BY entry
)
SELECT
	tmp.entry, tmp.num_files, tmp.ssize, tmp.num_errors, tmp.empty,
	tmp.biggest_bytes --, files.path AS bgst
FROM tmp
--LEFT JOIN files ON files.file_size = tmp.biggest_bytes AND
--				files.path LIKE CONCAT('/', tmp.entry, '%')
ORDER BY tmp.entry ASC
;
