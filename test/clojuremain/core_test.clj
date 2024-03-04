(ns clojuremain.core-test
  (:require [clojure.test :refer :all]
            [clojuremain.core :refer :all]))

(deftest evalexp-test
  (testing evalexp
    (is (= (evalexp '(and x (or x (and y (not z)))) '{x false, z true}) 'false))))
