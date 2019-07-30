(ns scp.keyboard
  (:require [scp.map :as map]))

(def keymap
  {38 :up
   37 :down
   40 :left
   39 :right})

(defn update-pos-fn [k]
  (case k
    :up    (fn [p] (update p 1 dec))
    :down  (fn [p] (update p 0 dec))
    :left  (fn [p] (update p 1 inc))
    :right (fn [p] (update p 0 inc))
    identity))

(defn shortcuts [db]
  (set! (.-onkeydown js/document)
        (fn [e]
          (let [db-val @db
                update-fn (update-pos-fn (keymap (.-keyCode e)))
                new-pos (update-fn (get-in db-val [:player :position]))]
            (when (map/can-stand new-pos (:room-map db-val))
              (swap! db update-in [:player :position] update-fn))))))

