(ns scp.ui.events
  (:require
    [taoensso.timbre :as log]
    [re-frame.core :as r]
    [scp.ui.display :as d]
    [scp.game.map :as map]
    [scp.game.level :as l]
    [scp.game.rules :as rules]))

;; effect handlers

(r/reg-fx
  :facts
  (fn [facts]
    (rules/insert-all! (log/spy facts))))

(r/reg-fx
  :draw
  (fn [actions]
    (doseq [[where what] actions]
      (d/draw where what))))

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
          who-symbol (get-in db [who :symbol])
          new-pos (move-fn old-pos)
          people (l/people-at-pos db new-pos)
          map-tile (map/get-char new-pos (:map db))]
      (cond
        (seq people)
        {:dispatch [:dialog/start people]}

        (map/can-stand new-pos (:map db))
        {:draw     [[old-pos map-tile]
                    [new-pos who-symbol]]
         :db       (update-in db [who :position] move-fn)
         :dispatch [:map/stand who new-pos]}))))

(r/reg-event-fx
  :map/stand
  (fn [{:keys [db]} [_ who pos]]
    (when-let [pickup-items (l/items-at-pos db pos)]
      {:dispatch [:item/pickup who pickup-items]})))

(r/reg-event-fx
  :item/pickup
  (fn [{:keys [db]} [_ who items]]
    (let [ids (into #{} (map :id items))]
      {:dispatch [:fact/insert-all
                  (mapv #(rules/->Owns who %) ids)]
       :db       (update db :items
                         (fn [items]
                           (remove #(ids (:id %)) items)))})))

;; rule events

(r/reg-event-fx
  :fact/insert-all
  (fn [_ [_ facts]]
    {:facts facts
     :dispatch-n (mapv #(vector :event/insert %) facts)}))

(r/reg-event-db
  :event/insert
  (fn [db [_ event]]
    (update-in db [:events]
               conj event)))

;; dialog events

(r/reg-event-db
  :dialog/start
  (fn [db [_ people]]
    (assoc db :dialog (-> people first :dialog))))

(r/reg-event-db
  :dialog/say
  (fn [db [_ what]]
    (if what
      (update db :history
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
