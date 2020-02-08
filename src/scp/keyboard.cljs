(ns scp.keyboard
  (:require
    [scp.map :as map]))

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

(defn shortcuts []
  (set! (.-onkeydown js/document)
        (fn [e]
          (let [{:keys [map player]} @@scp.core/app
                update-fn (-> e (.-keyCode) keymap update-pos-fn)
                new-pos   (-> player :position update-fn)]
            (when (map/can-stand new-pos map)
              (map/draw (:position player) ".")
              (map/draw new-pos "@")
              (swap! @scp.core/app update-in [:player :position] update-fn))))))
