(ns scp.map
  (:require
    [taoensso.timbre :as log]
    ["rot-js" :as rot]
    [mount.core :refer [defstate]]
    [scp.rules :as r]))

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

[:greeting :weather :boss :leave]
(some-fn)
[
 {:id          :greeting
  :phrase      "hello, friend"
  :visible-if? {}
  ;; =>
  :response    {:answer   "hello"
                :new-fact {}}
  :choices     [{:ref                :yes
                 :additional-choices []}
                {:choices [{:choices [{:choices []}]}]}
                {:id          :success
                 :phrase      "you win"
                 :visible-if? {}}]
  }
 {:phrase      "intimidate by boss friendship"
  :visible-if? {"you know about boss existence"}
  :condition   {'(r/fears :*collocutor* :boss) {:action [:boss :threaten :bdsm]}
                cond2 #_-> {:response {:answer "no"}
                            :choices  []}
                default    {}}
  }
 {:id :yes
  :phrase "yes"
  :answer ""
  :choices [{} {}]}
 {:phrase "leave"
  :answer "goodbye"
  :terminal? true
  }
 {:answer "yes"
  :new-fact {}
  :choices []}
 ]

(def all-symbols
  [{:id :akatosh
    :is-threat? true}])

(defn trigger-fn
  [target action symbol]
  #_=> #_response)

[:boss :threaten :bdsm]

{:threaten (->> all-symbols
                (filter can-be-threat?)
                )
 :ask-about _
 :give      _}


;; static tree

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

(defstate display :start (new-display))

(defn render [element] (.appendChild element (.getContainer @display)))

(defn draw
  ([[x y] c] (draw x y c))
  ([x y c] (.draw @display x y c)))

(defn resize [w h]
  (.setOptions @display (clj->js {:width  w :height h})))

(defn size [map]
  [(-> map first count) (-> map count)])

(defn draw-level [{:keys [map player items]}]
  (.clear @display)
  (let [[w h] (size map)]
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
