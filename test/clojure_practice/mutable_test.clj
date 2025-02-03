(ns clojure-practice.mutable-test
  (:require [clojure.test :refer :all]
            [clojure-practice.mutable :refer :all]))

(deftest point-test
  (testing "Pointを初期化する"
    (let [p (->Point 1 2)]
      (is (= (:x p) 1))
      (is (= (:y p) 2)))))

