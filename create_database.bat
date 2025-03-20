@echo off

set DB_NAME=scooter_rental
set DB_USER=postgres

psql -U %DB_USER% -c "CREATE DATABASE %DB_NAME%;"
