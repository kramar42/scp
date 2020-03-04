(ns scp.ui.dom)

(defn element [id]
  (js/document.getElementById id))

(def root-element (element "app"))
