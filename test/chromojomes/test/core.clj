(ns chromojomes.test.core
  (:require [clojure.zip :as zip])
  (:use chromojomes.core)
  (:use midje.sweet))

(def test-func-tree '(+ (* x x) (* x y z)))
(def zipped-func (zip/seq-zip (seq test-func-tree)))

(fact
 (get-branch zipped-func) => zip/branch?
 (get-branch (zip/down zipped-func)) => zip/branch?)

(fact
 (count-args zipped-func) => 2
 (count-args (-> zipped-func zip/next zip/right zip/right)) => 3)

(fact
 (count-nodes zipped-func) => 11
 (count-nodes (-> zipped-func zip/next zip/right zip/right)) => 5)
