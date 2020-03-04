(ns scp.game.level
  (:require
   [taoensso.timbre :as log]
   [scp.game.map :as m]
   [scp.game.dialog :as d]))


(defn generate [{:keys [map]}]
  (log/info "generating level")
  {:map    map
   :player {:symbol "@"
            :position (m/rand-pos map)}
   :items  [{:symbol "%"
             :color  "yellow"
             :id :key
             :position (m/rand-pos map)}]
   :people [{:name :soldier
             :symbol "s"
             :dialog (d/root d/dialog)
             :position (m/rand-pos map)}]
   })

(defn items-at-pos [{:keys [items]} pos]
  (->> items
       (filter (fn [{:keys [position]}]
                 (= position pos)))
       seq))

(defn people-at-pos [{:keys [people]} pos]
  (filter (fn [{:keys [position]}]
            (= position pos))
          people))
