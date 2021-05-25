## Goal for the first milestone

1. Build an asynchronous & non-blocking REST application using the Play framework and reactive mongo plugin
2. Basic API that allows to perform the basic CRUD operations for the data stored in a mongo database.
3. Use the docker compose file to build and run the containers for mongoDb and mongo express to view database content in UI
4. Implement Swagger API management tool to perform all the endpoints operation 
5. Implement test-container dependency to create test suite cases using docker container for mongodb
6. Use of play form to validate the request form while creating and updating a new blog post
7. Use of Akka stream and integrate with Reactive mongo to upload JSON content to the database at the start-up of Play application

### Future ideas to implement
- Add simple JWT authorization layer
- Add comment functionality using Play web socket
- Each blog can have title image background that need to be stored in mongo as GridFS
- Start Front-end project using Vue3 and Typescript 
- Added a simple github action job to test when merged to master

### About ReactiveMongo
[ReactiveMongo](http://reactivemongo.org/) is an asynchronous and non-blocking Scala driver for MongoDB. 
In addition to performing standard CRUD operations, it also supports querying the data as a stream, which helps to process a large amount of data with minimal overhead.

### Resources
- [Reactive Scala Driver for MongoDB for Play](http://reactivemongo.org/releases/1.0/documentation/tutorial/play.html)
- [testcontainers-scala for MongoDb](https://github.com/testcontainers/testcontainers-scala)
- [Play framework](https://www.playframework.com/)
- [Introduction to Reactive Mongo](https://www.baeldung.com/scala/mongo-reactive-intro)

### Online example projects
- [play-framework-blog in Java](https://github.com/reljicd/play-framework-blog)
- [Full stack ScalaBlog, but old](https://github.com/kairos34/ScalaBlog)
- [scala-play-blog with Web socket example](https://github.com/mykisscool/scala-play-blog)
- [play-mongo based on this project](https://github.com/smahjoub/play-mongo)
- [Official reactivemongo-demo-app](https://github.com/ReactiveMongo/reactivemongo-demo-app)

### Technologies used
- Scala 2.13
- Sbt 1.13
- Play 2.8
- ReactiveMongo
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

### Test used for the project:
- Testing your application with [ScalaTest in Play](https://www.playframework.com/documentation/2.8.x/ScalaTestingWithScalaTest)
- [testcontainers-scala](https://github.com/testcontainers/testcontainers-scala)

## Know issues
1. At the moment, if the docker container of MongoDb is initialized with the username and password, then the ReactiveMongo won't connect even using the required url.
Might need to look into more, see the document regarding [connection](http://reactivemongo.org/releases/1.0/documentation/tutorial/connect-database.html). 
   Hence, the environment variables for username and password are disabled in the docker-compose file.