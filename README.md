
# Java Expression Optimizer

A small compiler-related project in Java that parses
arithmetic expressions into an expression tree and applies
basic optimization rules.

## Features

- Recursive-descent parsing
- Expression-tree representation
- Constant folding
- Algebraic simplification
- Before-and-after operation counts
- Basic error handling

## Example

Input:

(3 * 4) + (x * 1) + (10 - 10)

Optimized expression:

(12 + x)

Operations before: 5
Operations after: 1

## Concepts explored

- Parsing and operator precedence
- Expression trees
- Bottom-up optimization
- Constant folding
- Algebraic identities
- Testing edge cases

## Run locally

Requires JDK 16 or later.

```bash
javac Main.java
java Main
java Main "(2 + 3) * (4 - 1)"
```

## Limitations

This is a learning project, not a production compiler.
It handles a small arithmetic-expression grammar and
does not compile Java source or optimize JVM bytecode.



## Automated Testing

The Java Expression Optimizer includes an automated test suite using Java's built-in `assert` statements, without requiring external testing libraries.

### Test Coverage

- **Expression Parsing:** Tests operator precedence, parentheses, whitespace handling, variables, and multi-digit numbers.
- **Constant Folding:** Verifies compile-time evaluation of arithmetic expressions.
- **Algebraic Simplification:** Tests rules such as `x + 0 → x`, `x * 1 → x`, and `x * 0 → 0`.
- **Arithmetic Safety:** Verifies safe handling of division by zero, non-integral division, and integer overflow.
- **Optimization Metrics:** Checks whether optimization reduces the number of operations in an expression tree.
- **Invalid Input Handling:** Tests empty expressions, missing operands, unmatched parentheses, and unsupported operators.

### Project Structure

```text
ExpressionOptimizer/
├── Main.java       # Parser, expression tree, and optimizer
├── MainTest.java   # Automated test suite
└── README.md
```

### How to Run the Tests

**Prerequisites:** Install a compatible Java Development Kit (JDK) and ensure `java` and `javac` are available in your terminal.

**1. Compile the source files**

```bash
javac Main.java MainTest.java
```

**2. Run the test suite with assertions enabled**

```bash
java -ea MainTest
```

The `-ea` flag enables Java assertions. The test runner prints individual test results and reports the total number of tests passed. A failed assertion identifies the failing test and stops execution.

### Example

**Input Expression:**

```text
(3 * 4) + (x * 1) + (10 - 10)
```

**Optimized Expression:**

```text
(12 + x)
```

This demonstrates constant folding, elimination of multiplication by one, and evaluation of constant subtraction.

### Testing Approach

The test suite validates expected expression-tree output, operation counts, and exception handling. It helps ensure that existing functionality continues to work when parsing or optimization rules are modified.

### Future Improvements

- Integrate JUnit 5 for structured test execution and reporting.
- Add randomized expression generation for broader test coverage.
- Implement differential testing to compare the evaluation of original and optimized expressions.
- Add edge cases for deeply nested expressions and arithmetic boundaries.
