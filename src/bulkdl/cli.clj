(ns bulkdl.cli
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:use [bulkdl.download :as download]))

(def cli-options
  [["-i" "--input-file FILE" "Download list"]
   ["-o" "--output-dir DIRECTORY" "Output directory"]])

(defn cli-callback [msg]
  (println msg))

(defn execute [args]
  (let [parsed-args (parse-opts args cli-options)
        input-file (get-in parsed-args [:options :input-file])
        output-dir (get-in parsed-args [:options :output-dir])]
    (if-not (and (nil? input-file) (nil? output-dir))
      (download/download! input-file output-dir cli-callback)
      (cli-callback "FAIL (Wrong number of CLI args)"))))
