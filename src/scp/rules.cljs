(ns scp.rules
  (:require-macros
    [clara.macros :refer [defrule defquery defsession]])
  (:require
    [clara.rules :as clara]
    [clara.tools.inspect :as inspect]
    [mount.core :refer [defstate]]))

(defrecord Owns [who what])

(defrecord CanOpen [who what])

(defrule can-open
  "creature can open door if has key"
  [Owns (= who ?who) (= what :key)]
  =>
  (clara/insert! (->CanOpen ?who :door)))

(defquery get-who-can-open-doors
  []
  [?who <- CanOpen (= ?who who)])

(defquery can-open-door
  [:?creature]
  [?who <- CanOpen
   (= ?who who)
   (= ?who ?creature)])

(defsession session- 'scp.rules)

(defstate session :start (atom session-))

(defn insert [& facts]
  (swap! @session
         #(-> %
              (clara/insert-all facts)
              (clara/fire-rules))))

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
