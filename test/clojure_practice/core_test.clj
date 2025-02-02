(ns clojure-practice.core-test
  (:require [clojure.test :refer :all]
            [clojure-practice.core :refer :all]))

(deftest point-test
  (testing "Pointを初期化する"
    (let [p (->Point 1 2)]
      (is (= (:x p) 1))
      (is (= (:y p) 2)))))

(deftest cell-test
  (testing "Cellを初期化する"
    (let [c (->Cell (->Point 1 2) Integer/MAX_VALUE false false)]
      (is (= (:x (:point c) 1))
      (is (= (:y (:point c) 2))
      (is (= (:value c) Integer/MAX_VALUE)))))))

(deftest board-test
  (testing "Boardを初期化する"
    (let [matrix [
                  [
                    (->Cell (->Point 0 0) Integer/MAX_VALUE false false)
                    (->Cell (->Point 1 0) Integer/MAX_VALUE false false)
                  ]
                  [
                    (->Cell (->Point 0 1) Integer/MAX_VALUE false false)
                    (->Cell (->Point 1 1) Integer/MAX_VALUE false false)
                  ]
                 ]
          b (->Board matrix)]
      (is (= (get-in b [:matrix 1 0 :point :x] 0)))
      (is (= (get-in b [:matrix 1 0 :point :y] 1))))))

(deftest create-board-test
  (testing "面の初期化"
    (let [b (create-board ["A...."
                           ".#.#."
                           "....B"])]
      (is (= (get-in b [:matrix 0 0 :point :x] 0)))
      (is (= (get-in b [:matrix 0 0 :point :y] 0)))
      (is (= (get-in b [:matrix 0 0 :value] 0)))
      (is (= (get-in b [:matrix 1 0 :point :x] 1)))
      (is (= (get-in b [:matrix 1 0 :point :y] 0)))
      (is (= (get-in b [:matrix 1 0 :value] Integer/MAX_VALUE))))))

(deftest create-board-test-llm
  (testing "盤面を作成する"
    (let [lines ["A.#"
                 "..B"
                 "#.."]
          board (create-board lines)]
      (is (= (:matrix board) [[(->Cell (->Point 0 0) 0 false false)
                                (->Cell (->Point 1 0) Integer/MAX_VALUE false false)
                                (->Wall)]
                               [(->Cell (->Point 0 1) Integer/MAX_VALUE false false)
                                (->Cell (->Point 1 1) Integer/MAX_VALUE false false)
                                (->Cell (->Point 2 1) Integer/MAX_VALUE false true)]
                               [(->Wall)
                                (->Cell (->Point 1 2) Integer/MAX_VALUE false false)
                                (->Cell (->Point 2 2) Integer/MAX_VALUE false false)]])))))

(deftest get-cell-test
  (testing "指定した座標の Cell を取得する"
    (let [board (create-board ["A.#"
                               "..B"
                               "#.."])]
      (is (= (:value (get-cell board (->Point 0 0))) 0))
      (is (= (:is-goal (get-cell board (->Point 2 1))) true))
      (is (nil? (get-cell board (->Point 2 0)))))))  ;; 壁は `nil`

(deftest get-cells-test
  (testing "盤面内の Cell をすべて取得する"
    (let [board (create-board ["A.#"
                               "..B"
                               "#.."])
          cells (get-cells board)]
      (is (= (count cells) 7)) ;; 壁を除いた7つのCell
      (is (some #(= (:value %) 0) cells)) ;; "A" の Cell
      (is (some #(= (:is-goal %) true) cells))))) ;; "B" の Cell


(deftest update-cell-test
  (testing "指定したセルを確定させる"
    (let [board (create-board ["A.#"
                               "..B"
                               "#.."])
          board (update-cell board (->Point 0 0) (fn [c] (assoc c :fixed true)))
          cell (get-cell board (->Point 0 0))]
      (is (= (get cell :fixed) true)))))

(deftest get-unfixed-smallest-cell-test
  (testing "未確定のうち一番小さい値のCellを取得する"
    (let [board (create-board ["A.#"
                               "..B"
                               "#.."])
          board (update-cell board (->Point 0 0) (fn [c] (assoc c :fixed true)))
          board (update-cell board (->Point 1 0) (fn [c] (assoc c :value 1)))
          cell (get-unfixed-smallest-cell board)]
      (is (= (get cell :point) (->Point 1 0))))))

(deftest get-neighborhood-cells-test
  (testing "隣りあうCellを取得する 0 0"
    (let [board (create-board ["A...."
                               ".#.#."
                               "....B"])
          cells (get-neighborhood-cells board (->Point 0 0))]
      (is (= (get-in cells [0 :point]) (->Point 1 0))))))

(deftest spread-goal-test
  (testing "ゴールの拡張"
    (let [board (create-board ["..."
                               ".B."
                               "..."])
          updated-board (spread-goal board)]
      (is (every? :is-goal (get-neighborhood-cells updated-board (->Point 1 1)))))))
