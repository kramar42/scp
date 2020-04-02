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
   :items  {:key {:symbol "%"
                  :color  "yellow"
                  :position (m/rand-pos map)}}
   :creatures {:soldier {:symbol "s"
                         :dialog (d/root d/dialog)
                         :position (m/rand-pos map)}}
   })

(defn items-at-pos [{:keys [items]} pos]
  (->> items
       (filter (fn [[_ {:keys [position]}]]
                 (= position pos)))
       (map first)
       (select-keys items)))

(defn creatures-at-pos [{:keys [creatures]} pos]
  (filter (fn [[_ {:keys [position]}]]
            (= position pos))
          creatures))
