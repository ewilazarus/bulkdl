(ns bulkdl.core
  (:use [bulkdl.cli :as cli]
        [bulkdl.gui :as gui])
  (:gen-class))

(defn -main [& args]
  (if (zero? (count args))
    (gui/display-gui)
    (cli/execute args)))
