(ns chromojomes.mutations
  (:require [clojure.zip :as zip])
  (:use chromojomes.discovery))

(def function-seq (seq (available-fns 'clojure.core)))

(defn gen-rand-arg []
  (let [args (list 'x 'y (rand-int 100) Math/E Math/PI)]
    (rand-nth args)))

(defn apply-random-fn [args fns]
  (cons (first (rand-nth (filter
                          (fn [x]
                            (.contains (second x) (count args)))
                          fns))) args))

(defn mutate-arg [arg-loc]
  (zip/edit
   arg-loc
   (fn [x] (apply-random-fn [x (gen-rand-arg)] function-seq))))

