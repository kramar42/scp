(ns scp.game.rules
  (:require-macros
    [clara.macros :refer [defrule defquery defsession]])
  (:require
    [taoensso.timbre :as log]
    [clara.rules :as clara]
    [mount.core :refer [defstate]]
    [re-frame.core :as rf]))

(defrecord Owns [who what])

(defrecord CanOpen [who what])

(defrecord Knows [who whom])

(defrule can-open
  "creature can open door if has key"
  [Owns (= who ?who) (= what :key)]
  =>
  (let [fact (->CanOpen ?who :door)]
    (clara/insert! fact)
    ;; todo think about better way to introspect session
    (rf/dispatch [:event/insert fact])))

(defquery get-who-can-open-doors
  []
  [?who <- CanOpen (= ?who who)])

(defquery can-open-door
  [:?creature]
  [?who <- CanOpen
   (= ?who who)
   (= ?who ?creature)])

(defquery knows
  [:?who :?whom]
  [?whom <- Knows
   (= ?who who)
   (= ?whom whom)])

(defsession session- 'scp.game.rules)

(defstate session :start (atom session-))

(defn insert-all! [facts]
  (swap! @session
         #(-> %
              (clara/insert-all facts)
              (clara/fire-rules))))

(defn query
  ([query-fn]
   (clara/query @@session query-fn)))

(defn retract-all [session fact-seq]
  (apply clara/retract session fact-seq))

(defn retract [& facts]
  (swap! @session
         #(-> %
              (retract-all facts)
              (clara/fire-rules))))

(defn player-has-key []
  (let [creature-set
        (->> (query get-who-can-open-doors)
             (map :?who)
             (into #{}))]
    (some? (creature-set :player))))

(comment

  (insert-all! [(->Owns :player :key)])
  (insert-all! [(->Owns :player2 :milk)])
  (retract [(->Owns :player :key)])

  (query get-who-can-open-doors)

  (clara/query @@session knows :?who :player :?whom :creature)
  (clara/query @@session knows :?who :player :?whom :himself)
  (insert-all! [(->Knows :player :himself)])

  (player-has-key)
  )
