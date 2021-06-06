## GridFS

GridFS is a specification for storing and retrieving files that exceed the BSON document size limit of 16MB. Instead of storing a file in a single document, GridFS divides a file into parts, or chunks, and stores each of those chunks as a separate document.

When you query a GridFS store for a file, the Scala driver will reassemble the chunks as needed.

Play2-ReactiveMongo makes it easy to serve and store files in a complete non-blocking manner. 
It provides a body parser for handling file uploads, and a method to serve files from a GridFS store.

The maximum size of upload using the GridFS provided by a MongoController can be configured by the Play `DefaultMaxDiskLength`.

### Resources
- [Reactive Scala Driver for MongoDB](http://reactivemongo.org/releases/1.0/documentation/advanced-topics/gridfs.html)