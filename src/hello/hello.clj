(ns hello.hello
  (:gen-class)
  (:use compojure.core)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:use ring.middleware.reload)
  (:require [clojure.java.jdbc :as sql]
            [compojure.handler :as handler]
            [compojure.route   :as route])
  (:import (java.util Date)
           (org.thymeleaf TemplateEngine)
           (org.thymeleaf.context Context)
           (org.thymeleaf.resourceresolver FileResourceResolver)
           (org.thymeleaf.templateresolver TemplateResolver)))
  

;; Database creation and population
(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//localhost:3306/helloworld"
         :user "root"
         :password ""})

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

;; Utility functions to translate database return values
(defn strip-keywords-from-map
  "Return a new map such that keyword keys are replaced with string keys"
  [m]
  (let [{:keys [forename surname]} m]
    { "forename" forename "surname" surname }))

(defn strip-keywords-from-array
  "Takes in [{:forename \"surname\"} {:forename2 \"surname2\"}] and gives back [{\"forename\" \"surname\"} {\"forename2\" \"surname2\"}]"
  [array-of-maps]
  (map strip-keywords-from-map array-of-maps))


;; ThymeLeaf integration
(defn create-engine []
  (let [tr (TemplateResolver.)]
    (.setResourceResolver tr (FileResourceResolver.))
    (.setTemplateMode tr "XHTML")
    (.setPrefix tr "src/hello/")
    (.setSuffix tr ".html")
    (let [engine (TemplateEngine.)]
      (.setTemplateResolver engine tr)
      engine)))

(defn create-context [m]
  (reduce (fn [c [k v]] (.setVariable c k v) c) (Context.) m))

;; Actually set up the DB
(drop-table-from db)
(create-table-from db)
(insert-records-in db)

;; Test data
(def all-names-static
  [{:forename "Rory" :surname "Gibson" } {:forename "James" :surname "Smith"}])

;; Compojure route/handler bindings        
(defroutes app-routes
  (GET "/:forename" [forename]
    (let [engine (create-engine)
          surname (lookup-surname-of forename db)
          context (create-context { "forename" forename "surname" surname })] 
      (.process engine "hello" context)))

  (route/not-found 
    (let [engine (create-engine)
          names (strip-keywords-from-array (all-names-from db))
          context (create-context { "allnames" names})]
      (.process engine "index" context))))

(def app
  (handler/site app-routes))

(defn -main []
  (run-jetty app {:port 3000}))