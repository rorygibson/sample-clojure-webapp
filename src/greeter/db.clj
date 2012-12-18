(ns greeter.db
  (:require [clojure.java.jdbc :as sql]))  

;; Database creation and population
(def mysqldb {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//localhost:3306/helloworld"
         :user "root"
         :password ""})

(def h2db {:classname   "org.h2.Driver"
           :subprotocol "h2:file"
           :subname     (str (System/getProperty "user.dir") "/" "greeter")
           :user        "sa"
           :password    "" })

(defn drop-table-from [conn]
  (try
    (sql/with-connection conn
      (sql/drop-table :names))
    (catch Exception e (.getNextException e))))

(defn create-table-from [conn]
  (sql/with-connection conn
    (sql/create-table :names
      [:forename "varchar(32)" "PRIMARY KEY"]
      [:surname "varchar(32)"])))

(defn insert-records-in [conn]
  (sql/with-connection conn
    (sql/insert-records :names
      {:forename "Rory" :surname "Gibson"} 
      {:forename "Bilbo" :surname "Baggins"} 
      {:forename "Martin" :surname "Fowler"}
      {:forename "James" :surname "Dean Bradley"} 
      {:forename "Mick" :surname "Jagger"})))

;; Getting stuff from the DB
(defn all-names-from [conn]
    (sql/with-connection conn
      (sql/with-query-results rows
        ["select forename, surname from names"]
        (doall rows))))     

(defn lookup-surname-of [forename conn]  
    (sql/with-connection conn
      (sql/with-query-results rows
        ["select surname from names where forename=?" forename]
        (:surname (first rows)))))
