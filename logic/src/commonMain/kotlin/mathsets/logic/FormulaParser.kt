package mathsets.logic

/**
 * A recursive-descent parser that converts a Unicode string representation
 * of a first-order formula into a [Formula] AST.
 *
 * The parser recognizes the following grammar (from lowest to highest precedence):
 *
 * | Precedence | Operator | Symbol |
 * |------------|----------|--------|
 * | 1 (lowest) | Biconditional | `↔` |
 * | 2          | Implication   | `→` |
 * | 3          | Disjunction   | `∨` |
 * | 4          | Conjunction   | `∧` |
 * | 5          | Negation      | `¬` |
 * | 6 (highest)| Quantifiers / Atoms | `∀`, `∃`, `∈`, `=` |
 *
 * Parentheses `(` `)` override precedence. Quantifiers bind as
 * `∀x(body)` / `∃x(body)`.
 *
 * Usage:
 * ```kotlin
 * val formula = FormulaParser.parse("∀x(x ∈ A → x ∈ B)")
 * ```
 *
 * @see Formula
 * @see FormulaPrettyPrinter
 * @throws IllegalStateException if the input string contains syntax errors.
 */
object FormulaParser {
    /**
     * Parses a Unicode formula string into a [Formula] AST.
     *
     * @param input The formula string to parse (e.g., `"∀x(x ∈ A)"`).
     * @return The parsed [Formula] AST.
     * @throws IllegalStateException if the input is not a well-formed formula.
     */
    fun parse(input: String): Formula {
        val parser = Parser(tokenize(input))
        val formula = parser.parseFormula()
        parser.expectEnd()
        return formula
    }

    private fun tokenize(input: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when {
                c.isWhitespace() -> i++
                c in listOf('(', ')', ',', '¬', '∧', '∨', '→', '↔', '∀', '∃', '∈', '=') -> {
                    tokens.add(c.toString())
                    i++
                }
                c.isLetterOrDigit() || c == '_' -> {
                    val start = i
                    while (i < input.length && (input[i].isLetterOrDigit() || input[i] == '_')) i++
                    tokens.add(input.substring(start, i))
                }
                else -> error("Unexpected character '$c' in formula.")
            }
        }
        return tokens
    }

    private class Parser(private val tokens: List<String>) {
        private var index: Int = 0

        fun parseFormula(): Formula = parseIff()

        fun expectEnd() {
            if (index < tokens.size) {
                error("Unexpected token '${tokens[index]}' at end of input.")
            }
        }

        private fun parseIff(): Formula {
            var left = parseImplies()
            while (match("↔")) {
                val right = parseImplies()
                left = Formula.Iff(left, right)
            }
            return left
        }

        private fun parseImplies(): Formula {
            var left = parseOr()
            while (match("→")) {
                val right = parseOr()
                left = Formula.Implies(left, right)
            }
            return left
        }

        private fun parseOr(): Formula {
            var left = parseAnd()
            while (match("∨")) {
                val right = parseAnd()
                left = Formula.Or(left, right)
            }
            return left
        }

        private fun parseAnd(): Formula {
            var left = parseNot()
            while (match("∧")) {
                val right = parseNot()
                left = Formula.And(left, right)
            }
            return left
        }

        private fun parseNot(): Formula {
            if (match("¬")) return Formula.Not(parseNot())
            return parseQuantifiedOrAtomic()
        }

        private fun parseQuantifiedOrAtomic(): Formula {
            if (match("∀")) {
                val variable = expectIdentifier()
                expect("(")
                val body = parseFormula()
                expect(")")
                return Formula.ForAll(variable, body)
            }
            if (match("∃")) {
                val variable = expectIdentifier()
                expect("(")
                val body = parseFormula()
                expect(")")
                return Formula.Exists(variable, body)
            }
            if (match("(")) {
                val nested = parseFormula()
                expect(")")
                return nested
            }
            return parseAtomic()
        }

        private fun parseAtomic(): Formula {
            val left = parseTerm()
            return when {
                match("∈") -> Formula.Membership(left, parseTerm())
                match("=") -> Formula.Equals(left, parseTerm())
                else -> error("Expected '∈' or '=' after term.")
            }
        }

        private fun parseTerm(): Term {
            val id = expectIdentifier()
            if (match("(")) {
                val args = mutableListOf<Term>()
                if (!match(")")) {
                    do {
                        args.add(parseTerm())
                    } while (match(","))
                    expect(")")
                }
                return Term.App(id, args)
            }
            return Term.Var(id)
        }

        private fun expectIdentifier(): String {
            val token = peek() ?: error("Expected identifier but reached end of input.")
            if (token.isEmpty() || !token[0].isLetter()) {
                error("Expected identifier, found '$token'.")
            }
            index++
            return token
        }

        private fun expect(expected: String) {
            if (!match(expected)) error("Expected '$expected'.")
        }

        private fun match(expected: String): Boolean {
            val token = peek() ?: return false
            if (token == expected) {
                index++
                return true
            }
            return false
        }

        private fun peek(): String? = tokens.getOrNull(index)
    }
}
