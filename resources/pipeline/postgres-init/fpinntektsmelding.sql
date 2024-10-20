CREATE DATABASE fpinntektsmelding_unit;
CREATE USER fpinntektsmelding_unit WITH PASSWORD 'fpinntektsmelding_unit';
GRANT ALL ON DATABASE fpinntektsmelding_unit TO fpinntektsmelding_unit;
ALTER DATABASE fpinntektsmelding_unit SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpinntektsmelding_unit OWNER TO fpinntektsmelding_unit;

CREATE DATABASE fpinntektsmelding;
CREATE USER fpinntektsmelding WITH PASSWORD 'fpinntektsmelding';
GRANT ALL ON DATABASE fpinntektsmelding TO fpinntektsmelding;
ALTER DATABASE fpinntektsmelding SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpinntektsmelding OWNER TO fpinntektsmelding;
