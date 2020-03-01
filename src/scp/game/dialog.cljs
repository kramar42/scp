(ns scp.game.dialog
  (:require
    [taoensso.timbre :as log]
    [cljs.spec.alpha :as s]
    [scp.game.rules :as r]))

(s/def ::phrase string?)

(s/def ::say string?)

(s/def ::response (s/keys :req [::say]))

(s/def ::choice
  (s/or :ref keyword?
        :node ::node))

(s/def ::choices (s/coll-of ::choice))

(s/def ::action fn?)

(s/def ::node
 (s/and
   (s/keys :req [::phrase]
           :opt [])
   (s/or
     :choices (s/keys :req [::choices])
     :action (s/keys :req [::action]))
   (s/or
     :response (s/keys :req [::response])
     :cond (s/keys :req [::cond]))))

(s/def ::tree
  (s/map-of keyword? ::node))

(defn resolve-ref
  "resolve kw or fn reference to a node in a tree"
  ([tree parent]
   (fn [ref] (resolve-ref tree parent ref)))
  ([tree parent ref]
   (let [node
         (cond
           (keyword? ref) (-> tree ref)
           (fn? ref) (ref parent)
           :else ref)]
     (assoc node :dialog/tree tree
                 :dialog/parent parent))))

(defn root [tree]
  ":dialog/root is special id for starting conversation"
  (resolve-ref tree tree :dialog/root))

(defn with-root
  "attach :dialog/root to dialog tree"
  [tree root]
  (assoc tree
    :dialog/root {:node/choices root}))

(defn choices
  "return resolved & filtered choices of a node"
  [node]
  (if-let [action (:node/action node)]
    (-> node action choices)
    (->> (:node/choices node)
         (map (resolve-ref (:dialog/tree node) node))
         (remove (fn [{:keys [node/visible-if?]}]
                   (and (some? visible-if?)
                        (not ((:cond/pred visible-if?)))))))))

;; :node/action functions

(defn back [node]
  (-> node :dialog/parent :dialog/parent))

(defn leave [node]
  (-> node :dialog/tree root))

(def ^:dynamic *collocutor*)

(def dialog-tree
  {:greeting  #:node{
                     ;; what player says on selecting this option
                     :phrase   "hello, friend"
                     ;; what happens in a world in response
                     :response #:response{
                                          ;; collocutor's phrase
                                          :say      "hello, do i know you?"
                                          ;; insert new fact in a system
                                          :new-fact {}
                                          ;; make him do something
                                          :action   [:boss :threaten :bdsm]
                                          }
                     ;; list of node's children
                     ;; has same shape as this node or is keyword reference
                     :choices  [:yes :no]}
   :open-door #:node{:phrase      "can you open that door for me?"
                     ;; declaring how this node (& if) should be shown
                     :visible-if? #:cond{:desc "you possess key"
                                         :pred #'r/player-has-key}}

   :weather   #:node{:phrase   "nice weather"
                     :response #:response{:say "indeed"}
                     :choices  [#:node{:phrase "back"
                                       :action back}]}
   :yes       #:node{:phrase   "yes"
                     :response #:response{:say "hm, you don't look familiar"}
                     :choices  [:leave]}
   :no        #:node{:phrase  "no"
                     :choices [:leave]}
   :leave
              #:node{:phrase "leave"
                     :action leave
                     }
   })

(def dialog
  (with-root dialog-tree
             [:greeting :open-door :weather :leave]))

(comment
(-> dialog
    root
    choices
    ;(nth 2)
    ;:node/phrase
    ;choices
    ;back
    )
)

;; action & trigger ideas
(comment
(def all-symbols
  [{:id         :akatosh
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
)
