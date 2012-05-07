(ns chromojomes.test.discovery
  (:use chromojomes.discovery)
  (:use midje.sweet))

(fact
 (optional-args? #'clojure.core/+) => true
 (optional-args? #'clojure.core/first) => false)

(fact
 (required-arg-counts #'clojure.core/+) => '(0 1 2 2)
 (required-arg-counts #'clojure.core/first) => '(1)
 (required-arg-counts #'clojure.core/map) => '(2 3 4 4))