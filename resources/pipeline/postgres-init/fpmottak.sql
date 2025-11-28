CREATE DATABASE fpmottak;
CREATE USER fpmottak WITH PASSWORD 'fpmottak';
GRANT ALL ON DATABASE fpmottak TO fpmottak;
ALTER DATABASE fpmottak SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpmottak OWNER TO fpmottak;
