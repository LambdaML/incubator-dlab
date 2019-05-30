var MongoClient = require('mongodb').MongoClient;

MongoClient.connect("mongodb://localhost:27017/MyDb", function (err, db) {
    use dlabdb
    dlabdb.createUser(
        {
          user: "admin",
          pwd: "mongo_passwd",
          roles: [
             { role: "userAdminAnyDatabase", db: "admin" },
             "readWrite"
          ]
        }
    );
