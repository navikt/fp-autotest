CREATE DATABASE fpkalkulus_unit;
CREATE USER fpkalkulus_unit WITH PASSWORD 'fpkalkulus_unit';
GRANT ALL ON DATABASE fpkalkulus_unit TO fpkalkulus_unit;
ALTER DATABASE fpkalkulus_unit SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpkalkulus_unit OWNER TO fpkalkulus_unit;

CREATE DATABASE fpkalkulus;
CREATE USER fpkalkulus WITH PASSWORD 'fpkalkulus';
GRANT ALL ON DATABASE fpkalkulus TO fpkalkulus;
ALTER DATABASE fpkalkulus SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpkalkulus OWNER TO fpkalkulus;
