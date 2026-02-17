---
name: kotlin-fundamentals
description: |
  Prover diretrizes concisas e idiomáticas para escrever, analisar e refatorar código Kotlin. Use when: agente precisa gerar, revisar ou refatorar código Kotlin para produção.
---

# kotlin-fundamentals

Overview
A referência mínima e pragmática para gerar código Kotlin que seja idiomático, seguro e interoperável. Foco em decisões de engenharia — preferir imutabilidade, respeitar null-safety e usar construções idiomáticas do idioma.

Paradigmas
Kotlin une orientação a objetos (classes, interfaces) com programação funcional (funções de ordem superior, lambdas, imutabilidade). Preferir composição sobre herança, usar funções puras quando possível e modelar estados com tipos (data classes, sealed classes) em vez de flags e nulls.

Diretrizes de Geração de Código
- Imutabilidade
  - Prefere propriedades val por padrão; use var apenas quando mutação for necessária e documentada.
  - Para coleções, prefira as interfaces imutáveis (List, Set, Map) e copie/modifique ao invés de alterar in-place.

- Null Safety
  - Use tipos não-null sempre que possível. Evite o operador !!; trate null explicitamente com safe-calls (?.), elvis (?:) ou quando apropriado, com tipos selados que representam ausência.
  - Preferir contratos e validações no início da função (guard clauses) para tornar o fluxo de código claro.

- Recursos idiomáticos
  - Data classes para DTOs e value-objects; inclua validação externa se necessário.
  - Sealed classes para hierarquias fechadas (representação de estados, resultados).
  - Smart casts são preferíveis a casts explícitos quando o compilador pode garantir o tipo.
  - Extension functions para adicionar comportamento sem alterar tipos existentes.
  - Use expression body functions e single-expression properties para clareza quando a implementação for simples.

- Coroutines e concorrência
  - Preferir kotlinx.coroutines para concorrência: struktured concurrency, uso de CoroutineScope adequado (viewModelScope / lifecycleScope / applicationScope) e evitar GlobalScope.
  - Escopos devem ser canceláveis e ter um supervisor onde necessário; usar Dispatchers explicitamente quando interação com IO/CPU for esperada.

- API Design e Naming
  - Nomes claros, verbos para funções que realizam ações, substantivos para modelos/valores.
  - Preferir APIs imutáveis e fábricas (companion object / named constructors) quando necessário.
  - Documentar contratos de thread-safety e side effects.

- Testabilidade
  - Projetar para injeção de dependências (preferir DI leve: interfaces + construtores; Koin/Dagger quando apropriado).
  - Testes unitários pequenos e determinísticos; usar runBlockingTest / TestCoroutineDispatcher para coroutines.

How to generate idiomatic Kotlin
1. Preferir val; reavaliar se var é realmente necessária.
2. Escolher data class para transporte de dados; adicionar funções de validação fora da classe quando pertinente.
3. Usar sealed classes para estados finitos e when sem else para forçar exaustividade.
4. Substituir loops mutáveis por mapas, filters e folds quando a lógica for transformacional.
5. Evitar nulls como sinal de erro; usar Result/Either (Arrow) ou sealed classes para representar falhas.

How to ensure null-safety and avoid !!
1. Validar entradas no início da função e lançar IllegalArgumentException ou retornar Result em caso de inválidos.
2. Operar com safe-call (?.) e elvis (?:) para fluxo claro:
   - Exemplo: val length = user?.name?.length ?: 0
3. Quando interoperar com Java, anotar contratos ( @NotNull / @Nullable ) e envolver valores Java inseguros com validações antes de propagar.

How to apply immutability patterns
1. Expor coleções como List<T> (imutável) e criar cópias com toMutableList() ao modificar internamente.
2. Preferir construção imutável com copy() em data classes:
   - Exemplo: val updated = user.copy(name = "novo")
3. Para estados complexos, modelar com sealed classes e aplicar when para transições explícitas.

How to use coroutines safely
1. Encapsular lógica assíncrona em funções suspensas (suspend) e expor apenas as operações relevantes.
2. Usar withContext(Dispatchers.IO) para IO e Dispatchers.Default para trabalho CPU-bound.
3. Testar com TestCoroutineScope/TestDispatcher; evitar sleeps e timeouts reais em testes.

Reference Routing
- Ler references/java_js_interop.md quando:
  - houver interoperabilidade com Java (JVM) ou consumo/produção de bibliotecas Java, ou precisar usar anotações @Jvm*.
  - houver target Kotlin/JS ou uso de dynamic/JS wrappers.
- Ler references/ecosystem_and_science.md quando:
  - for necessário escolher bibliotecas, frameworks, ou avaliar opções para data science e aplicações científicas.
- Regra prática: carregar referência somente se a tarefa requer decisões de compatibilidade, build targets múltiplos, ou seleção de bibliotecas; para recomendações lineares e snippets curtos, seguir as diretrizes acima.

Token Economy (orientação rápida)
- Conceitos triviais (naming, val vs var): 1 sentença.
- Nuances (interoperabilidade, coroutines testing): 1 parágrafo + pequeno exemplo.
- Tópicos avançados e decisão de bibliotecas: mover para references/ e citar quando ler.

References: arquivos auxiliares em references/ (leitura sob demanda)
- references/java_js_interop.md
- references/ecosystem_and_science.md

(Fim do SKILL.md)
