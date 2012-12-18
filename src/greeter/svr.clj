(ns greeter.svr
  (:gen-class)
  (:use compojure.core)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [compojure.handler :as handler]
            [compojure.route   :as route]
            [greeter.util :refer :all]
            [greeter.db :refer :all]
            [greeter.thymeleaf :refer :all]))
 

;; Set up the DB
(drop-table-from h2db)
(create-table-from h2db)
(insert-records-in h2db)

;; Route/handler bindings        
(defroutes app-routes
  (GET "/:forename" [forename]
    (let [engine  (create-engine)
          surname (lookup-surname-of forename h2db)
          context (create-context { "forename" forename "surname" surname })] 
      (.process engine "greeting" context)))

  (route/not-found 
    (let [engine  (create-engine)
          names   (strip-keywords-from-array (all-names-from h2db))
          context (create-context { "allnames" names})]
      (.process engine "index" context))))

(def app
  (handler/site app-routes))

(defn -main []
  (run-jetty app {:port 3000}))