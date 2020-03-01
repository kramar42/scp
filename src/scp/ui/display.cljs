(ns scp.ui.display
  (:require
    ["rot-js" :as rot]
    [mount.core :refer [defstate]]
    [scp.game.map :as m]))


(defn new-display [& params]
  (new rot/Display
       (clj->js {:width 50 :height 50
                 :forceSquareRatio false})))

(defstate display :start (new-display))

(defn render [element] (.appendChild element (.getContainer @display)))

(defn draw
  ([[x y] c] (draw x y c))
  ([x y c] (.draw @display x y c)))

(defn resize [w h]
  (.setOptions @display (clj->js {:width  w :height h})))

(defn draw-level [{:keys [map player items]}]
  (.clear @display)
  (let [[w h] (m/size map)]
    (resize w h))
  (doseq [[i r] (map-indexed vector map)]
    (doseq [[j c] (map-indexed vector r)]
      (draw j i c)))
  (draw (:position player) "@")
  (doseq [item items]
    (draw (:position item) (:symbol item))))

(comment
  (draw 1 1 "#")
  (get-map :2)
  (draw-level @scp.core/app)
  )

(defn display-option
  ([name]
   (-> @display
       (.getOptions)
       (js->clj :keywordize-keys true)
       name)))

(comment
  (def m (-> (new rot/Map.Digger 50 50)
             (.create (.-DEBUG (:display @db)))
             #_(js->clj :keywordize-keys true)))

  (def d (.create m))

  (-> d (.getCorridors))

  (rot/SHOW (.getContainer (:display @b)))

  (.create m (.-DEBUG (:display @db)))
  )