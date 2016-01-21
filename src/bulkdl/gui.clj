(ns bulkdl.gui
  (:import [javax.swing JFileChooser])
  (:require [clojure.java.io :as io])
  (:use [clojure.string :as s] 
        [bulkdl.download :as download]
        [seesaw.core]))


;; UI

(def title "bulkdl")
(def window-size {:width 400
                  :height 500})


(defn url-selector-popup [parent]
  (let [chooser (JFileChooser.)]
    (do (.setFileSelectionMode chooser JFileChooser/FILES_ONLY) 
        (.setDialogType chooser JFileChooser/OPEN_DIALOG)
        (.showOpenDialog chooser parent)
        (.getSelectedFile chooser))))
(def file-selector-url (text))
(def file-selector-url-button (button :text "..."
                                      :size [20 :by 18]))
(def url-panel
  (border-panel
    :vgap 2
    :north (label :text "Path to URL list (*):")
    :south (horizontal-panel :items [file-selector-url
                                     file-selector-url-button])))


(defn dir-selector-popup [parent]
  (let [chooser (JFileChooser.)]
    (do (.setFileSelectionMode chooser JFileChooser/DIRECTORIES_ONLY) 
        (.setDialogType chooser JFileChooser/OPEN_DIALOG)
        (.showOpenDialog chooser parent)
        (.getSelectedFile chooser))))
(def file-selector-dir (text :text "."))
(def file-selector-dir-button (button :text "..."
                                      :size [20 :by 18]))
(def dir-panel
  (border-panel
    :vgap 2
    :north (label :text "Path to output directory (*):")
    :south (horizontal-panel :items [file-selector-dir
                                     file-selector-dir-button])))


(def top-panel
  (border-panel
    :vgap 5
    :north url-panel
    :south dir-panel))

(def download-button (button :text "DOWNLOAD"
                             :size [379 :by 30]))

(def log
  (text :multi-line? true
        :editable? false))
(def log-panel
  (scrollable log))
(def details-panel
  (border-panel
    :vgap 2
    :north (label :text "Details:")
    :center log-panel))

(def bottom-panel
  (border-panel
    :vgap 20
    :north download-button
    :center details-panel))

(def window-content
  (border-panel
    :north (flow-panel)
    :south (flow-panel)
    :east (flow-panel)
    :west (flow-panel)
    :center (border-panel
              :hgap 5
              :vgap 20
              :north top-panel
              :center bottom-panel)))

(def main-window
  (frame :title title
         :width (:width window-size)
         :height (:height window-size)
         :content window-content
         :on-close :exit))

(defn make-frame []
  (invoke-later
    (-> main-window
        show!)))


;; Main

(defn display-gui []
  (make-frame))


;; Helper functions

(defn print-to-details [message]
  (text! log (str (text log) message "\n")))

(defn clean-details []
  (text! log ""))



;; Callback

(defn gui-callback [msg]
  (print-to-details msg))


;; Listeners

(listen file-selector-url-button 
        :action (fn [e] (let [file (url-selector-popup main-window)]
                          (if-not (nil? file)
                            (text! file-selector-url 
                                   (.getAbsolutePath file))))))

(listen file-selector-dir-button
        :action (fn [e] (let [dir (dir-selector-popup main-window)]
                          (if-not (nil? dir)
                            (text! file-selector-dir
                                   (.getAbsolutePath dir))))))
                              
(listen download-button
        :action (fn [e] (let [filename (text file-selector-url)
                              target-dir (text file-selector-dir)]
                          (if-not (and (s/blank? filename) 
                                       (s/blank? target-dir))
                            (do (clean-details)
                                (download/download! filename
                                                    target-dir
                                                    gui-callback))))))
