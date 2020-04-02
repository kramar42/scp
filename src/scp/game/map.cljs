(ns scp.game.map
  (:require
   #_[taoensso.timbre :as log]
   [clojure.string :as str]))

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
        map-lines (str/split-lines map-str)]
    (map to-array (rest map-lines))))

(defn get-char [[px py] map]
  (aget (nth map py [])
        px))

(defn can-stand [pos map]
  (= "." (get-char pos map)))

(defn size [map]
  [(-> map first count) (-> map count)])

(defn rand-pos [map]
  (loop []
    (let [[w h] (size map)
          i     (rand-int w)
          j     (rand-int h)]
      (if (can-stand [i j] map)
        [i j]
        (recur)))))
