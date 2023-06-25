ALTER TABLE `Product` ADD CONSTRAINT `Product_fk0` FOREIGN KEY (`Ordr_ID`) REFERENCES `Order`(`Order_ID`);

ALTER TABLE `Order` ADD CONSTRAINT `Order_fk0` FOREIGN KEY (`Cust_ID`) REFERENCES `Customer`(`Customer_ID`);
