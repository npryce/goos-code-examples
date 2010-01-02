

CREATE TABLE CreditCardDetails (
    id INTEGER PRIMARY KEY, 
    cardNumber VARCHAR(16) NOT NULL, 
    nameOnCard VARCHAR(80) NOT NULL, 
    expiryDate DATE NOT NULL,
    billingAddress_id INTEGER NOT NULL
);

CREATE TABLE PaymentMethod (
    id INTEGER PRIMARY KEY
);

CREATE TABLE Customer (
    id INTEGER PRIMARY KEY, 
    name VARCHAR(80) NOT NULL,
    email VARCHAR(80),
    address_id INTEGER NOT NULL
);

CREATE TABLE Customer_PaymentMethod (
    customer_id INTEGER NOT NULL, 
    paymentMethods_id INTEGER NOT NULL,
    PRIMARY KEY (customer_id, paymentMethods_id)
);

CREATE TABLE AuctionSite (
    id INTEGER PRIMARY KEY, 
    name VARCHAR(32) NOT NULL, 
    siteURL VARCHAR(255) NOT NULL
);

CREATE TABLE AuctionSiteCredentials (
    id INTEGER PRIMARY KEY,
    auctionSite_id INTEGER NOT NULL,
    userName VARCHAR(255) NOT NULL, 
    password VARCHAR(255) NOT NULL
);


CREATE TABLE PayMateDetails (
    id INTEGER PRIMARY KEY,
    userName VARCHAR(255) NOT NULL, 
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Address (
    id INTEGER PRIMARY KEY, 
    street VARCHAR(255), 
    town VARCHAR(255),
    postcode VARCHAR(255), 
    country VARCHAR(255)
);

CREATE TABLE Customer_AuctionSiteLogin (
    customer_id INTEGER NOT NULL, 
    auctionSiteLogins_id INTEGER NOT NULL,
   
    PRIMARY KEY (customer_id, auctionSiteLogins_id)
);

CREATE TABLE HIBERNATE_UNIQUE_KEY (
    next_hi INTEGER
);

-- ----------------------------------------------
-- DDL Statements for keys
-- ----------------------------------------------

-- foreign
ALTER TABLE Customer 
	ADD CONSTRAINT F1 
	FOREIGN KEY (Address_id) 
	REFERENCES Address (id) 	
	ON DELETE CASCADE 
	ON UPDATE NO ACTION;

ALTER TABLE Customer_PaymentMethod 
	ADD CONSTRAINT F2 
	FOREIGN KEY (PaymentMethods_id) 
	REFERENCES PaymentMethod (id) 
	ON DELETE CASCADE
	ON UPDATE NO ACTION;

ALTER TABLE Customer_PaymentMethod 
	ADD CONSTRAINT F3 
	FOREIGN KEY (customer_id) 
	REFERENCES Customer (id) 
	ON DELETE CASCADE
	ON UPDATE NO ACTION;

ALTER TABLE Customer_AuctionSiteLogin
	ADD CONSTRAINT F4 
	FOREIGN KEY (customer_id) 
	REFERENCES Customer (id) 
	ON DELETE CASCADE 
	ON UPDATE NO ACTION;
	
ALTER TABLE Customer_AuctionSiteLogin
    ADD CONSTRAINT F5
    FOREIGN KEY (auctionSiteLogins_id)
    REFERENCES AuctionSiteLogin (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE PaymateDetails 
	ADD CONSTRAINT F6 
	FOREIGN KEY (id) 
	REFERENCES PaymentMethod (id) 
	ON DELETE CASCADE 
	ON UPDATE NO ACTION;

ALTER TABLE CreditCardDetails 
	ADD CONSTRAINT F7 
	FOREIGN KEY (id) 
	REFERENCES PaymentMethod (ID) 
	ON DELETE CASCADE 
	ON UPDATE NO ACTION;

ALTER TABLE CreditCardDetails 
	ADD CONSTRAINT F8 
	FOREIGN KEY (BillingAddress_id) 
	REFERENCES Address (id) 
	ON DELETE CASCADE 
	ON UPDATE NO ACTION;
	
INSERT INTO HIBERNATE_UNIQUE_KEY (next_hi) VALUES (0);
