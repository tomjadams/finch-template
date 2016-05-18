create table users (
  id bigserial primary key,
  name text not null,
  email text not null unique,
  avatar_url text not null,
  facebook_token text not null unique,
  auth_token text unique
);

create unique index name_idx on users (name);
create unique index email_idx on users (email);
create unique index facebook_token_idx on users (facebook_token);
create unique index auth_token_idx on users (auth_token);
