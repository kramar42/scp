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
    (log/info who pos (:items db))
    (let [pickup-items (->> db
                            :items
                            (filter (fn [{:keys [position]}]
                                      (= position pos))))]
      (log/info pickup-items)
      {:dispatch [:pickup who pickup-items]})))

(rf/reg-event-fx
  :pickup
  (fn [_ [_ who items]]
    {:dispatch-n
     (for [item items]
       (log/spy [:rule r/->Owns [who item]]))}))

(rf/reg-event-fx
  :rule
  (fn [_ [_ rule args]]
    (r/insert (apply rule args))))
