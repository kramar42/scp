(ns scp.core
  (:require [reagent.core :as r]
            [taoensso.timbre :as log]
            [scp.keyboard :as key]
            [scp.map :as map]
            [scp.util :as util]
            ["rot-js" :as rot]))

(defonce db (r/atom {:player {:position [1 1]}
                     :room-map (map/get-map :2)}))

(def GRID_SIZE 17)

(defn css-repeat [n & what]
  (str "repeat(" n "," (or what "1fr") ")"))

(defn render-room-map
  [m {[px py] :position}]
  "Should map know about creatures & items on it?"
  (let [height (count m)
        width  (count (first m))]
    [:div.map {:style {:grid-template-columns (css-repeat width)
                       :width  (* width  GRID_SIZE)
                       :height (* height GRID_SIZE)}}
     ;row
     (for [[i r] (map-indexed vector m)]
        ;cell
        (for [[j v] (map-indexed vector r)]
          ^{:key (str i "x" j)}
          [:div.map-cell {:style {:height GRID_SIZE}}
           (if (and (= px j)
                    (= py i))
             ""
             v)]))]))

(defn render-player [{:keys [position]}]
  (let [[px py] position]
    [:div.player {:style {:width GRID_SIZE
                          :height GRID_SIZE
                          :left (* GRID_SIZE px)
                          :top (* GRID_SIZE py)}}
     "@"]))

(defn room [{:keys [room-map player]}]
  [:div.room
   [render-room-map room-map player]
   [render-player player]])

(defn app []
  [:div.room-wrapper
   [room @db]])

(defn refresh []
  (log/info "loaded")
  (r/render [app] (util/root-element))
  (key/shortcuts db))

(comment
  (in-ns 'scp.core)

  (js/alert "hi")

  (+ 1 1)

  (refresh)

  (swap! db assoc :player {:position [4 3]})
  (swap! db assoc :room-map (map/get-map :3))
  (map/get-map :3)

  (map/can-stand [1 0] (:room-map @db)))
