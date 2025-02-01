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

;(defn spread-goal [board]
;  (let [goal (some #(when (:isGoal %) %) (get-cells board))]
;
;    (defn visible-cells [b p move-fn]
;      (let [next-point (move-fn p)]
;        (if (get-active-cell b next-point)
;          (recur (update-cell b next-point #(assoc % :isGoal true))
;                 next-point
;                 move-fn)
;          b)))
;
;    (let [move-fns [(fn [p] (->Point (:x p) (inc (:y p))))
;                    (fn [p] (->Point (:x p) (dec (:y p))))
;                    (fn [p] (->Point (inc (:x p)) (:y p)))
;                    (fn [p] (->Point (dec (:x p)) (:y p)))]]
;      (reduce (fn [acc fn] (visible-cells acc (:point goal) fn)) board move-fns))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
