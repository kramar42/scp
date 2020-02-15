(ns scp.events
  (:require
    [re-frame.core :as rf]
    [scp.map :as map]
    [scp.rules :as r]
    [taoensso.timbre :as log]))

(rf/reg-event-db
  :init
  (fn [_ [_ data]]
    data))

(rf/reg-event-fx
  :move
  (fn [{:keys [db]} [_ who move-fn]]
    (let [new-pos (log/spy (-> db (get-in [who :position]) move-fn))]
      (when (log/spy (map/can-stand new-pos (:map db)))
        (map/draw (get-in db [who :position]) ".")
        (map/draw new-pos "@")
        {:db (update-in db [who :position] move-fn)
         :dispatch [:stand who new-pos]}))))

(rf/reg-event-fx
  :stand
  (fn [{:keys [db]} [_ who pos]]
    (when-let [pickup-items (->> db
                                 :items
                                 (filter (fn [{:keys [position]}]
                                           (= position pos)))
                                 seq)]
      {:dispatch [:pickup who pickup-items]})))

(rf/reg-event-fx
  :pickup
  (fn [_ [_ who items]]
    ;; also remove item from the level
    {:dispatch-n
      (vec (for [{:keys [id]} items]
             (log/spy [:rule r/->Owns [who id]])))}))

(rf/reg-event-fx
  :rule
  (fn [_ [_ rule args]]
    (r/insert (apply rule args))
    nil))
