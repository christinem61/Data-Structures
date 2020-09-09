# Infix Expression Evaluator

This is a calculator that evaluates complex infix expressions in Java. It uses recursion,
stacks, and other data structures to compute and print answers. 

Here are some examples of expressions the program will evaluate:
- 3
- Xyz
- 3-4*5
- a-(b+A[B[2]])*d+3
- A[2*(a+b)]
- (varx + vary*varz[(vara+varb[(a+b)*33)])/55

Expressions are limited to:
- integer constants
- non-array variables with integer values
- arrays of integers, indexed with a constant or subexpression
- addition, subtraction, multiplication and division operators
- parenthesized subexpressions
