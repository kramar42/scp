(ns user
  (:require [shadow.cljs.devtools.api :as shadow]))

(comment

  (shadow/repl :main)

  (+ 1 1)

  (defn start [] (ra/start-figwheel!))

  (defn stop [] (ra/stop-figwheel!))

  (defn cljs [] (ra/cljs-repl "dev"))

  )