(ns scp.ui.views
  (:require
    ;  [taoensso.timbre :as log]
    [re-frame.core :as r]
    [scp.ui.subscriptions]
    [scp.game.dialog :as d]
    [scp.game.data :as data]
    [garden.core :as garden]
    [scp.ui.css :as css]))

(defn mapi [coll]
  (map vector (range) coll))

(defn eventlog []
  (let [events (r/subscribe [:events/log])]
    (fn []
      [:div.eight.wide.column "Events"
       [:div#events.ui.feed
        (for [[id event] (mapi @events)]
          ^{:key id}
          [:div.event
           [:div.content {} event]])]])))

(defn history-log []
  (let [history (r/subscribe [:history/log])]
    (fn []
      [:div.history
       (for [[id entry] (mapi @history)]
         ^{:key id}
         [:div.entry
          [:div.content entry]])])))

(defn dialog []
  (let [node (r/subscribe [:dialog/node])]
    (fn []
      (when (some? @node)
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
              phrase]])]]))))

(defn datalog []
  (let [path (r/subscribe [:data/path])]
      [:div.sixteen.wide.column "Datalog"
       (if (empty? @path)
         [:ul
          (for [[id entity] (mapi (data/entities))]
            ^{:key id}
            [:li
             [:a
              {:href "#"
               :on-click #(r/dispatch [:data/path entity])}
              entity]])]
         [:div.dossier
          [:table.ui.celled.table.unstackable.entities
           [:thead>tr
            [:th "Attr"] [:th "Value"]]
           [:tbody
            (for [[id [attr value]] (mapi (data/attrs (last @path)))]
              ^{:key id}
              [:tr
               [:td attr] [:td value]])]]
          [:table.ui.celled.table.unstackable.relations
           [:thead>tr
            [:th "Relation"] [:th "Target"]]
           [:tbody
            (for [[id [rel target]] (mapi (data/rels (last @path)))]
              ^{:key id}
              [:tr
               [:td rel]
               [:td>a
                {:href "#"
                 :on-click #(r/dispatch [:data/path target])}
                target]])]]
          [:table.ui.celled.table.unstackable.motivs
           [:thead>tr
            [:th] [:th "Verb"] [:th "Target"]]
           [:tbody
            (for [[id [verb target]] (mapi (data/motivs (last @path)))]
              ^{:key id}
              [:tr
               [:td "Wants to"]
               [:td verb]
               [:td>a
                {:href     "#"
                 :on-click #(r/dispatch [:data/path target])}
                target]])]]])
        [:a {:href "#"
             :on-click #(r/dispatch [:data/path :data.path/back])}
         "back"]]))

(defn app []
  [:div.ui.grid {:style (garden/css css/root)}
   [:div#map.sixteen.wide.column]
   [eventlog]
   [dialog]
   [datalog]])
