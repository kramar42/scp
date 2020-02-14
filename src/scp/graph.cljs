(ns scp.graph
  (:require [loom.graph :as graph]
            [loom.attr :as attr]
            [loom.io :as io]))

(comment
  # grammar

  dangeon -> entrance - exit
  room -> key -(door)- room


  # render
  room
  key (room + key)
  entrance (room + <)
  exit (room + >)
  corridor
  corridor w/ door
  )

(defn init-map
  (-> (graph/graph [:dungeon])
      (attr/add-attr :dungeon :type :dungeon)))

(defn edge []
  ())

(def rules
  {{:1 :dungeon} {:1 [:chain [:2 :3]]
                  :2 [:chain [:4]]
                  :3 [:chain [:4]]}
   {:1 [:chain [:2]]
    :2 :chain}
                 {:1 [:key [:2]]
                  :2 :lock}})

(def g
  (-> (graph/graph [:entrance :door] [:door :goal])
      (attr/add-attr :entrance :label "this is entrance")
      (graph/add-nodes "foobar" {:name "baz"} [1 2 3])))

(def g (graph/graph [1 2] [2 3] {3 [4] 5 [6 7]} 7 8 9))

;; not as simple. we need to find sub-graph not just node
(defn rule-applies? [g node rule]
  true)

(defn find-rule-path [g rule &opts]
  (->> (graph/nodes g)
       (filter #(rule-applies? g % rule))
       first))

(defn map-nodes [g node rules])

(defn remove-edges [g node rule])

(defn apply-rule [g node rule]
  (let [node-mapping (map-nodes g node rule)]
    (-> g
        (remove-edges node rule)
        (replace-nodes node rule)
        (add-edges node rule))))

(comment
  (io/view g))