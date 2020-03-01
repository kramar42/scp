(ns scp.game.level
  (:require
    [taoensso.timbre :as log]
    [scp.game.map :as m]
    [scp.game.dialog :as d]))


(defn generate [{:keys [map]}]
  (log/info "generating level")
  {:map    map
   :player {:position (m/rand-pos map)}
   :items  [{:symbol "%"
             :color  "yellow"
             :id :key
             :position (m/rand-pos map)}]
   :dialog (d/root d/dialog)
   })
