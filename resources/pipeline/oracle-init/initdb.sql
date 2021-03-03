alter database set TIME_ZONE='Europe/Oslo';
alter database datafile 1 autoextend on maxsize 5G;
alter system set recyclebin=OFF DEFERRED;
alter profile default limit password_life_time unlimited;
alter system set processes=150 scope=spfile;
alter system set session_cached_cursors=100 scope=spfile;
alter system set session_max_open_files=100 scope=spfile;
alter system set sessions=100 scope=spfile;
alter system set license_max_sessions=100 scope=spfile;
alter system set license_sessions_warning=100 scope=spfile;
alter system set disk_asynch_io=FALSE scope=spfile;
alter profile default limit password_verify_function null;

DECLARE userexists INTEGER;
BEGIN
  SELECT count(*)
  INTO userexists
  FROM SYS.ALL_USERS
  WHERE USERNAME = 'vl_dba';
  IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER vl_dba IDENTIFIED BY vl_dba');
  END IF;
END;

GRANT CREATE USER, CONNECT, RESOURCE, DBA, ALTER SESSION TO vl_dba WITH ADMIN OPTION;

