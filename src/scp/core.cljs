(ns scp.core
  (:require
    [taoensso.timbre :as log]
    [reagent.core :as r]
    [re-frame.core :as re]
    [scp.ui.events]
    [scp.ui.keyboard :as k]
    [scp.ui.display :as d]
    [scp.game.dialog :as dl]
    [scp.game.map :as m]
    [scp.game.level :as l]
    [scp.ui.dom :as u]
    [scp.ui.views :as v]))

(enable-console-print!)

(defonce app
  (l/generate {:map (m/get-map :1)}))

(defn refresh []
  (re/dispatch-sync [:db/init app])
  (k/shortcuts)
  #_(v/run-history-chan)
  (log/info "render")
  (d/draw-level app)
  (r/render v/app u/root-element)
  (d/render (u/element "map")))

(defn ^:dev/after-load after-load []
  (re/clear-subscription-cache!)
  (refresh))

(defn set-map [map-name]
  (swap! @app assoc :map (m/get-map map-name))
  (re/dispatch [:init @@app])
  (d/draw-level @@app))

(comment
  (refresh)
  (set-map :1)
  (m/draw-level @app)
  @app
  @@r/session
  (re/subscribe [:events/log])
  (-> (re/subscribe [:dialog])
      deref
      dl/choices
      (nth 2)
      dl/choices
      first
      dl/choices)
  (re/subscribe [:history])
  )
