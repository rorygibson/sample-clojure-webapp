(ns hello-world.handler
  (:use compojure.core)
  (:require [clojure.java.jdbc :as sql])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defn drop-names
  [conn]
  (try
    (sql/with-connection conn
      (sql/drop-table :names))
    (catch Exception e (.getNextException e))))

(defn create-names
  [conn]
  (sql/with-connection conn
    (sql/create-table :names
      [:first "varchar(32)" "PRIMARY KEY"]
      [:second "varchar(32)"])))

(defn insert-records
  [conn]
  (sql/with-connection conn
    (sql/insert-records :names
      {:first "Rory" :second "Gibson"}
      {:first "Bilbo" :second "Baggins"}
      {:first "Martin" :second "Fowler"}
      {:first "James" :second "Dean Bradley"}
      {:first "Mick" :second "Jagger"})))

(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//localhost:3306/helloworld"
         :user "root"
         :password ""})

(drop-names db)
(create-names db)
(insert-records db)

(defroutes app-routes
  (GET "/:fname" [fname]
  	(sql/with-connection db
	  (sql/with-query-results rows
	    ["select second from names where first=?" fname]
	    (str "Hello, " fname " " (:second (first rows))))))

  (GET "/" [] "<h1>Missing name</h1><p>You need to supply a name: try <a href=\"http://localhost:3000/Rory\">Rory</a></p>")

  (route/not-found "<h1>Uh oh</h1><p>You tried to find a page that doesn't exist</p>"))

(def app
  (handler/site app-routes))