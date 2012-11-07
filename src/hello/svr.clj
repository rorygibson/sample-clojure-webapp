(ns hello.svr
  (:gen-class)
  (:use compojure.core)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [compojure.handler :as handler]
            [compojure.route   :as route]
            [hello.util]
            [hello.db]
            [hello.thymeleagf]))
  

;; Set up the DB
(drop-table-from db)
(create-table-from db)
(insert-records-in db)

;; Route/handler bindings        
(defroutes app-routes
  (GET "/:forename" [forename]
    (let [engine  (create-engine)
          surname (lookup-surname-of forename db)
          context (create-context { "forename" forename "surname" surname })] 
      (.process engine "hello" context)))

  (route/not-found 
    (let [engine  (create-engine)
          names   (strip-keywords-from-array (all-names-from db))
          context (create-context { "allnames" names})]
      (.process engine "index" context))))

(def app
  (handler/site app-routes))

(defn -main []
  (run-jetty app {:port 3000}))