 (defproject le-guin "0.1.0-SNAPSHOT"
   :description "FIXME: write description"
   :dependencies [[org.clojure/clojure "1.8.0"]
                  [metosin/compojure-api "1.1.11"]
                  [org.slf4j/slf4j-log4j12 "1.7.1"]
                  [me.raynes/conch "0.8.0"]
                  [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                     javax.jms/jms
                                                     com.sun.jmdk/jmxtools
                                                     com.sun.jmx/jmxri]]
                  [cheshire "5.8.0"]
                  [io.forward/yaml "1.0.8"]
                  [com.cemerick/pomegranate "0.4.0"]
                  [clj-vxe "0.1.0-SNAPSHOT"]
                  [org.flatland/ordered "1.5.6"]]
   :plugins [[lein-ring "0.12.0"]]
   :ring {:handler le-guin.handler/app
          :nrepl {:start? true
                  :port 9003
                  }}
   :uberjar-name "server.jar"
   :aot :all
   :repl-options {:init-ns le-guin.core}
   :main le-guin.handler
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                    :plugins [[lein-ring "0.12.0"]]}})
