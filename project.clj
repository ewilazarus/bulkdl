(defproject bulkdl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [commons-validator "1.4.1"]
                 [org.clojure/tools.cli "0.3.3"]
                 [seesaw "1.4.5"]]
  :main ^:skip-aot bulkdl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
