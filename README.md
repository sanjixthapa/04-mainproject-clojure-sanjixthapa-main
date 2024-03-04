[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/uUHQBnAf)
# Clojure Main Project

In this project you will write a Clojure program to recursively convert logical expressions made up of the and, or, and not connectives to expressions using only the nor connective, then symbolically simplify the resulting expression.

As in the microproject, expressions are created as unevaluated lists. For example:

```clojure

(def p1 '(and x (or x (and y (not z)))))
(def p2 '(and (and z false) (or x true false)))
(def p3 '(or true a))
```

You should begin by building upon the microproject to implement a function which uses recursion to build the output for a nested expression. Your life will be easiest if the function works from the “inside” expressions outward. You may wish to modify deep-substitute for this task.

Then, write a Clojure program using at least one higher order function (e.g., map, filter, reduce, some, …) to symbolically simplify boolean nor relations of arbitrary length >= 1 represented as unevaluated lists. Simplification consists of replacing particular forms with equivalent forms or symbols. For example, in each of the below sample simplifications, given an unevaluated list representation of the item on the left, the result should be the item on the right.

```clojure
(nor false)               -> true
(nor true)                -> false
(nor (nor x))             -> x
(nor (nor (nor x)))       -> (nor x)
(nor (nor (nor (nor x)))) -> x
(nor x x)                 -> (nor x)
(nor x x x)               -> (nor x)
(nor x y)                 -> (nor x y)
(nor x true)              -> false
(nor x false)             -> (nor x)
(nor false false)         -> true
(nor x y false)           -> (nor x y)
(nor x false false)       -> (nor x)
(nor false false false)   -> true
(nor x y true)            -> false
(nor x y z)               -> (nor x y z)
```
You should generalize for any length expression based on these patterns. Your program must work for any arbitrary variables used. As in the microproject you may wish to write functions to handle certain kinds of cases, and handle the non-recursive case before you handle the recursive one.

The main entry point to the program, evalexp, calls functions that simplify, nor-convert, and bind these expressions. Binding consists of replacing some or all of the variables in expressions with constants (true or false), and then returning the partially evaluated form. We’ve already written a function which does this, in the form of deep-substitute.

The evalexp function should take a symbolic expression and a binding map and return the simplest form (that might just be a constant). One way to define this is:
```clojure
(defn evalexp [exp bindings]   
  (simplify (nor-convert (bind-values bindings exp))))
```
## Example:

```clojure
(evalexp p1 '{x false, z true})
```
binds x and z (but not y) in p1, leading to
```clojure
(and false (or false (and y (not true))))
```

Then the conversion to nor occurs resulting in:
```clojure
(nor (nor false)
     (nor (nor (nor false
                   (nor (nor y)
                        (nor (nor true)))))))
```
and then further simplifies to just `false`. You may wish to work out other cases for yourself in order to thoroughly test your code. Try experimenting with p1, p2, and p3 with varied variable binding maps.

## Suggested Development Environment:

[IntelliJ IDEA](https://www.jetbrains.com/idea/) + [Cursive](https://cursive-ide.com/)


