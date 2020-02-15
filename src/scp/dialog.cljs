(ns scp.dialog)

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

   :yes  {:phrase "yes" :choices [{} {}]}
   :no   {:phrase "no"}
   :leave
         {:phrase    "leave"
          :answer    "goodbye"
          :terminal? true}
   })

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

