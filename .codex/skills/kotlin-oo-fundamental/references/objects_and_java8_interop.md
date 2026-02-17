# objects_and_java8_interop

Object Declarations
- object X => singleton; object expressions => instâncias anônimas; companion objects => membros ligados à classe.

Initialization and lifecycle
- Use `val ... by lazy {}` para inicialização sob demanda com thread-safety; `lateinit var` para inicialização tardia quando necessário; preferir val quando possível.

@Jvm* interoperability patterns
- @JvmStatic: expõe métodos de companion como estáticos para Java.
- @JvmField: expõe propriedades como campos públicos para Java.
- @JvmOverloads: gera overloads Java para funções com parâmetros default.
- @file:JvmName("Name") para controlar o nome da classe gerada para funções top-level.

Practical guidelines
- Expor List<T> não MutableList<T>; documentar nullability e contratos.
- Fornecer overloads e fábricas para ergonomia Java 8; converter exceptions em tipos funcionais quando apropriado.
