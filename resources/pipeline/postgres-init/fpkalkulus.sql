CREATE DATABASE fpkalkulus;
CREATE USER fpkalkulus WITH PASSWORD 'fpkalkulus';
GRANT ALL ON DATABASE fpkalkulus TO fpkalkulus;
ALTER DATABASE fpkalkulus SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpkalkulus OWNER TO fpkalkulus;
