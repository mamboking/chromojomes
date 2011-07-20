(ns chromojomes.core
  (:require [clojure.zip :as zip]))

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
  (loop [h (hash-map) v (filter var? (vals (ns-publics some-ns)))]
    (if (empty? v)
      (filter (fn [x] (seq (second x))) h)
      (recur
       (assoc h ((meta (first v)) :name) (required-arg-counts (first v)))
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

(def function-seq (seq (available-fns 'clojure.core)))

(defn get-branch [loc]
  (if (zip/branch? loc)
    loc
    (zip/up loc)))

(defn count-args [loc]
  (loop [point (zip/next (get-branch loc))
         count 0]
    (if (nil? (zip/right point))
      count
      (recur (zip/right point) (inc count)))))

(defn gen-rand-arg []
  (let [args (list 'x 'y (rand-int 100) Math/E Math/PI)]
    (rand-nth args)))

(defn apply-random-fn [args]
  (cons (first (rand-nth (filter
                          (fn [x]
                            (.contains (second x) (count args)))
                          function-seq))) args))

(defn mutate-arg [arg-loc]
  (zip/edit
   arg-loc
   (fn [x] (apply-random-fn [x (gen-rand-arg)]))))

(defn random-arg [loc]
  (loop [point (zip/right (zip/next (get-branch loc)))
         steps (rand-int (count-args loc))]
    (if (= steps 0)
      point
      (recur (zip/right point) (dec steps)))))

(defn replacefn [loc]
  (zip/edit
   (zip/next (get-branch loc))
   (fn [x] (rand-nth function-seq))))

(defn count-nodes [loc]
  (loop [point loc
         count 0]
    (if (zip/end? point)
      count
      (recur (zip/next point) (inc count)))))

(defn random-loc [loc]
  (let [max-steps (rand-int (count-nodes loc))]
    (loop [point loc
           step 0]
      (if (or (>= step max-steps) (zip/end? (zip/next point)))
        point
        (recur (zip/next point) (+ step 1))))))

(defn mutate-loc [loc]
  (-> loc random-loc random-arg mutate-arg zip/root seq zip/seq-zip))

(defn test-mutate []
  (doseq [x (take 11 (iterate mutate-loc root-loc))] (prn (zip/root x))))

(defn add-func [tree]
  tree)

(defn remove-func [tree]
  tree)

(defn add-branch [tree]
  tree)

(defn remove-branch [tree]
  tree)

(defn add-dataset [tree]
  tree)

(defn remove-dataset [tree]
  tree)

(def mutations #{add-func remove-func add-branch remove-branch add-dataset remove-dataset})

(def test-tree '(+ (* x x) (* x y)))

(def root-loc (zip/seq-zip (seq test-tree)))

(def sbs-financials {:closing-prices {:20110628 12.28
                                      :20110627 12.30
                                      :20110624 12.15
                                      :20110623 12.00}
                     :revenue-history {:20110430 1000000000
                                       :20110131  990000000
                                       :20101031 1010000000}})

