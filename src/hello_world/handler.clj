(ns hello-world.handler
  (:use compojure.core)
  (:require [clojure.java.jdbc :as sql]
            [compojure.handler :as handler]
            [compojure.route   :as route]))

; Database creation and population
(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//localhost:3306/helloworld"
         :user "root"
         :password ""})

(defn drop-table-from
  [conn]
  (try
    (sql/with-connection conn
      (sql/drop-table :names))
    (catch Exception e (.getNextException e))))

(defn create-table-from
  [conn]
  (sql/with-connection conn
    (sql/create-table :names
      [:first "varchar(32)" "PRIMARY KEY"]
      [:second "varchar(32)"])))

(defn insert-records-in
  [conn]
  (sql/with-connection conn
    (sql/insert-records :names
      {:first "Rory" :second "Gibson"}
      {:first "Bilbo" :second "Baggins"}
      {:first "Martin" :second "Fowler"}
      {:first "James" :second "Dean Bradley"}
      {:first "Mick" :second "Jagger"})))

; Getting stuff from the DB
(defn all-names-from
  [conn]
    (sql/with-connection conn
      (sql/with-query-results rows
        ["select first, second from names"]
        (doall rows))))     

(defn lookup-surname-of 
  [firstname conn]  
    (sql/with-connection conn
      (sql/with-query-results rows
        ["select second from names where first=?" firstname]
        (:second (first rows)))))

; Generating HTML
(defn li-for 
  [name-map]
  (str "<li> <a href=\"/" (:first name-map) "\">" (:first name-map) " " (:second name-map) "</a></li>"))   

(defn ul-of 
  [seq-of-name-maps]
  (str "<ul>" (apply str (map li-for seq-of-name-maps)) "</ul>"))

(defn hello
  [fname sname]
  (if (nil? sname)
            (str "Hello " fname ", have we met before?")
            (str "Hello " fname " " sname ", nice to see you again")))

; Actually set up the DB
(drop-table-from db)
(create-table-from db)
(insert-records-in db)

; Compojure route/handler bindings
(defroutes app-routes
  (GET "/:firstname" [firstname]
    (let [surname (lookup-surname-of firstname db)]
      (hello firstname surname)))          

  (route/not-found 
    (let [heading "<h1>Uh oh</h1>"
          para-1  "<p>That's not how you say hello.</p>"
          hint    "<p>Are you one of these people?</p>"]
      (str heading para-1 hint (ul-of (all-names-from db))))))

(def app
  (handler/site app-routes))