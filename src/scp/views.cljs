(ns scp.views
  (:require
    [mount.core :refer [defstate]]
    [reagent.core :as r]
    [scp.dialog :as d]
    [taoensso.timbre :as log]))

(defstate events :start (r/atom '()))

(defn add-event [event]
  (swap! @events conj event))

(comment
  (add-event "test")
  @@events
  )

(defn event-log []
  (fn []
    [:div.four.wide.column
     [:div#events.ui.feed "Events"
      (for [event @@events]
        ^{:key event} [:div.event
                       [:div.content event]])]]))

(defn dialog [root]
  (let [node (r/atom (:root root))]
    (fn []
      [:div.four.wide.column
       [:ul
        (for [ans (log/spy (:choices @node))]
          [:li
           [:a
            {:href "#"
             :on-click #(reset! node (log/spy (d/resolve-ref ans root)))
             }
            (-> ans
                (d/resolve-ref root)
                log/spy
                :phrase)]])]])))

(defn app []
  [:div.ui.grid
   [:div#map.sixteen.wide.column]
   [event-log]
   [dialog d/dialog]])
