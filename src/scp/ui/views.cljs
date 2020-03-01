(ns scp.ui.views
  (:require
    [taoensso.timbre :as log]
    [re-frame.core :as r]
    [scp.ui.subscriptions]
    [scp.game.dialog :as d]))

(defn event-log []
  (let [events (r/subscribe [:events/log])]
    (fn []
      [:div.eight.wide.column "Events"
       [:div#events.ui.feed
        (for [[id event] (map vector (range) @events)]
          ^{:key id}
          [:div.event
           [:div.content {} event]])]])))

(defn history-log []
  (let [history (r/subscribe [:history/log])]
    (fn []
      [:div.history
       (for [[id entry] (map vector (range) @history)]
         ^{:key id}
         [:div.entry
          [:div.content entry]])])))

(defn dialog []
  (let [node (r/subscribe [:dialog/node])]
    (fn []
      [:div.eight.wide.column "Dialog"
       [history-log]
       [:ul
        (for [{:keys [node/phrase] :as choice}
              (d/choices @node)]
          ^{:key choice}
          [:li
           [:a
            {:href     "#"
             :on-click #(r/dispatch [:dialog/choose choice])}
            phrase]])]])))

(defn app []
  [:div.ui.grid
   [:div#map.sixteen.wide.column]
   [event-log]
   [dialog]])
