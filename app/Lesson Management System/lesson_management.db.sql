BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS Booking (id INTEGER PRIMARY KEY AUTOINCREMENT,booking_date TEXT,status BOOLEAN,client_id INTEGER,offering_id INTEGER,FOREIGN KEY (client_id) REFERENCES Client(id),FOREIGN KEY (offering_id) REFERENCES Offering(id));
CREATE TABLE IF NOT EXISTS Client (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,phone_number TEXT UNIQUE,age INTEGER NOT NULL,guardian_id INTEGER,FOREIGN KEY (guardian_id) REFERENCES Client(id));
CREATE TABLE IF NOT EXISTS Instructor (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,phone_number TEXT UNIQUE,specialization TEXT,start_date TEXT,end_date TEXT);
CREATE TABLE IF NOT EXISTS InstructorCities (id INTEGER PRIMARY KEY AUTOINCREMENT,instructor_id INTEGER,city TEXT NOT NULL,FOREIGN KEY (instructor_id) REFERENCES Instructor(id) ON DELETE CASCADE);
CREATE TABLE IF NOT EXISTS LessonType (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT UNIQUE);
CREATE TABLE IF NOT EXISTS Location (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,address TEXT,city TEXT,type_of_space TEXT);
CREATE TABLE IF NOT EXISTS Offering (id INTEGER PRIMARY KEY AUTOINCREMENT,lesson_type TEXT,is_group BOOLEAN,availability BOOLEAN,capacity INTEGER,start_time TEXT,end_time TEXT,has_instructor BOOLEAN DEFAULT 0,instructor_id INTEGER,schedule_id INTEGER,location_id INTEGER,FOREIGN KEY (instructor_id) REFERENCES Instructor(id),FOREIGN KEY (schedule_id) REFERENCES Schedule(id),FOREIGN KEY (location_id) REFERENCES Location(id));
CREATE TABLE IF NOT EXISTS Schedule (id INTEGER PRIMARY KEY AUTOINCREMENT,start_date TEXT,end_date TEXT,day_of_week INTEGER,start_time TEXT,end_time TEXT,location_id INTEGER,FOREIGN KEY (location_id) REFERENCES Location(id));
CREATE TABLE IF NOT EXISTS TypeOfSpace (id INTEGER PRIMARY KEY AUTOINCREMENT,space_type TEXT UNIQUE);
INSERT INTO "Booking" ("id","booking_date","status","client_id","offering_id") VALUES (1,'2023-09-10',1,1,1),
 (2,'2023-09-11',1,2,2),
 (3,'From: 10 to 12 on 2023-09-01',1,4,2);
INSERT INTO "Client" ("id","name","phone_number","age","guardian_id") VALUES (1,'James Russo','123-456-7890',40,NULL),
 (2,'Lisa Russo','123-456-7891',14,NULL),
 (3,'Emily Green','123-456-7892',30,NULL),
 (4,'Sadam','111-333-5555',14,1);
INSERT INTO "Instructor" ("id","name","phone_number","specialization","start_date","end_date") VALUES (1,'John Doe','111-222-3333','YOGA','2023-01-01','2023-12-31'),
 (2,'Jane Smith','222-333-4444','DANCE','2023-01-01','2023-12-31');
INSERT INTO "InstructorCities" ("id","instructor_id","city") VALUES (1,1,'New York'),
 (2,1,'Los Angeles'),
 (3,2,'Chicago');
INSERT INTO "LessonType" ("id","name") VALUES (1,'YOGA'),
 (2,'DANCE'),
 (3,'MUSIC'),
 (4,'ART');
INSERT INTO "Location" ("id","name","address","city","type_of_space") VALUES (1,'NYC Yoga Pool','123 Main St','New York','POOL'),
 (2,'LA Dance Room','456 Hollywood Blvd','Los Angeles','ROOM');
INSERT INTO "Offering" ("id","lesson_type","is_group","availability","capacity","start_time","end_time","has_instructor","instructor_id","schedule_id","location_id") VALUES (1,'YOGA',1,1,20,'09:00','11:00',1,1,1,1),
 (2,'DANCE',0,1,8,'10:00','12:00',1,2,2,2),
 (3,'YOGA',0,1,1,'13:00','15:00',0,NULL,1,1),
 (4,'DANCE',1,1,30,'14:00','16:00',0,NULL,2,2);
INSERT INTO "Schedule" ("id","start_date","end_date","day_of_week","start_time","end_time","location_id") VALUES (1,'2023-09-01','2023-12-01',1,'09:00','20:00',1),
 (2,'2023-09-01','2023-12-01',2,'10:00','20:00',2);
INSERT INTO "TypeOfSpace" ("id","space_type") VALUES (1,'POOL'),
 (2,'ROOM');
COMMIT;
