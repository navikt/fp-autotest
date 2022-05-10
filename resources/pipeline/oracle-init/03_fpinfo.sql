-- ###############################
-- ### Opplegg for lokal jetty ###
-- ###############################
DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPINFO_SCHEMA';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPINFO_SCHEMA IDENTIFIED BY fpinfo_schema PROFILE DEFAULT ACCOUNT UNLOCK');
  END IF;
END;
/

GRANT
    CREATE SESSION,
    ALTER SESSION,
    CONNECT,
    RESOURCE,
    CREATE MATERIALIZED VIEW,
    CREATE JOB,
    CREATE TABLE,
    CREATE SYNONYM,
    CREATE VIEW,
    CREATE SEQUENCE,
    UNLIMITED TABLESPACE,
    SELECT ANY TABLE
TO FPINFO_SCHEMA;

DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPINFO';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPINFO IDENTIFIED BY fpinfo PROFILE DEFAULT ACCOUNT UNLOCK');
  END IF;
END;
/

GRANT
    CREATE SESSION,
    ALTER SESSION,
    CONNECT,
    RESOURCE,
    CREATE MATERIALIZED VIEW,
    CREATE JOB,
    CREATE TABLE,
    CREATE SYNONYM,
    CREATE VIEW,
    CREATE SEQUENCE,
    UNLIMITED TABLESPACE,
    SELECT ANY TABLE
TO FPINFO;

ALTER USER FPINFO QUOTA UNLIMITED ON SYSTEM;