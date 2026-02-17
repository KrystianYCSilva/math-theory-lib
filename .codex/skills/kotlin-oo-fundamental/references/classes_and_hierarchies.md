# classes_and_hierarchies

Overview
Practical descriptions of Kotlin OO constructs and modeling patterns.

Classes and Interfaces
- Class: encapsula estado e comportamento; use encapsulamento para invariantes.
- Interface: permite implementações padrão; projetar para comportamento.
- Abstract class: quando compartilhar estado entre implementações relacionadas.

Data Classes
- Use para value-objects/DTOs; manter sem lógica de negócio pesada; preferir val nos parâmetros primários; usar copy() para derivar novos valores.

Sealed Classes / Sealed Interfaces
- Modelar hierarquias fechadas para estados e resultados; when exaustivo sem else.

Enum Classes
- Para valores enumerados; prefira sealed quando variantes carregam dados distintos.

Interface Delegation (`by`)
- Delegar implementação de uma interface a um objeto existente; reduz boilerplate e facilita testes.
- Exemplo:
  ```
  interface Logger { fun log(msg: String) }
  class ConsoleLogger: Logger { override fun log(msg: String) = println(msg) }
  class Service(logger: Logger) : Logger by logger
  ```

Versioning and evolution
- Evitar expor hierarquias internas; usar facades e interfaces estáveis; documentar contratos de extensão.
