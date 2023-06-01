CREATE DATABASE fpinfohistorikk;
CREATE USER fpinfohistorikk WITH PASSWORD 'fpinfohistorikk';
GRANT ALL PRIVILEGES ON DATABASE fpinfohistorikk TO fpinfohistorikk;
ALTER DATABASE fpinfohistorikk SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpinfohistorikk OWNER TO fpinfohistorikk;
