(ns scp.core
  (:require
    [taoensso.timbre :as log]
    [devtools.core :as devtools]
    [mount.core :refer [defstate]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [scp.views :as v]
    [scp.keyboard :as k]
    [scp.map :as m]
    [scp.util :as u]
    [scp.level :as l]
    [scp.events :as e]))

(devtools/install!)
(enable-console-print!)

(defstate app :start (atom (l/generate (m/get-map :3))))

(defn render []
  (m/render (u/root-element))
  #_(r/render [v/app] (u/root-element)))

(defn refresh []
  (log/info "loaded")
  (rf/dispatch [:init @@app])
  #_(m/render (u/root-element))
  (m/draw-level @@app)
  (k/shortcuts)
  (render))

(defn ^:dev/after-load after-load []
  (rf/clear-subscription-cache!)
  (render))

(defn set-map [map-name]
  (swap! @app assoc :map (m/get-map map-name))
  (m/draw-level @@app))

(comment
  (refresh)
  (set-map :3)
  (m/draw-level @app)
  @app
  @@r/session
  )
