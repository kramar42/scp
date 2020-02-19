(ns scp.core
  (:require
    [taoensso.timbre :as log]
    [mount.core :refer [defstate]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [scp.keyboard :as k]
    [scp.map :as m]
    [scp.util :as u]
    [scp.level :as l]
    [scp.events :as e]
    [scp.views :as v]))

(enable-console-print!)

(defstate ^{:on-reload :noop} app
  :start (atom (l/generate (m/get-map :1))))

(defn refresh []
  (rf/dispatch [:init @@app])
  (k/shortcuts)
  (log/info "render")
  (m/draw-level @@app)
  (r/render v/app u/root-element)
  (m/render (u/element "map")))

(defn ^:dev/after-load after-load []
  (rf/clear-subscription-cache!)
  (refresh))

(defn set-map [map-name]
  (swap! @app assoc :map (m/get-map map-name))
  (rf/dispatch [:init @@app])
  (m/draw-level @@app))

(comment
  (refresh)
  (set-map :1)
  (m/draw-level @app)
  @app
  @@r/session
  )
