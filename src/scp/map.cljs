(ns scp.map
  (:require
    ["rot-js" :as rot]
    [taoensso.timbre :as log]))

(def levels
  {:1 "
######################
#....................#
#....................#
#....................#
#....................#
#....................#
#....................#
######################"

   :2 "
######################
#...#.......#.#......#
#...#######.#........#
#........#..#.#......#
#.###....#..#.#......#
#.######.#..#.#......#
#.............#......#
######################"

   :3 "
######################
#...#.......#.#......#
#...###.....#........#
#...#....#..#.#......#
#.#.+....#....#......#
#.######.#..#.#......#
#...#.........#......#
######################"})

(defn get-map [name]
  (let [map-str (levels name)
        map-lines (clojure.string/split-lines map-str)]
    (map to-array (rest map-lines))))

(defn can-stand [[px py] map]
  (= "."
     (aget
       (nth map py [])
       px)))

(defn new-display [& params]
  (new rot/Display
       (clj->js {:width 50 :height 50
                 :forceSquareRatio false})))

(defn draw
  ([display [x y] c] (draw display x y c))
  ([display x y c] (.draw display x y c)))

(defn resize [display w h]
  (.setOptions display (clj->js {:width  w :height h})))

(defn size [map]
  [(-> map first count) (-> map count)])

(defn draw-level [{:keys [map player items display]}]
  (.clear display)
  (let [[w h] (size map)]
    (resize display w h))
  (doseq [[i r] (map-indexed vector map)]
    (doseq [[j c] (map-indexed vector r)]
      (draw display j i c)))
  (draw display (:position player) "@")
  (doseq [item items]
    (draw display (:position item) (:symbol item))))

(comment
  (draw scp.core/db
        1 1 "#")
  (:display @scp.core/db)
  (get-map :2)
  (draw-level @scp.core/db)
  )

(defn display-option
  ([display name]
   (-> display
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
