(ns clojuremain.core)

(defn simplify-nested [expr]
  (if (seq? expr)
    (map #(if (seq? %) (simplify-nested %) %) expr)
    expr))

(defn simplify [e]
  (let [expr (simplify-nested e)]
    (cond
      ;; (nor false) => true
      (= expr '(nor false)) true

      ;; (nor true) => false
      (= expr '(nor true)) false

      ;;  (nor (nor x)) => x
      (and (seq? expr)
           (= 'nor (first expr))
           (seq? (second expr))
           (= 'nor (first (second expr))))
      (second (second expr))

      ;; (nor (nor (nor x))) => (nor x)
      (and (seq? expr)
           (= 'nor (first expr))
           (seq? (second expr))
           (= 'nor (first (second expr)))
           (seq? (second (second expr)))
           (= 'nor (first (second (second expr)))))
      (list 'nor (second (second (second expr))))

      ;; (nor (nor (nor (nor x)))) => x
      (and (seq? expr)
           (= 'nor (first expr))
           (seq? (second expr))
           (= 'nor (first (second expr)))
           (seq? (second (second expr)))
           (= 'nor (first (second (second expr))))
           (seq? (second (second (second expr))))
           (= 'nor (first (second (second (second expr))))))
      (second (last expr))

      ;;  (nor x x) => (nor x)
      (and (seq? expr)
           (= 'nor (first expr))
           (= (second expr) (last expr)))
      (list 'nor (second expr))

      ;;  (nor x y) => (nor x y)
      (and (seq? expr)
           (= 'nor (first expr))
           (<= (count expr) 3))
      expr

      ;; (nor x true) => false
      (and (seq? expr)
           (= 'nor (first expr))
           (= (last expr) true))
      false

      ;;  (nor x false) => (nor x)
      (and (seq? expr)
           (= 'nor (first expr))
           (= (last expr) false))
      (list 'nor (first expr))

      ;;  (nor false false) => true
      (and (seq? expr)
           (= 'nor (first expr))
           (every? #(= false %) (rest expr)))
      true

      ;; Rule: (nor x y false) => (nor x y)
      (and (seq? expr)
           (= 'nor (first expr))
           (= (last expr) false))
      (take (dec (count expr)) expr)

      ;; Rule: (nor x y true) => false
      (and (seq? expr)
           (= 'nor (first expr))
           (= (last expr) true))
      false

      ;; Recursively simplify each element if it's a sequence
      :else (if (seq? expr)
              (map simplify expr)
              expr))))

(defn not-to-nor [expr]
  (let [arg (first (rest expr))]
    (list 'nor arg)))

(defn or-to-nor [expr]
  (list 'nor (cons 'nor (rest expr))))

(defn and-to-nor [expr]
  (cons 'nor (map #(list 'nor %) (rest expr))))

(defn nor-single [expr]
  (cond
    (and (list? expr) (= 'not (first expr)))
    (not-to-nor expr)
    (and (list? expr) (= 'or (first expr)))
    (or-to-nor expr)
    (and (list? expr) (= 'and (first expr)))
    (and-to-nor expr)
    :else expr))

(defn nor-convert [expr]
  (let [converted-expr (nor-single expr)]
    (if (seq? converted-expr)
      (map #(if (seq? %) (nor-convert %) %) converted-expr)
      converted-expr)))

(defn bind-values [exp bindings]
  (if (seq? exp)
    (map #(bind-values % bindings) exp)
    (if (and (symbol? exp) (contains? bindings exp))
      (bindings exp)
      exp)))

(defn evalexp [exp bindings]
  (simplify (bind-values (nor-convert exp) bindings)))
(simplify '(nor (nor (nor (nor (nor true))))))



