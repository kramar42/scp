(ns scp.rules
  (:require-macros
    [clara.macros :refer [defrule defquery defsession]])
  (:require
    [clara.rules :as clara]))

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

(comment

  (defsession session 'scp.rules)

  (-> session
      (clara/insert (->Owns :player :milk)
                    (->Owns :orc :key))
      (clara/fire-rules)
      (clara/query get-who-can-open-doors)
      )
  )
