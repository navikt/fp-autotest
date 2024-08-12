ALTER SESSION SET CONTAINER=FREEPDB1;
alter session set "_oracle_script"=true;

DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPRISK';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPRISK IDENTIFIED BY fprisk PROFILE DEFAULT ACCOUNT UNLOCK');
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
TO FPRISK;

ALTER USER FPRISK QUOTA UNLIMITED ON SYSTEM;

DECLARE userexists INTEGER;
BEGIN
SELECT count(*)
INTO userexists
FROM SYS.ALL_USERS
WHERE USERNAME = 'FPRISK_UNIT';
IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPRISK_UNIT IDENTIFIED BY fprisk_unit PROFILE DEFAULT ACCOUNT UNLOCK');
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
TO FPRISK_UNIT;
