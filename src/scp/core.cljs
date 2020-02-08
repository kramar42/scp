(ns scp.core
  (:require
    [taoensso.timbre :as log]
    [mount.core :refer [defstate]]
    [scp.keyboard :as k]
    [scp.map :as m]
    [scp.util :as u]
    [scp.level :as l]))

(defstate app :start (atom (l/generate (m/get-map :3))))

(defn refresh []
  (log/info "loaded")
  (m/render (u/root-element))
  (m/draw-level @@app)
  (k/shortcuts))

(defn set-map [map-name]
  (swap! @app assoc :map (m/get-map map-name))
  (m/draw-level @@app))

(comment
  (refresh)
  (set-map :3)
  (m/draw-level @app)
  @app
  )
