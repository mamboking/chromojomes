(ns chromojomes.mutations)

(defn gen-rand-arg []
  (let [args (list 'x 'y (rand-int 100) Math/E Math/PI)]
    (rand-nth args)))

(defn apply-random-fn [args function-seq]
  (cons (first (rand-nth (filter
                          (fn [x]
                            (.contains (second x) (count args)))
                          function-seq))) args))

