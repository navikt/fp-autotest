CREATE DATABASE fpoversikt;
CREATE USER fpoversikt WITH PASSWORD 'fpoversikt';
GRANT ALL PRIVILEGES ON DATABASE fpoversikt TO fpoversikt;
ALTER DATABASE fpoversikt SET timezone TO 'Europe/Oslo';
