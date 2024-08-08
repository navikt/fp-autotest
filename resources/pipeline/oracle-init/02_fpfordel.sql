ALTER SESSION SET CONTAINER=FREEPDB1;
alter session set "_oracle_script"=true;

-- ##################################################
-- ### Opplegg for enhetstester (lokal + jenkins) ###
-- ##################################################
DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPFORDEL_UNIT';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPFORDEL_UNIT IDENTIFIED BY fpfordel_unit PROFILE DEFAULT ACCOUNT UNLOCK');
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
TO FPFORDEL_UNIT;

-- ###############################
-- ### Opplegg for lokal jetty ###
-- ###############################
DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPFORDEL';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPFORDEL IDENTIFIED BY fpfordel PROFILE DEFAULT ACCOUNT UNLOCK');
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
TO FPFORDEL;

ALTER USER FPFORDEL QUOTA UNLIMITED ON SYSTEM;
