-- ###############################
-- ### Opplegg for lokal jetty ###
-- ###############################
DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'FPOPPDRAG';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPOPPDRAG IDENTIFIED BY fpoppdrag');
  END IF;
END;
/

GRANT CONNECT, RESOURCE, CREATE JOB, CREATE TABLE, CREATE SYNONYM, CREATE VIEW, CREATE MATERIALIZED VIEW TO FPOPPDRAG;
