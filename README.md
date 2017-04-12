sbt-docker-helloworld
=====================

This is a simple hello world example to serve as a skeleton app to show your Scala code
running inside a docker container

It uses the ```sbt-native-packager``` plugin

First, make you you have docker installed and running

i.e. ```docker ps```

should return the running containers on your current docker host

Then go to the root of this project and run

```sbt docker:publishLocal```

It should build and end with

```
[info] Built image DockerAlias(None,None,sbt-docker-helloworld,Some(1.0))
[success] ...
```

```docker images```

should show your new image, named ```sbt-docker-helloworld```

To run

```docker run -it sbt-docker-helloworld:1.0```

You should see "Hello World" follow by the timestamp

Of course to run locally outside of docker, just run

```sbt run```

---

If you look inside ```target/docker``` you'll see the ```Dockerfile```, jars,
and ENTRYPOINT binaries

If you're curious you can shell into the image and take a look around

```docker run -it --entrypoint bash sbt-docker-helloworld:1.0```

e.g. ```bin/sbt-docker-helloworld``` will run the scala app

You can even supply your own Dockerfile if you so wish

http://sbt-native-packager.readthedocs.io/en/latest/formats/docker.html#custom-dockerfile

---

To read more about the sbt-native-packager Docker plugin

http://sbt-native-packager.readthedocs.io/en/latest/formats/docker.html

There is also another sbt-docker plugin

https://github.com/marcuslonnberg/sbt-docker