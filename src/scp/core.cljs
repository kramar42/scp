(ns scp.core
  (:require-macros [clara.macros :refer [defrule defquery defsession]])
  (:require [reagent.core :as r]
            [taoensso.timbre :as log]
            [scp.keyboard :as key]
            [scp.map :as map]
            [scp.util :as util]
            ["rot-js" :as rot]
            [clara.rules :as clara]))

(defonce db (r/atom {:player   {:position [1 1]}
                     :display  (new rot/Display (clj->js {:width 50 :height 50 :forceSquareRatio true}))
                     :room-map (map/get-map :2)}))

(defonce _
  (.appendChild (util/root-element)
                (.getContainer (:display @db))))

(defn refresh []
  (log/info "loaded")
  #_(r/render [app] (util/root-element))
  (key/shortcuts db))

(comment
  (refresh)

  (swap! db assoc :player {:position [4 3]})
  (swap! db assoc :room-map (map/get-map :3))
  (map/get-map :3)

  (map/can-stand [1 0] (:room-map @db)))

(defn display-option
  ([name]
   (-> (:display @db)
       (.getOptions)
       (js->clj :keywordize-keys true)
        name)))

(comment

  (.getOptions (:display @db))

  (display-option :width)

  (.draw (:display @db) 2 2 "" "" "#ccc")
  (.draw (:display @db) 1 1 "@")

  (def m (-> (new rot/Map.Digger 50 50)
             (.create (.-DEBUG (:display @db)))
             #_(js->clj :keywordize-keys true)))

  (def d (.create m))

  (-> d
      (.getCorridors))

  (rot/SHOW (.getContainer (:display @b)))

  (.create m (.-DEBUG (:display @db)))

  map.create(display.DEBUG);

  (keys m)

  )




(comment
  display.draw(15, 4, "%", "#0f0");          /* foreground color */
  display.draw(25, 4, "#", "#f00", "#009");  /* and background color */

  var display = new ROT.Display({width:20, height:5});
  SHOW(display.getContainer()); /* do not forget to append to page! */
  )

(defrecord SupportRequest [client level])

(defrecord ClientRepresentative [name client])

(defrule is-important
         "Find important support requests."
         [SupportRequest (= :high level)]
         =>
         (println "High support requested!"))

(defrule notify-client-rep
         "Find the client representative and request support."
         [SupportRequest (= ?client client)]
         [ClientRepresentative (= ?client client) (= ?name name)]
         =>
         (println "Notify" ?name "that"
                  ?client "has a new support request!"))

(comment

  (defsession session 'scp.core)

  (-> session
      (clara/insert (->ClientRepresentative "Alice" "Acme")
                    (->SupportRequest "Acme" :high))
      (clara/fire-rules))
  )

(comment



  )