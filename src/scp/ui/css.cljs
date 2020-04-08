(ns scp.ui.css)

(defn translate [i]
  (if (> i 9)
    (char (+ 87 i))
    i))

(defn ->base
  [base]
  (fn [i]
    (->> i
         (iterate #(quot % base))
         (take-while pos?)
         (map #(mod % base))
         reverse
         (map translate)
         (map str)
         (apply str))))

(def ->hex (->base 16))

(defn rgb->hex [r g b]
  (str "#" (->hex r) (->hex g) (->hex b)))
(defn gray->hex [s]
  (rgb->hex s s s))
(comment (gray->hex 42))

(def text-color (gray->hex 183))

(def root
  [[:body
    {:color text-color}]
   [:label
    {:color (gray->hex 190)}]
   [:a
    {:color (rgb->hex 143 143 240)}]
   [:a:hover
    {:color (rgb->hex 106 106 255)}]])
