alter session set "_oracle_script"=true;
DECLARE
    userexists INTEGER;
BEGIN
    SELECT count(*)
    INTO userexists
    FROM SYS.ALL_USERS
    WHERE USERNAME = 'FPSAK';
    IF (userexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE USER FPSAK IDENTIFIED BY fpsak PROFILE DEFAULT ACCOUNT UNLOCK');
    END IF;
END;
/

DECLARE
    userexists INTEGER;
BEGIN
    SELECT count(*)
    INTO userexists
    FROM SYS.ALL_USERS
    WHERE USERNAME = 'FPSAK_HIST';
    IF (userexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE USER FPSAK_HIST IDENTIFIED BY fpsak_hist PROFILE DEFAULT ACCOUNT UNLOCK');
    END IF;
END;
/

DECLARE
    userexists INTEGER;
BEGIN
    SELECT count(*)
    INTO userexists
    FROM SYS.ALL_USERS
    WHERE USERNAME = 'FPSAK_UNIT';
    IF (userexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE USER FPSAK_UNIT IDENTIFIED BY fpsak_unit PROFILE DEFAULT ACCOUNT UNLOCK');
    END IF;
END;
/

DECLARE
    userexists INTEGER;
BEGIN
    SELECT count(*)
    INTO userexists
    FROM SYS.ALL_USERS
    WHERE USERNAME = 'FPSAK_HIST_UNIT';
    IF (userexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE USER FPSAK_HIST_UNIT IDENTIFIED BY fpsak_hist_unit PROFILE DEFAULT ACCOUNT UNLOCK');
    END IF;
END;
/

DECLARE
    userexists INTEGER;
BEGIN
    SELECT count(*)
    INTO userexists
    FROM SYS.ALL_USERS
    WHERE USERNAME = 'VL_DBA';
    IF (userexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE USER VL_DBA IDENTIFIED BY vl_dba PROFILE DEFAULT ACCOUNT UNLOCK');
    END IF;
END;
/

-- 1 Role for vl_dba
GRANT DBA TO VL_DBA;
ALTER USER VL_DBA DEFAULT ROLE ALL;

-- 2 System Privileges for vl_dba
GRANT CREATE SESSION TO VL_DBA;
GRANT UNLIMITED TABLESPACE TO VL_DBA;

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
    TO FPSAK;

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
    TO FPSAK_HIST;

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
    TO FPSAK_UNIT;

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
    TO FPSAK_HIST_UNIT;

-- Ikke endre rollenavn, den er referert i migreringsskriptene og skal finnes i alle miljøer inkl prod
DECLARE
    roleexists INTEGER;
BEGIN
    SELECT count(*)
    INTO roleexists
    FROM SYS.DBA_ROLES
    WHERE ROLE = 'FPSAK_HIST_SKRIVE_ROLE';
    IF (roleexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE ROLE FPSAK_HIST_SKRIVE_ROLE');
    END IF;
END;
/

-- Ikke endre rollenavn, den er referert i migreringsskriptene og skal finnes i alle miljøer inkl prod
DECLARE
    roleexists INTEGER;
BEGIN
    SELECT count(*)
    INTO roleexists
    FROM SYS.DBA_ROLES
    WHERE ROLE = 'FPSAK_HIST_LESE_ROLE';
    IF (roleexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE ROLE FPSAK_HIST_LESE_ROLE');
    END IF;
END;
/

GRANT FPSAK_HIST_SKRIVE_ROLE TO FPSAK;
GRANT FPSAK_HIST_SKRIVE_ROLE TO FPSAK_UNIT;

ALTER USER FPSAK QUOTA UNLIMITED ON SYSTEM;
ALTER USER FPSAK_UNIT QUOTA UNLIMITED ON SYSTEM;

ALTER USER FPSAK_HIST QUOTA UNLIMITED ON SYSTEM;
ALTER USER FPSAK_HIST_UNIT QUOTA UNLIMITED ON SYSTEM;
