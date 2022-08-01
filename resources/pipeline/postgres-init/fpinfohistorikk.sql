-- Brukes for opprette bruke for fpinfo-historikk for lokal utvikling.
CREATE DATABASE fpinfohistorikk;
CREATE USER fpinfohistorikk WITH PASSWORD 'fpinfohistorikk';
GRANT ALL PRIVILEGES ON DATABASE fpinfohistorikk TO fpinfohistorikk;
ALTER DATABASE fpinfohistorikk SET timezone TO 'Europe/Oslo';
