(ns clojuremain.core)

;; Function to simplify NOR expressions
(defn simplify [expr]
  (cond
    ;; Rule: (nor false) => true
    (= expr '(nor false)) true

    ;; Rule: (nor true) => false
    (= expr '(nor true)) false

    ;; Rule: (nor (nor x)) => x
    (and (seq? expr)
         (= 'nor (first expr))
         (seq? (second expr))
         (= 'nor (first (second expr))))
    (second (second expr))

    ;; Rule: (nor (nor (nor x))) => (nor x)
    (and (seq? expr)
         (= 'nor (first expr))
         (seq? (second expr))
         (= 'nor (first (second expr)))
         (seq? (second (second expr)))
         (= 'nor (first (second (second expr)))))
    (list 'nor (second (second (second expr))))

    ;; Rule: (nor (nor (nor (nor x)))) => x
    (and (seq? expr)
         (= 'nor (first expr))
         (every? #(= 'nor %) (rest expr))
         (= 'nor (first (last expr))))
    (second (last expr))

    ;; Rule: (nor x x) => (nor x)
    (and (seq? expr)
         (= 'nor (first expr))
         (= (second expr) (last expr)))
    (list 'nor (second expr))

    ;; Rule: (nor x y) => (nor x y)
    (and (seq? expr)
         (= 'nor (first expr))
         (<= (count expr) 3))
    expr

    ;; Rule: (nor x true) => false
    (and (seq? expr)
         (= 'nor (first expr))
         (= (last expr) true))
    false

    ;; Rule: (nor x false) => (nor x)
    (and (seq? expr)
         (= 'nor (first expr))
         (= (last expr) false))
    (list 'nor (first expr))

    ;; Rule: (nor false false) => true
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

    ;; Recursive rule for nested simplification
    :else (map simplify expr)))

;; Function to convert NOT expressions to NOR
(defn not-to-nor [expr]
  (let [arg (first (rest expr))] ;; Extract the argument of the NOT expression
    (list 'nor arg))) ;; Return a NOR expression with the argument

;; Function to convert OR expressions to NOR
(defn or-to-nor [expr]
  (list 'nor (cons 'nor (rest expr)))) ;; Return a NOR expression with each OR argument negated and combined with NOR

;; Function to convert AND expressions to NOR
(defn and-to-nor [expr]
  (cons 'nor (map #(list 'nor %) (rest expr)))) ;; Return a NOR expression with each AND argument negated and combined with NOR

;; Function to convert single logical operations to NOR
(defn nor-single [expr]
  (cond
    (and (list? expr) (= 'not (first expr))) ;; If the expression is a NOT expression
    (not-to-nor expr) ;; Convert it to NOR
    (and (list? expr) (= 'or (first expr))) ;; If the expression is an OR expression
    (or-to-nor expr) ;; Convert it to NOR
    (and (list? expr) (= 'and (first expr))) ;; If the expression is an AND expression
    (and-to-nor expr) ;; Convert it to NOR
    :else expr)) ;; Otherwise, return the expression unchanged

;; Function to convert expressions to NOR
(defn nor-convert [expr]
  (let [converted-expr (nor-single expr)] ;; Convert single logical operations to NOR
    (if (seq? converted-expr) ;; If the result is a sequence
      (map #(if (seq? %) (nor-convert %) %) converted-expr) ;; Recursively apply nor-convert to each element of the sequence
      converted-expr))) ;; Return the converted expression

;; Function to bind values to variables in the expression


;; Function to bind values to variables in the expression
(defn bind-values [exp bindings]
  (if (seq? exp)
    (map #(bind-values % bindings) exp)
    (if (and (symbol? exp) (contains? bindings exp))
      (bindings exp)
      exp)))


;; Function to evaluate the expression with the provided bindings after simplification
(defn evalexp [exp bindings]
  (simplify (bind-values (nor-convert exp) bindings))) ;; Simplify the expression after converting it to NOR and binding values


(def p1 '(and x (or x (and y (not z)))))
(evalexp p1 '{x false, z true})
