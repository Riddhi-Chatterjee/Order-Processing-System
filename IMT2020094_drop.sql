use IMT2020094_DB;

alter table `Product`
drop FOREIGN KEY `Product_fk0`;

alter table `Order`
drop FOREIGN KEY `Order_fk0`;

drop table `Customer`;

drop table `Order`;

drop table `Product`;

drop database IMT2020094_DB;