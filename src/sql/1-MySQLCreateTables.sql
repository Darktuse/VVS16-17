-- Indexes for primary keys have been explicitly created.

-- ---------- Table for validation queries from the connection pool. ----------

DROP TABLE PingTable;
CREATE TABLE PingTable (foo CHAR(1));

-- ------------------------------ UserProfile ----------------------------------

ALTER TABLE Bid DROP FOREIGN KEY bidProductIdFK;
ALTER TABLE Bid DROP FOREIGN KEY bidWinnerUserFK;
ALTER TABLE Bid DROP FOREIGN KEY bidUserFK;

ALTER TABLE Product DROP FOREIGN KEY productUserFK;
ALTER TABLE Product DROP FOREIGN KEY productCategFK;
ALTER TABLE Product DROP FOREIGN KEY productBidFK;

DROP TABLE Category;
DROP TABLE Bid;
DROP TABLE UserProfile;
DROP TABLE Product;

CREATE TABLE UserProfile (
    usrId BIGINT NOT NULL AUTO_INCREMENT,
    loginName VARCHAR(30) COLLATE latin1_bin NOT NULL,
    enPassword VARCHAR(13) NOT NULL, 
    firstName VARCHAR(30) NOT NULL,
    lastName VARCHAR(40) NOT NULL, email VARCHAR(60) NOT NULL,
    CONSTRAINT UserProfile_PK PRIMARY KEY (usrId),
    CONSTRAINT LoginNameUniqueKey UNIQUE (loginName)) 
    ENGINE = InnoDB;
	CREATE INDEX UserProfileIndexByLoginName ON UserProfile (loginName);


CREATE TABLE Category (
	categoryId BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(20),
	CONSTRAINT category_PK PRIMARY KEY(categoryId));


CREATE TABLE Bid( 
	bidId BIGINT NOT NULL AUTO_INCREMENT,
	userBidId BIGINT NOT NULL,
	amount DECIMAL(17,2) NOT NULL CHECK (amount > 0),
	dateInit TIMESTAMP DEFAULT 0 NOT NULL, 
	currentPriceProduct DECIMAL(17,2) NOT NULL,
	currentWinnerProductId BIGINT DEFAULT NULL,
	productId BIGINT NOT NULL,
	CONSTRAINT bid_PK PRIMARY KEY(bidId),
	CONSTRAINT bidWinnerUserFK FOREIGN KEY(currentWinnerProductId) REFERENCES UserProfile(usrId) ON DELETE SET NULL,
	CONSTRAINT bidUserFK FOREIGN KEY(userBidId) REFERENCES UserProfile(usrId) ON DELETE CASCADE);



CREATE TABLE Product( 
	productId BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(40),
	description VARCHAR(60), 
	startingPrice DECIMAL(17,2) CHECK (startingPrice > 0),
	startingDate TIMESTAMP DEFAULT 0 NOT NULL,
	endingDate TIMESTAMP DEFAULT 0 NOT NULL,
	deliveryInformation VARCHAR(60),
	currentPrice DECIMAL(17,2) DEFAULT 0,
	ownerId BIGINT NOT NULL, 
	categoryId BIGINT NOT NULL,
	currentBidId BIGINT,
	version BIGINT,
	CONSTRAINT Product_PK PRIMARY KEY(productId),
	CONSTRAINT productUserFK FOREIGN KEY(ownerId) REFERENCES UserProfile(usrId) ON DELETE CASCADE,
	CONSTRAINT productCategFK FOREIGN KEY(categoryId) REFERENCES Category(categoryId),
	CONSTRAINT productBidFK FOREIGN KEY(currentBidId) REFERENCES Bid(bidId) );

ALTER TABLE Bid ADD CONSTRAINT bidProductIdFK FOREIGN KEY(productId)
    REFERENCES Product (productId);
