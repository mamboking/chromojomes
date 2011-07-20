;Copyright (c) 2010, Kevin Hostelley
;All rights reserved.
;
;Redistribution and use in source and binary forms, with or without
;modification, are permitted provided that the following conditions are
;met:
;
;    * Redistributions of source code must retain the above copyright
;      notice, this list of conditions and the following disclaimer.
;
;    * Redistributions in binary form must reproduce the above
;      copyright notice, this list of conditions and the following
;      disclaimer in the documentation and/or other materials provided
;      with the distribution.
;
;THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
;"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
;LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
;A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
;HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
;SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
;LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
;DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
;THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
;(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
;OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(ns org.mambo.guesser)

(def max-runs 100000)
(def start [0 1 1 2 3 5 8 13])
(def goal [21 34 55 89 144])

(defn adder [x]
  (+ x 2))

(defn optional-args? [some-var]
  (not
    (empty?
      (flatten (map (fn [arglist] (filter #{'&} arglist))
                 ((meta some-var) :arglists))))))

(defn required-arg-counts [some-var]
  (map
    (fn [arglist]
      (count (take-while (fn [x] (not (= '& x))) arglist)))
    ((meta some-var) :arglists)))

(defn available-fns [some-ns]
  (loop [h (hash-map) v (filter var? (vals (ns-map some-ns)))]
    (if (empty? v)
      h
      (recur
        (assoc h (first v) (required-arg-counts (first v)))
        (rest v)))))



(defn fns-by-arity [some-fns]
  (reduce (fn [m x]
            (let [new-keys (last x) new-val (first x)]
              (reduce (fn [inside-map new-key]
                        (assoc inside-map new-key (cons new-val (m new-key))))
                m
                (set new-keys))))
    (hash-map)
    some-fns))

;
;
;                      (defn find-fn-with-arity [x some-ns]
;                        (loop [l (list) k (keys (available-fns some-ns))]
;                          (if (empty? k)
;                            l
;                            (recur (if (
