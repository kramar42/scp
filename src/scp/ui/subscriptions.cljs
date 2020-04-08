(ns scp.ui.subscriptions
  (:require
    (re-frame.core :as r)))

(r/reg-sub
  :ui/data
  (fn [db [_ path]]
    (get-in db [path :data])))

(r/reg-sub
  :ui/visible?
  (fn [db [_ path]]
    (not (get-in db [path :visible?]))))

(r/reg-sub
  :ui/dialog
  (fn [db [_ path]]
    (get-in db [path :dialog])))

(r/reg-sub
  :dialog/node
  (fn [db _]
    (:dialog/node db)))

(r/reg-sub
 :data/path
 (fn [db _]
   (:data/path db)))

(r/reg-sub
  :creature/inventory
  (fn [db [_ creature]]
    (get-in db [:creatures creature :items])))