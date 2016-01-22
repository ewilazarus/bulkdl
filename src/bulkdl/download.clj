(ns bulkdl.download
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:import org.apache.commons.validator.UrlValidator))


(defn valid-url? [url]
  (.isValid (UrlValidator.) url))

(defn protocol-url [url]
  (let [trimmed-url (s/trim url)]
    (if (.startsWith trimmed-url "http")
      trimmed-url
      (str "http://" trimmed-url))))

(defn get-download-list [file]
  (->> (s/split (slurp file) #"\n")
       (map protocol-url)))

(defn create-output-dir [path]
  (try 
    (do (.mkdir (java.io.File. path)) true)
    (catch Exception e false)))

(defn output-file [dir url]
  (io/file dir (last (s/split url #"/"))))

(defn output-dir [dir]
  (io/file dir "bulkdl-output"))
      
(defn fetch-resource! [url file]
  (with-open [in (io/input-stream url)
              out (io/output-stream file)]
    (try
      (do (io/copy in out)
          :download-success)
      (catch Exception e :download-fail))))

(defn compose-msg [url file status]
  (cond (= status :download-success)
          (str "OK: " url)
        (= status :already-exists)
          (str "SKIPPED (File already exists locally): " url)
        (= status :download-fail)
          (str "FAIL (Can't download file from server): " url)
        (= status :invalid-url)
          (str "FAIL (Invalid URL): " url)
        (= status :cant-create-dir)
          (str "FAIL (Can't create output directory)")
        (= status :inexistent-list)
          (str "FAIL (Can't read download list)")
        :else
          (str "FAIL (Unexpected error)")))

(defn download! [input target-dir cb]
  (.start
    (Thread. 
      (fn [] (let [dir (.getAbsolutePath (output-dir target-dir))]
               (cond 
                 (not (create-output-dir dir)) (cb (compose-msg nil nil :cant-create-dir))
                 (not (.exists (io/as-file input))) (cb (compose-msg nil nil :inexistent-list))
                 :else (doseq [url (get-download-list input)]
                         (let [file (output-file dir url)
                               status (cond (not (valid-url? url)) :invalid-url
                                            (.exists (io/as-file file)) :already-exists
                                            :else (fetch-resource! url file))]
                           (cb (compose-msg url file status))))))))))
