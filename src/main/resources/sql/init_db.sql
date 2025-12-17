-- 1. Create the User (if not exists)
-- DROP USER IF EXISTS commerce_core;
CREATE USER commerce_core WITH PASSWORD 'zxcl123123';

-- 2. Create the Database
-- DROP DATABASE IF EXISTS commerce_core;
CREATE DATABASE commerce_core OWNER commerce_core;

-- 3. Grant Privileges (Optional if owner is set, but good for safety)
GRANT ALL PRIVILEGES ON DATABASE commerce_core TO commerce_core;

-- \c commerce_core
-- GRANT ALL ON SCHEMA public TO commerce_core;
