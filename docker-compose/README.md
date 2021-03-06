docker-compose
==============

Example of using the ```sbt-docker-compose``` plugin. 
https://github.com/Tapad/sbt-docker-compose

Runs 3 containers, mongodb, mongoexpress (web admin UI for mongo), and a home grown scala akka-http front end
that talks to mongo

```sbt dockerComposeUp```

This will create your image and bring it up, showing your instance with ```sbt dockerComposeInstances```

You will be be presented with a few example commands

On the command shell ```docker ps``` should show you

```
mongo
mongo-express
webapp-scala
```

```mongo-express``` is on :8081

```mongo``` is on :27017

```webapp-scala``` is on :8080

```
webapp-scala /=> "Say hello to akka-http from <hostname>"  
webapp-scala /stuff=> lists json from /test/stuff collection on mongo
webapp-scala /insert=> inserts random data into /test/stuff collection on mongo
```

```mongo-express``` lets you view/edit the mongo data on mongo

```mongo``` data mounted inside docker host
e.g. ```docker-machine ssh``` to log into host, ```ls /data``` should show the mongo data
You can stop mongo, start again, and data will be still there

To stop your stack, ```sbt dockerComposeStop```

Being inside ```sbt``` and running ```dockerComposeRestart``` is very useful when coding

## Testing

You can run up the stack and run your unit/integration tests with 
```sbt dockerComposeTest```

Once it has run, you will also find an html report inside ```target/htmldir/index.html```

You should see 2 test classes, one for the WebServer (which then talks to Mongo), as well
as one for Mongo directly.

As you can see, this is a nice way to compose/orchestrate your microservice stack

More info here 
https://github.com/Tapad/sbt-docker-compose#to-execute-scalatest-test-cases-against-a-running-instance
