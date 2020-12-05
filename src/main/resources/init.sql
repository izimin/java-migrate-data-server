drop table if exists public.ref_competition_athlete;
drop table if exists public.competition;
drop table if exists public.kind_of_sport;
drop table if exists public.athlete;
drop table if exists public.championship;
drop table if exists public.country;

create table if not exists public.kind_of_sport
(
    id  serial  not null constraint kinds_of_sports_pk primary key,
    name varchar not null
);

comment on table public.kind_of_sport is 'Вид спорта';

comment on column public.kind_of_sport.id is 'Идентификатор вида спорта';

comment on column public.kind_of_sport.name is 'Наименование вида спорта';

create table if not exists public.athlete
(
    id   serial  not null constraint athlete_pk primary key,
    name varchar not null,
    age  integer
);

comment on table public.athlete is 'Спортсмен';

comment on column public.athlete.id is 'Идентификатор спортсмена';

comment on column public.athlete.name is 'ФИО спортсмена';

comment on column public.athlete.age is 'Возраст спортсмена';

create table if not exists public.country
(
    id   bigserial    not null constraint country_pk primary key,
    name varchar(255) not null
);

create table if not exists public.championship
(
    id         serial not null constraint championship_pk primary key,
    year       integer,
    country_id bigint constraint championship_country_id_fk references public.country

);

comment on table public.championship is 'Чемпионат мира';

comment on column public.championship.id is 'Идентификатор чемпионата мира';

comment on column public.championship.year is 'Год проведения ЧМ';

create table if not exists public.competition
(
    id               serial not null constraint competition_pk primary key,
    kind_of_sport_id integer constraint competition_kind_of_sport_id_fk references public.kind_of_sport,
    championship_id  integer constraint competition_championship_id_fk references public.championship
);

comment on table public.competition is 'Соревнование';

comment on column public.competition.id is 'Идентификатор соревнования';

comment on column public.competition.kind_of_sport_id is 'Вид спорта';

comment on column public.competition.championship_id is 'Идентификатор ЧМ';

create unique index competition_id_kind_of_sport_id_championship_id_uindex
    on competition (id, kind_of_sport_id, championship_id);

create table if not exists public.ref_competition_athlete
(
    competition_id integer constraint ref_competition_athlete_competition_id_fk references public.competition,
    athlete_id     integer constraint ref_competition_athlete_athlete_id_fk references public.athlete
);

comment on column public.ref_competition_athlete.competition_id is 'Идентификатор соревнования';

comment on column public.ref_competition_athlete.athlete_id is 'Идентификатор спортсмена';

create unique index ref_competition_athlete_competition_id_athlete_id_uindex
    on ref_competition_athlete (competition_id, athlete_id);
