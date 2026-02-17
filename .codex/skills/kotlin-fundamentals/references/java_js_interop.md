# java_js_interop

Resumo
Documenta práticas e armadilhas na interoperabilidade Kotlin ↔ Java (JVM) e Kotlin → JavaScript (Kotlin/JS). Use este ficheiro quando código precisa de contratos binários estáveis, interoperabilidade com frameworks Java, ou integração com código JS dinâmico.

Interoperabilidade JVM (Kotlin ↔ Java)
- Conceito
  - Kotlin compila para bytecode JVM e é plenamente interoperável com Java. Tipos Kotlin não-null são vistos como platform types em Java; atenção a checagens de null.
- Anotações úteis
  - @JvmOverloads — gera overloads Java para funções com parâmetros default.
  - @JvmStatic — expõe funções de companion object como métodos estáticos em Java.
  - @JvmField — expõe uma propriedade como campo público (sem getter/setter) para Java.
  - @file:JvmName("NomeArquivo") — altera o nome do class file gerado para funções top-level.
- Boas práticas
  - Não confiar em null-safety do Kotlin ao receber objetos de Java: validar platform types explicitamente.
  - Ao projetar API pública que será consumida por Java, preferir overloads explícitos, não apenas parâmetros default.
  - Documentar contratos de thread-safety e nullability para consumidores Java.

Kotlin/JS e interoperability with JavaScript
- Conceito
  - Kotlin/JS compila para JS (IR ou Legacy). Tipos Kotlin e execução JS têm diferenças: dynamic existe para interoperabilidade direta.
- dynamic e bindings
  - O tipo dynamic permite chamar propriedades/métodos JS sem checagem estática; use-o com parcimônia.
  - Preferir declarações externas (external) e wrappers tipados para segurança:
    - external interface MyLib { fun doSomething(x: String): Int }
  - Criar wrappers Kotlin-idiomáticos sobre APIs JS para adaptar paradigmas (promises → suspend via await).
- Wrappers e tooling
  - Para frameworks como React, criar wrappers tipados ou usar wrappers já existentes (kotlin-wrappers).
  - Gerar typings com dukat/externals quando consumir bibliotecas JS sem bindings.
- Boas práticas
  - Minimizar uso de dynamic em código central; encapsular interop em módulos boundary.
  - Testar em ambientes reais do target (browser/node) e configurar source maps para debugging.
  - Documentar limitações de performance e serialização entre JS/Kotlin.

Exemplos curtos
- @JvmOverloads
  ```
  class Logger {
    @JvmOverloads
    fun log(msg: String, level: Int = 1) { ... }
  }
  // Java pode chamar: new Logger().log("x"); ou log("x", 2);
  ```
- external wrapper (Kotlin/JS)
  ```
  external interface WindowLike { var title: String }
  fun readTitle(win: WindowLike): String = win.title
  ```

Quando consultar este ficheiro
- Ao decidir anotar APIs públicas para consumidores Java.
- Ao escrever bindings para bibliotecas JS ou ao migrar módulos para Kotlin/JS.
- Ao debugar erros de NullPointerException vindos de código Java que interage com Kotlin.

Links úteis
- Kotlin docs: Interoperabilidade com Java (https://kotlinlang.org/docs/java-interop.html)
- Kotlin/JS: https://kotlinlang.org/docs/js-overview.html
- kotlin-wrappers: https://github.com/JetBrains/kotlin-wrappers
