--
-- Ma_Sys.ma DirStat 2.0.0.0 Database Table Drop Script 1.0.0.0
-- Copyright (c) 2014 Ma_Sys.ma.
-- For further info send an e-mail to Ma_Sys.ma@web.de
--

-- __        __               _             
-- \ \      / /_ _ _ __ _ __ (_)_ __   __ _ 
--  \ \ /\ / / _` | '__| '_ \| | '_ \ / _` |
--   \ V  V / (_| | |  | | | | | | | | (_| |
--    \_/\_/ \__,_|_|  |_| |_|_|_| |_|\__, |
--                                    |___/ 
--
-- Be very careful with this.
-- Do not source it unless you know what you are doing. Then: \i PATH
--
--			THIS SCRIPT DELETES ALL DATA

-- The transaction ensures we do not delete anything which is not exactly
-- equivalent to what we want.
BEGIN TRANSACTION;
DROP TABLE errors;
DROP TYPE  error_type;
DROP TABLE optional_attrs;
DROP INDEX file_ext_index;
DROP TABLE files;
DROP TABLE scans;
END;
