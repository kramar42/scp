(ns scp.util
  (:require [clojure.pprint :as ppp]))

(defn pp [v]
  (with-out-str (ppp/pprint v)))

(defn element [id]
  (js/document.getElementById id))

(defn root-element []
  (element "app"))

(defmacro runonce [& body]
  `(defonce _
            (do
              ~@body)))
