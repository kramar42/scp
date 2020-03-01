(ns scp.ui.util)

(defn element [id]
  (js/document.getElementById id))

(def root-element (element "app"))
