(defproject sockplay "0.1.0"
  :description "An (still) ugly implementation of multi-rooms webchat using Clojure http-kit websocket & angular"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [http-kit "2.1.16"]
                 [selmer "0.6.7"]
                 [cheshire "5.3.1"]
                 [lib-noir "0.8.4"]
                 [couchbase-clj "0.1.3"]
                 [com.ashafa/clutch "0.4.0-RC1"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler sockplay.handler/app}
  :main sockplay.handler
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
