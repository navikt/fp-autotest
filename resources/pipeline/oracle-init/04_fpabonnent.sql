DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPABONNENT';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPABONNENT IDENTIFIED BY fpabonnent  PROFILE DEFAULT ACCOUNT UNLOCK');
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
TO FPABONNENT;

ALTER USER FPABONNENT QUOTA UNLIMITED ON SYSTEM;

DECLARE userexists INTEGER;
BEGIN
SELECT count(*)
INTO userexists
FROM SYS.ALL_USERS
WHERE USERNAME = 'FPABONNENT_UNIT';
IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPABONNENT_UNIT IDENTIFIED BY fpabonnent_unit  PROFILE DEFAULT ACCOUNT UNLOCK');
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
TO FPABONNENT_UNIT;