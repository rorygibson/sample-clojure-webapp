# Sample Clojure webapp (with DB access)

## Prerequisites: 
* Java 6
* A MySQL database called _helloworld_
* Leiningen 2 on your path

## Development mode:

```bash
rgibson@thingy:~$ lein deps
rgibson@thingy:~$ lein ring server
```

This will actually fire up a browser frame to click around.
Or hit http://localhost:3000 to do it from $BROWSER_OF_CHOICE.

## Production mode
```bash
rgibson@thingy:~$ lein uberjar
rgibson@thingy:~$ java -jar target/hello-0.1.0-SNAPSHOT-standalone.jar
```

Yep - fully packaged, no external dependencies except Java.



