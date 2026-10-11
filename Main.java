
import java.util.Objects;

public class Main {

    // Every expression in our tree implements Expr.
    interface Expr {}

    // Leaf nodes: numbers and variable names.
    record Num(int value) implements Expr {}
    record Var(String name) implements Expr {}

    // Internal node: left operator right.
    record Bin(char op, Expr left, Expr right) implements Expr {}

    // Converts a string into an expression tree.
    static class Parser {
        private final String s;
        private int pos = 0;

        Parser(String s) {
            this.s = s.replaceAll("\\s+", "");
        }

        Expr parse() {
            Expr e = expression();
            if (pos != s.length())
                throw new IllegalArgumentException("Unexpected: " + s.charAt(pos));
            return e;
        }

        // expression = term (('+' | '-') term)*
        Expr expression() {
            Expr e = term();
            while (pos < s.length()
                    && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) {
                char op = s.charAt(pos++);
                e = new Bin(op, e, term());
            }
            return e;
        }

        // term = factor (('*' | '/') factor)*
        Expr term() {
            Expr e = factor();
            while (pos < s.length()
                    && (s.charAt(pos) == '*' || s.charAt(pos) == '/')) {
                char op = s.charAt(pos++);
                e = new Bin(op, e, factor());
            }
            return e;
        }

        // factor = number | variable | '(' expression ')'
        Expr factor() {
            if (pos >= s.length())
                throw new IllegalArgumentException("Unexpected end");

            char c = s.charAt(pos);

            if (c == '(') {
                pos++;
                Expr e = expression();
                if (pos >= s.length() || s.charAt(pos++) != ')')
                    throw new IllegalArgumentException("Missing ')'");
                return e;
            }

            if (Character.isDigit(c)) {
                int start = pos;
                while (pos < s.length()
                        && Character.isDigit(s.charAt(pos))) pos++;
                return new Num(Integer.parseInt(s.substring(start, pos)));
            }

            if (Character.isLetter(c) || c == '_') {
                int start = pos++;
                while (pos < s.length()
                        && (Character.isLetterOrDigit(s.charAt(pos))
                            || s.charAt(pos) == '_')) pos++;
                return new Var(s.substring(start, pos));
            }

            throw new IllegalArgumentException("Unexpected: " + c);
        }
    }

    // Recursively applies optimization rules.
    static Expr optimize(Expr e) {
        if (!(e instanceof Bin b)) return e;

        Expr l = optimize(b.left());
        Expr r = optimize(b.right());

        // Constant folding: evaluate operations on two constants.
        if (l instanceof Num a && r instanceof Num c) {
            int x = a.value(), y = c.value();
            try {
                return switch (b.op()) {
                    case '+' -> new Num(Math.addExact(x, y));
                    case '-' -> new Num(Math.subtractExact(x, y));
                    case '*' -> new Num(Math.multiplyExact(x, y));
                    case '/' -> {
                        if (y == 0 || (x == Integer.MIN_VALUE && y == -1))
                            yield new Bin(b.op(), l, r);
                        if (x % y != 0)
                            yield new Bin(b.op(), l, r);
                        yield new Num(x / y);
                    }
                    default -> new Bin(b.op(), l, r);
                };
            } catch (ArithmeticException ex) {
                // Preserve the expression if integer arithmetic overflows.
                return new Bin(b.op(), l, r);
            }
        }

        // Algebraic simplification.
        if (b.op() == '+') {
            if (isNum(l, 0)) return r;
            if (isNum(r, 0)) return l;
        }
        if (b.op() == '-') {
            if (isNum(r, 0)) return l;
        }
        if (b.op() == '*') {
            if (isNum(l, 1)) return r;
            if (isNum(r, 1)) return l;
            if (isNum(l, 0) || isNum(r, 0)) return new Num(0);
        }
        if (b.op() == '/') {
            if (isNum(r, 1)) return l;
        }

        return new Bin(b.op(), l, r);
    }

    static boolean isNum(Expr e, int value) {
        return e instanceof Num n && n.value() == value;
    }

    // Prints the expression with parentheses to preserve structure.
    static String format(Expr e) {
        if (e instanceof Num n) return String.valueOf(n.value());
        if (e instanceof Var v) return v.name();
        Bin b = (Bin) e;
        return "(" + format(b.left()) + " " + b.op() + " "
                + format(b.right()) + ")";
    }

    static int countOperations(Expr e) {
        if (!(e instanceof Bin b)) return 0;
        return 1 + countOperations(b.left()) + countOperations(b.right());
    }

    public static void main(String[] args) {
        String input = args.length > 0
                ? String.join(" ", args)
                : "(3 * 4) + (x * 1) + (10 - 10)";

        try {
            Expr original = new Parser(input).parse();
            Expr result = optimize(original);

            System.out.println("Original:          " + format(original));
            System.out.println("Optimized:         " + format(result));
            System.out.println("Operations before: " + countOperations(original));
            System.out.println("Operations after:  " + countOperations(result));
        } catch (RuntimeException ex) {
            System.err.println("Invalid expression: " + ex.getMessage());
        }
    }
}
