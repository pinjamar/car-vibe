 # Add AutoAdverts and Fuel Types DBs

# --- !Ups
create table FuelTypes (
    id integer primary key AUTOINCREMENT,
    name varchar(64) not null
);

insert into FuelTypes (id, name) values (1, 'gasoline');
insert into FuelTypes (id, name) values (2, 'diesel');
insert into FuelTypes (id, name) values (3, 'hybrid');
insert into FuelTypes (id, name) values (4, 'mild-hybrid');
insert into FuelTypes (id, name) values (5, 'plug-in-hybrid');
insert into FuelTypes (id, name) values (6, 'battery-electric');
insert into FuelTypes (id, name) values (7, 'hydrogen');
insert into FuelTypes (id, name) values (8, 'hydrogen-electric');

create table AutoAdverts(
	id integer primary key AUTOINCREMENT,
	title varchar(128) not null,
	fuel_type_id integer not null,
	price real not null default 0,
	mileage integer null,
	is_new integer not null default 1,
	first_registration text null,

	foreign key (fuel_type_id) references FuelTypes(id)
);

# --- !Downs
DROP TABLE AutoAdverts;
DROP TABLE FuelTypes;