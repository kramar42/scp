(ns scp.ui.subscriptions
  (:require
    (re-frame.core :as r)))

(r/reg-sub
  :events/log
  (fn [db _]
    (:events db)))

(r/reg-sub
  :history/log
  (fn [db _]
    (:history db)))

(r/reg-sub
  :dialog/node
  (fn [db _]
    (:dialog db)))

(r/reg-sub
 :data/path
 (fn [db _]
   (:data-path db)))