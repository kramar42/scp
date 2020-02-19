(ns scp.dialog
  (:require
    [clojure.zip :refer [zipper up down left right root children] :as z]
    [taoensso.timbre :as log]))

(def dialog-tree
  ;; every top-level node should have id
  ;; :root is special id for starting conversation
  {:root {:choices [:greeting :boss :weather :leave]}
   :greeting
   ;; declaring how this node (& if) should be shown
         {:phrase      "hello, friend"
          :visible-if? {}
          ;; declaring what will happen on selecting it
          :response    {
                        ;; add dialog text
                        :answer   "hello, i know you?"
                        ;; insert new fact
                        :new-fact {}}
          ;; list of node's children
          ;; has same shape as this node or is keyword reference
          :choices     [:yes :no]}
   :intimidate
         {:phrase      "intimidate by boss friendship"
          :visible-if? {:cond "you know about boss existence"}
          ;; :condition means there is more then one outcome to choosing this node
          ;; it's a map of predicates (possible rule engine queries) to responses
          :condition   {'(r/fears :*collocutor* :boss)
                        {:answer   "you failed"
                         :new-fact {}
                         :action   [:boss :threaten :bdsm]}
                        :default
                        {:answer "no"}}}
   :boss {:phrase "no boss here"
          :choices [:leave]}
   :weather {:phrase "nice weather"
             :response {:answer "indeed"}
             :choices [:leave]}
   :yes  {:phrase "yes" :choices [{} {}]}
   :no   {:phrase "no"}
   :leave
         {:phrase    "leave"
          :answer    "goodbye"
          :terminal? true}
   })

(defn resolve-ref [node root]
  (if (keyword? node)
    (node root)
    node))

(defn prepare-tree [tree]
  (assoc (:root tree)
    :root tree))

(def dialog
  (zipper #_(fn [node]
            (map #(assoc % :root (:root node))
                 (:choices node)))
          :choices
          (fn [node]
            (let [r (log/spy node)]
              (:choices node)
              #_(map #(resolve-ref % (:root node)) (:choices node))))
          (fn [n c] (assoc n :choices c))
          (prepare-tree dialog-tree)))

#_(-> dialog
    down
    right
    right
    down
    ;rights
    ;down
    ;right
    ;right
    ;down
    ;right
    ;:root
    ;:choices
    )

(def all-symbols
  [{:id :akatosh
    :is-threat? true}])

(def can-be-threat? (constantly true))

(defn trigger-fn
  [target action symbol]
  #_=> #_response)

(def actions
  {:threaten  (->> all-symbols
                   (filter can-be-threat?)
                   (map trigger-fn))
   :ask-about {"by" "analogy"}
   :give      'something})

