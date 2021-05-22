## Goal for the first milestone

1. Build an asynchronous & non-blocking REST application using the Play framework and reactive mongo plugin
2. Basic API that allows us to perform the basic CRUD operations for the data stored in a mongo database.

### About ReactiveMongo
[ReactiveMongo](http://reactivemongo.org/) is an asynchronous and non-blocking Scala driver for MongoDB. 
In addition to performing standard CRUD operations, it also supports querying the data as a stream, which helps to process a large amount of data with minimal overhead.

### Resources
- [Reactive Scala Driver for MongoDB for Play](http://reactivemongo.org/releases/1.0/documentation/tutorial/play.html)

### Technologies used

- Scala 2.13
- Sbt 1.13
- Play 2.8
- Docker images:
  - mongo
  - mongo-express
    
### Run the docker 
```
Run: docker-compose up
Mongo express: http://localhost:8081/

# Connect using mongo shell
docker ps
docker exec -it <ContainerID> bash
mongo mongodb://localhost:27017 -u rootuser -p rootpass

# list all the databases
show dbs;

# Create or switch to database
use blogs;

# Run below functions inside the blogs database
db.getName();
db.createCollection("Hello World!");

# Delete the database
db.dropDatabase();

db.help();

# Collections
db.createCollection("person");
show collections
db.person.stats();
db.person.drop();

# Collections with configuration
db.createCollection("person", {capped: true, size: 6142800, max: 3000});
db.person.stats()
```

### About the web project

- Build a simple initial point for the blog application
- Should have a CRUD operation for the project