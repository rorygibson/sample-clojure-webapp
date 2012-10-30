# Sample Clojure webapp (with DB access)

Prerequisites: 
* A MySQL database called _helloworld_
* Leiningen 2 on your path

```bash
rgibson@thingy:~$ lein deps
rgibson@thingy:~$ lein ring server
```

This will actually fire up a browser frame to click around.
Or hit http://localhost:3000 to do it from $BROWSER_OF_CHOICE.

Try http://localhost:3000/Mick
