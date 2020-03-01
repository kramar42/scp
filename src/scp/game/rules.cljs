(ns scp.game.rules
  (:require-macros
    [clara.macros :refer [defrule defquery defsession]])
  (:require
    [taoensso.timbre :as log]
    [clara.rules :as clara]
    [clara.tools.inspect :as inspect]
    [mount.core :refer [defstate]]
    [re-frame.core :as rf]))

(defrecord Owns [who what])

(defrecord CanOpen [who what])

(defrecord Knows [who whom])

(defrule can-open
  "creature can open door if has key"
  [Owns (= who ?who) (= what :key)]
  =>
  (let [inf (->CanOpen ?who :door)]
    (clara/insert! inf)
    (rf/dispatch [:event/insert [inf]])))

(defquery get-who-can-open-doors
  []
  [?who <- CanOpen (= ?who who)])

(defquery can-open-door
  [:?creature]
  [?who <- CanOpen
   (= ?who who)
   (= ?who ?creature)])

(defsession session- 'scp.game.rules)

(defstate session :start (atom session-))

(defn insert [& facts]
  (swap! @session
         #(-> %
              (clara/insert-all facts)
              (clara/fire-rules)))
  (rf/dispatch [:event/insert facts]))

(defn query [query-fn]
  (clara/query @@session query-fn))

(defn retract-all [session fact-seq]
  (apply clara/retract session fact-seq))

(defn retract [& facts]
  (swap! @session
         #(-> %
              (retract-all facts)
              (clara/fire-rules))))

(comment

  (insert (->Owns :player :key))
  (insert (->Owns :player2 :milk))
  (retract (->Owns :player :key))

  (query get-who-can-open-doors)

  @session
  (-> @@session
      inspect/inspect
      ;:insertions
      )
  )
