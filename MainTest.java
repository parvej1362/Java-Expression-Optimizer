
public class MainTest {

    static int testsPassed = 0;

    static void check(boolean condition, String testName) {
        assert condition : "FAILED: " + testName;
        testsPassed++;
        System.out.println("PASS: " + testName);
    }

    static Main.Expr parse(String input) {
        return new Main.Parser(input).parse();
    }

    static String optimized(String input) {
        return Main.format(Main.optimize(parse(input)));
    }

    static void expectInvalid(String input, String testName) {
        boolean thrown = false;

        try {
            parse(input);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        check(thrown, testName);
    }

    public static void main(String[] args) {

        // 1. Parsing and operator precedence
        check(
            Main.format(parse("2 + 3 * 4"))
                .equals("(2 + (3 * 4))"),
            "Multiplication has higher precedence than addition"
        );

        check(
            Main.format(parse("(2 + 3) * 4"))
                .equals("((2 + 3) * 4)"),
            "Parentheses override precedence"
        );

        check(
            Main.format(parse("total_1 + 25"))
                .equals("(total_1 + 25)"),
            "Variables and multi-digit numbers"
        );

        check(
            Main.format(parse(" 2 + 3 "))
                .equals("(2 + 3)"),
            "Whitespace is ignored"
        );

        // 2. Constant folding
        check(
            optimized("2 + 3 * 4").equals("14"),
            "Constant folding respects precedence"
        );

        check(
            optimized("(8 - 3) * 2").equals("10"),
            "Constant folding with parentheses"
        );

        check(
            optimized("20 / 4").equals("5"),
            "Integer division with no remainder"
        );

        // 3. Algebraic simplification
        check(
            optimized("x + 0").equals("x"),
            "Remove zero from addition"
        );

        check(
            optimized("0 + x").equals("x"),
            "Remove zero from left side of addition"
        );

        check(
            optimized("x - 0").equals("x"),
            "Remove zero from subtraction"
        );

        check(
            optimized("x * 1").equals("x"),
            "Remove one from multiplication"
        );

        check(
            optimized("1 * x").equals("x"),
            "Remove one from left side of multiplication"
        );

        check(
            optimized("x * 0").equals("0"),
            "Multiply by zero"
        );

        check(
            optimized("x / 1").equals("x"),
            "Divide by one"
        );

        // 4. Preserve unsafe or non-integral calculations
        check(
            optimized("5 / 2").equals("(5 / 2)"),
            "Do not fold division with a remainder"
        );

        check(
            optimized("5 / 0").equals("(5 / 0)"),
            "Preserve division by zero"
        );

        check(
            optimized("2147483647 + 1")
                .equals("(2147483647 + 1)"),
            "Preserve integer overflow"
        );

        // 5. Count operations before and after optimization
        Main.Expr original = parse(
            "(3 * 4) + (x * 1) + (10 - 10)"
        );

        Main.Expr result = Main.optimize(original);

        check(
            Main.countOperations(original) == 5,
            "Count operations before optimization"
        );

        check(
            Main.countOperations(result) == 1,
            "Count operations after optimization"
        );

        check(
            Main.format(result).equals("(12 + x)"),
            "Optimize complete sample expression"
        );

        // 6. Invalid expressions
        expectInvalid("", "Reject empty expression");
        expectInvalid("2 +", "Reject missing operand");
        expectInvalid("(2 + 3", "Reject missing closing parenthesis");
        expectInvalid("2 + )", "Reject unexpected closing parenthesis");
        expectInvalid("2 $ 3", "Reject unsupported operator");

        System.out.println(
            "\nAll " + testsPassed + " tests passed!"
        );
    }
}
