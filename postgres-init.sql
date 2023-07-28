CREATE USER script_user;

DROP DATABASE IF EXISTS user_db;
DROP DATABASE IF EXISTS wallet_db;

CREATE DATABASE user_db;
CREATE DATABASE wallet_db;

GRANT ALL PRIVILEGES ON DATABASE user_db TO script_user;
GRANT ALL PRIVILEGES ON DATABASE wallet_db TO script_user;

\c user_db;
CREATE TABLE IF NOT EXISTS public.user_tb
(
    user_id uuid NOT NULL,
    created_at date,
    first_name text COLLATE pg_catalog."default" NOT NULL,
    last_name text COLLATE pg_catalog."default" NOT NULL,
    social_security_number text COLLATE pg_catalog."default",
    username text COLLATE pg_catalog."default" NOT NULL,
    wallet_id uuid,
    CONSTRAINT user_tb_pkey PRIMARY KEY (user_id),
    CONSTRAINT uk_lvx22t2upvjxxc86vf5daxc71 UNIQUE (username)
);

\c wallet_db;
CREATE TABLE IF NOT EXISTS public.wallet_tb (
    wallet_id uuid NOT NULL,
    balance numeric(19,2),
    user_id uuid,
    CONSTRAINT wallet_tb_pkey PRIMARY KEY (wallet_id)
);

\c wallet_db;
CREATE TABLE IF NOT EXISTS public.transaction_tb (
    transaction_id uuid NOT NULL,
    amount numeric(19,2),
    bill_value numeric(19,2),
    "timestamp" date,
    transaction_type text COLLATE pg_catalog."default",
    target_wallet_id uuid,
    wallet_id uuid NOT NULL,
    CONSTRAINT transaction_tb_pkey PRIMARY KEY (transaction_id),
    CONSTRAINT fkc6u2meix32g5j1u36pvchwvxe FOREIGN KEY (wallet_id)
        REFERENCES public.wallet_tb (wallet_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fklllx7i80u1rsfbi1e8dal6mkm FOREIGN KEY (target_wallet_id)
        REFERENCES public.wallet_tb (wallet_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

\c user_db;
INSERT INTO public.user_tb(user_id, created_at, first_name, last_name, social_security_number, username)
    VALUES ('40f8780c-99aa-4c95-9096-2060116b398e', '05/06/2023 04:04', 'John', 'Durante', '47578853856', 'johndcx45');
