(ns chromojomes.discovery)

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


