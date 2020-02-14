(ns scp.keyboard
  (:require
    [re-frame.core :as rf]
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
    nil))

(defn shortcuts []
  (set! (.-onkeydown js/document)
        (fn [e]
          (when-let [update-fn (-> e (.-keyCode) keymap update-pos-fn)]
            (rf/dispatch [:move :player update-fn])))))
