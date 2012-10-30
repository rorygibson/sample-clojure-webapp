(defproject hello "0.1.0-SNAPSHOT"
  :description "A sample clojure webapp"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [ring/ring-core "1.1.1"]
                 [ring/ring-jetty-adapter "1.1.1"]
                 [org.clojure/java.jdbc "0.0.6"]
                 [mysql/mysql-connector-java "5.1.6"]]

  :plugins [[lein-ring "0.7.3"]]
  :ring {:handler hello.hello/app}
  :main hello.hello)
