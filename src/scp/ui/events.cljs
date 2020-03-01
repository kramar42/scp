(ns scp.ui.events
  (:require
    [taoensso.timbre :as log]
    [re-frame.core :as r]
    [scp.ui.display :as d]
    [scp.game.map :as map]
    [scp.game.rules :as rule]))

;; db events

(r/reg-event-db
  :db/init
  (fn [_ [_ data]]
    (assoc data
      :events []
      :history [])))

;; map events

(r/reg-event-fx
  :map/move
  (fn [{:keys [db]} [_ who move-fn]]
    (let [old-pos (get-in db [who :position])
          new-pos (move-fn old-pos)]
      (when (map/can-stand new-pos (:map db))
        ;; todo turn into coeffects
        (d/draw old-pos ".")
        (d/draw new-pos "@")
        {:db (update-in db [who :position] move-fn)
         :dispatch [:map/stand who new-pos]}))))

(r/reg-event-fx
  :map/stand
  (fn [{:keys [db]} [_ who pos]]
    (when-let [pickup-items (->> db
                                 :items
                                 (filter (fn [{:keys [position]}]
                                           (= position pos)))
                                 ;; todo why?
                                 seq)]
      {:dispatch [:item/pickup who pickup-items]})))

(r/reg-event-fx
  :item/pickup
  (fn [_ [_ who items]]
    ;; todo also remove item from the level
    {:dispatch-n
     (vec (for [{:keys [id]} items]
            [:rule/insert rule/->Owns [who id]]))}))

;; rule events

(r/reg-event-fx
  :rule/insert
  (fn [_ [_ rule args]]
    (rule/insert (apply rule args))
    nil))

(r/reg-event-db
  :event/insert
  (fn [db [_ events]]
    (update-in db [:events]
               concat events)))

;; dialog events

(r/reg-event-db
  :dialog/say
  (fn [db [_ what]]
    (if what
      (update-in db [:history]
                 conj what)
      db)))

(r/reg-event-fx
  :dialog/choose
  (fn [{:keys [db]} [_ {:keys [node/phrase node/response] :as choice}]]
    {:dispatch-n
         [[:dialog/say phrase]
          [:dialog/say (:response/say response)]]
     :db (assoc db :dialog choice)}))


;; was used for timeouts / animation
(comment
(defstate history-chan :start (a/chan))
(defn run-history-chan []
  (go-loop []
           (->> @history-chan
                a/<!
                (swap! @history conj))
           (recur)))
(defn say [& msgs]
  (doseq [msg msgs]
    (a/put! @history-chan msg)))
(comment
  (say "hey")
)
)
