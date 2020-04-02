(ns scp.game.data
  (:require
    [datascript.core :as d]
    [mount.core :refer [defstate]]))

(def schema
  {:entity/name  {:db/unique :db.unique/identity}
   
   :attr/name    {}
   :attr/value   {}
   :attr/entity  {:db/type :db.type/ref}

   :rel/name     {}
   :rel/source   {:db/type :db.type/ref}
   :rel/target   {:db/type :db.type/ref}
   :rel/entities {:db/cardinality :db.cardinality/many}})

(defstate conn :start (d/create-conn schema))

;; todo turn this into reframe subscription
;; use it as a base for a subs that's showing
;; all entities & relations known to a player
;; will be used in make-assumption ui
(defn entities []
  (d/q '[:find [?name ...]
         :where
         [_ :entity/name ?name]]
       @@conn))

(defn add-attr [name attr value]
  (d/transact! @conn
               [{:entity/name name}
                {:attr/name attr
                 :attr/value value
                 :attr/entity [:entity/name name]}]))

(add-attr :you :age 26)
(add-attr :you :height 171)
(add-attr :he :eye-color :green)
(add-attr :she :likes :books)

(defn attrs [entity-name]
  (d/q '[:find ?name ?value
         :in $ ?entity-name
         :where
         [?e :entity/name ?entity-name]
         [?a :attr/entity ?e]
         [?a :attr/name ?name]
         [?a :attr/value ?value]]
       @@conn
       entity-name))

(defn add-rel
  ([name source target]
   (d/transact! @conn
                [{:entity/name source}
                 {:entity/name target}
                 {:rel/name name
                  :rel/source [:entity/name source]
                  :rel/target [:entity/name target]}])))

(add-rel :in-love :you :pizza)
(add-attr :pizza :size :large)
(add-rel :hates :he :you)
(add-rel :in-love :she :you)
(add-rel :has-brother :she :he)

(defn rels [entity-name]
  (d/q '[:find ?name ?target
         :in $ ?entity-name
         :where
         [?e :entity/name ?entity-name]
         [?r :rel/source ?e]
         [?r :rel/name ?name]
         [?r :rel/target ?t]
         [?t :entity/name ?target]]
       @@conn
       entity-name))

"""

initially write condition as a predicate
later introduce data description: [:has-item :create :key], [:knows :who :whom]

for now can only be triggered in dialog directly from response node,
or when new fact is inserted, or when some item is transfered

how rules are connected to data?
and how both are connected to conditions & actions?

"""

(comment
  (attrs :you)
  (rels :you)
  )