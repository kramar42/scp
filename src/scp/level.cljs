(ns scp.level
  (:require
    [scp.map :as m]
    [taoensso.timbre :as log]))

(defn rand-pos [map]
  (loop []
    (let [[w h] (m/size map)
          i     (rand-int w)
          j     (rand-int h)]
      (if (m/can-stand [i j] map)
        [i j]
        (recur)))))

(defn generate [map]
  (log/info "generating map")
  {:map    map
   :player {:position (rand-pos map)}
   :items  [{:symbol "%"
             :color  "yellow"
             :id :key
             :position (rand-pos map)}]
   })
