(ns scp.core
  (:require
    [taoensso.timbre :as log]
    [scp.keyboard :as k]
    [scp.map :as m]
    [scp.util :as u]
    [scp.level :as l]))

(defonce db (atom (merge {:display (m/new-display)}
                         (l/generate (m/get-map :3)))))

(defn refresh []
  (log/info "loaded")
  (.appendChild (u/root-element) (.getContainer (:display @db)))
  (m/draw-level @db)
  (k/shortcuts db))

(defn set-map [map-name]
  (swap! db assoc :map (m/get-map map-name))
  (m/draw-level @db))

(comment
  (refresh)
  (set-map :2)
  (m/draw-level @db)
  @db
  )
