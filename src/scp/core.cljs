(ns ^:figwheel-hooks scp.core
  (:require [reagent.core :as r]
            [scp.keyboard :as key]
            [scp.map :as map]))

(defonce db (r/atom {:player {:position [1 1]}
                     :room-map (map/get-map :2)}))

(def GRID_SIZE 20)

(defn render-room-map [m]
  (let [width (count (first m))]
    [:div.map (for [[i r] (map-indexed vector m)]
                ;row
                [:div {:style {:width (* width GRID_SIZE)
                               :height GRID_SIZE}
                       :key i}
                 ;cell
                 (for [[j v] (map-indexed vector r)]
                   [:div {:style {:width GRID_SIZE
                                  :height GRID_SIZE
                                  :float "left"}
                          :key (str i "x" j)}
                    v])])]))

(defn render-player [{:keys [position]}]
  (let [[px py] position]
    [:pre {:style {:position "absolute"
                   :width GRID_SIZE
                   :left (* GRID_SIZE (inc px))
                   :top (- (* GRID_SIZE py) 6)}}
     "@"]))

(defn room [{:keys [room-map player]}]
  [:div.room {:style {:position "relative"}}
   (render-room-map room-map)
   (render-player player)])

(defn app []
  (room @db))

(defonce _
  (do
    (r/render [app] (js/document.getElementById "app"))
    (key/shortcuts db)))

(comment
  (in-ns 'scp.core)

  (swap! db assoc :player {:position [0 0]})
  (swap! db assoc :room-map (map/get-map :1))

  (map/can-stand [1 0] (:room-map @db)))
