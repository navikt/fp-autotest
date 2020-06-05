-- ###############################
-- ### Opplegg for lokal jetty ###
-- ###############################
DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPTILBAKE';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPTILBAKE IDENTIFIED BY fptilbake');
  END IF;
END;
/

 GRANT CONNECT, RESOURCE, CREATE JOB, CREATE TABLE, CREATE SYNONYM, CREATE VIEW, CREATE MATERIALIZED VIEW TO FPTILBAKE;
