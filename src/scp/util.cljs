(ns scp.util
  (:require [clojure.pprint :as ppp]))

(defn pp [v]
  (with-out-str (ppp/pprint v)))

