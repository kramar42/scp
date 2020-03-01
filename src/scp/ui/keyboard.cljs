(ns scp.ui.keyboard
  (:require
    [re-frame.core :as re]))

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
            (re/dispatch [:map/move :player update-fn])))))
