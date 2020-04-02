(ns scp.ui.events
  (:require
    ; [taoensso.timbre :as log]
    [re-frame.core :as r]
    [scp.ui.display :as d]
    [scp.game.map :as map]
    [scp.game.level :as l]
    [scp.game.rules :as rules]))

;; effect handlers

(r/reg-fx
  :facts
  (fn [facts]
    (rules/insert-all! facts)))

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
           :history []
           :data-path [])))

;; map events

(r/reg-event-fx
  :map/move
  (fn [{:keys [db]} [_ who move-fn]]
    (let [old-pos (get-in db [who :position])
          who-symbol (get-in db [who :symbol])
          new-pos (move-fn old-pos)
          creatures (l/creatures-at-pos db new-pos)
          map-tile (map/get-char new-pos (:map db))]
      (cond
        (seq creatures)
        {:dispatch [:dialog/start creatures]}

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

(defn rm-items-fn [ids]
  (fn [m] (apply dissoc m ids)))

(defn add-items-fn [items]
  (fn [m] (merge m items)))

(r/reg-event-fx
  :item/pickup
  (fn [{:keys [db]} [_ who items]]
    (let [ids (keys items)]
      {:dispatch [:fact/insert-all
                  (mapv #(rules/->Owns who %) ids)]
       :db       (-> db
                     (update :items (rm-items-fn ids))
                     (update-in [:player :items] (add-items-fn items)))})))

(r/reg-event-fx
 :item/transfer
 (fn [{:keys [db]} [_ from to items]]
   (let [ids (keys items)]
     {:db (-> db
              (update-in [from :items] (rm-items-fn ids))
              (update-in [to :items] (add-items-fn items)))})))

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
  (fn [db [_ creatures]]
    (assoc db :dialog (-> creatures first :dialog))))

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

;; datalog events

(r/reg-event-db
 :data/path
 (fn [db [_ path]]
   (update db :data-path
           (if (= path :data.path/back)
             #(if (empty? %) [] (pop %))
             #(conj % path)))))
