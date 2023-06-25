create database IMT2020094_DB;
use IMT2020094_DB;

CREATE TABLE `Customer` (
	`Name` varchar(100) NOT NULL,
	`Address` varchar(200) NOT NULL,
	`Phone_number` varchar(10) NOT NULL,
	`Customer_number` INT NOT NULL UNIQUE,
	`Customer_ID` INT NOT NULL AUTO_INCREMENT UNIQUE,
	constraint `pk_Customer` PRIMARY KEY (`Customer_ID`)
);

CREATE TABLE `Product` (
	`Name` varchar(100) NOT NULL,
	`Type` varchar(100) NOT NULL,
	`Price` FLOAT NOT NULL,
	`Product_number` INT NOT NULL UNIQUE,
	`Product_ID` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`Ordr_ID` INT,
	constraint `pk_Product` PRIMARY KEY (`Product_ID`)
);

CREATE TABLE `Order` (
	`Description` varchar(200) NOT NULL,
	`Amount` FLOAT NOT NULL,
	`Order_date` DATE NOT NULL,
	`Delivery_date` DATE NOT NULL,
	`Order_number` INT NOT NULL UNIQUE,
	`Order_ID` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`Cust_ID` INT NOT NULL,
	constraint `pk_Order` PRIMARY KEY (`Order_ID`)
);