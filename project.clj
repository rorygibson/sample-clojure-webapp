(defproject hello-world "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [org.clojure/java.jdbc "0.0.6"]
                 [mysql/mysql-connector-java "5.1.6"]]
  :plugins [[lein-ring "0.7.3"]]
  :ring {:handler hello-world.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
