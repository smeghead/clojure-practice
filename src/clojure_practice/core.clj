(ns clojure-practice.core
  (:gen-class))

(defrecord Point [x y])

(defprotocol Tile)

(defrecord Cell [point value fixed is-goal]
  Tile)

(defrecord Wall []
  Tile)

(defrecord Board [matrix])

(defn create-board [lines]
  (let [matrix (vec (map-indexed
                      (fn [y line]
                        (vec (map-indexed
                               (fn [x c]
                                 (case c
                                   \A (->Cell (->Point x y) 0 false false)
                                   \B (->Cell (->Point x y) Integer/MAX_VALUE false true)
                                   \. (->Cell (->Point x y) Integer/MAX_VALUE false false)
                                   \# (->Wall)
                                   (throw (Exception. (str "Unexpected character: " c)))))
                               (seq line))))
                      lines))]
    (->Board matrix)))

(defn get-cell [board point]
  (let [tile (get-in (:matrix board) [(:y point) (:x point)])]
    (when (instance? Cell tile) tile)))

(defn get-cells [board]
  (filter #(instance? Cell %) (apply concat (:matrix board))))

(defn update-cell [board point fn]
  (update-in board [:matrix (:y point) (:x point)] fn))

(defn get-unfixed-smallest-cell [b]
  (let [cells (get-cells b)
        cells (filter (fn [c] (= (:fixed c) false)) cells)
        sorted (sort-by :value cells)
        smallest (first sorted)]
    (if (not= (:value smallest) Integer/MAX_VALUE)
      smallest)))

(defn get-active-cell [b p]
  (get-cell b p))

(defn get-neighborhood-cells [b p]
  (vec (filter identity [(get-active-cell b (->Point (dec (:x p)) (:y p)))
                         (get-active-cell b (->Point (inc (:x p)) (:y p)))
                         (get-active-cell b (->Point (:x p) (dec (:y p))))
                         (get-active-cell b (->Point (:x p) (inc (:y p))))])))


(defn spread-goal [board]
  (let [goal (some #(when (:is-goal %) %) (get-cells board))]

    (defn visible-cells [b p move-fn]
      (let [next-point (move-fn p)]
        (if (get-active-cell b next-point)
          (recur (update-cell b next-point #(assoc % :is-goal true))
                 next-point
                 move-fn)
          b)))

    (let [move-fns [(fn [p] (->Point (:x p) (inc (:y p))))
                    (fn [p] (->Point (:x p) (dec (:y p))))
                    (fn [p] (->Point (inc (:x p)) (:y p)))
                    (fn [p] (->Point (dec (:x p)) (:y p)))]]
      (reduce (fn [acc fn] (visible-cells acc (:point goal) fn)) board move-fns))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
