CREATE TABLE WritingGroup(
groupName VARCHAR(50) PRIMARY KEY,
headWriter VARCHAR(15),
yearFormed int,
subject VARCHAR(20)
);

CREATE TABLE Publishers(
publisherName VARCHAR(50) PRIMARY KEY,
publisherAddress VARCHAR(50),
publisherPhone VARCHAR(15),
publisherEmail VARCHAR(50)
);

CREATE TABLE Books(
groupName VARCHAR(50),
bookTitle VARCHAR(50),
publisherName VARCHAR(50),
yearPublished int,
numberOfPages int,
FOREIGN KEY(groupName) REFERENCES WritingGroup(groupName),
FOREIGN KEY(publisherName) REFERENCES Publishers(publisherName)
);

insert into WritingGroup(groupName, headWriter, yearFormed, subject)
values ('Riot Games', 'Ghostcrawler', 2009, 'Journalism'),
       ('Lizzard Games', 'Bobby', 2015, 'Gaming'),
       ('Panic Button Games', 'James', 2013, 'Strategy');

insert into Publishers(publisherName, publisherAddress, publisherPhone, publisherEmail)
values ('Riot Publishing', '12333 W Olympic Blvd', '310-267-8402', 'ritopublishing@ritogaems.com'),
      ('Blizzard Activision', '3100 Ocean Park Blvd', '219-482-3849', 'blizzardpublishing@blizzard.com'),
      ('Digital Extremes', '355 Wellington St', '839-943-7193', 'digitalextremespublishing@digital.com');

insert into Books(groupName, bookTitle, publisherName, yearPublished, numberOfPages)
values ('Riot Games', 'Riots Guide To Spaghetti Code', 'Riot Publishing', 2012, 300),
      ('Lizzard Games', 'How To Ruin Beloved Franchises', 'Blizzard Activision', 2019,  250),
      ('Panic Button Games', 'How To Make A Great Free To Play Game', 'Digital Extremes', 2017, 400); 