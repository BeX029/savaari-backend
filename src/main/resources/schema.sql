CREATE TABLE FareConfig (
    vehicleType VARCHAR(20),
    baseFare DOUBLE,
    perKmRate DOUBLE
);

INSERT INTO FareConfig VALUES ('Bike', 50, 10);
INSERT INTO FareConfig VALUES ('Car', 100, 20);