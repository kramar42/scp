(ns scp.ui.display
  (:require
    ["rot-js" :as rot]
    [mount.core :refer [defstate]]
    [scp.game.map :as m]))


(defn new-display []
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

(defn draw-level [{:keys [map player items people]}]
  (.clear @display)
  (let [[w h] (m/size map)]
    (resize w h))
  (doseq [[i r] (map-indexed vector map)]
    (doseq [[j c] (map-indexed vector r)]
      (draw j i c)))
  (draw (:position player) "@")
  (doseq [{:keys [position symbol]} items]
    (draw position symbol))
  (doseq [{:keys [position symbol]} people]
    (draw position symbol)))

(defn display-option
  ([name]
   (-> @display
       (.getOptions)
       (js->clj :keywordize-keys true)
       name)))

(comment
  (def m (-> (new rot/Map.Digger 50 50)
             (.create (.-DEBUG @display))
             #_(js->clj :keywordize-keys true)))

  (def d (.create m))

  (-> d (.getCorridors))

  (rot/SHOW (.getContainer @display))

  (.create m (.-DEBUG @display))
  )
