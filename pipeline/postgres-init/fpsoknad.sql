CREATE DATABASE fpsoknad;
CREATE USER fpsoknad WITH PASSWORD 'fpsoknad';
GRANT ALL PRIVILEGES ON DATABASE fpsoknad TO fpsoknad;
ALTER DATABASE fpsoknad SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpsoknad OWNER TO fpsoknad;
