# EXPANSION_EXECUTION_PLAN.md — `mathsets-kt`

**Versão:** 1.0.0-draft  
**Data:** 2026-02-17  
**Base documental:** `docs/expanding-mathsets-kt.md` + `docs/EXPANSION_ROADMAP.md`  
**Escopo deste documento:** planejamento de execução (sem implementação)

---

## 1. Objetivo

Transformar a expansão v2.0 em um plano de execução pragmático, com ordem de dependências, critérios mínimos de aceite e checkpoints para evitar retrabalho arquitetural.

---

## 2. Estado Atual do Repositório

### 2.1 Fundação disponível (v1.0)

- `kernel`
- `logic`
- `set`
- `relation`
- `function`
- `construction`
- `ordinal`
- `cardinal`
- `descriptive`
- `combinatorics`
- `forcing`
- `examples`

### 2.2 Módulos da expansão ainda ausentes

- `algebra`
- `polynomial`
- `analysis`
- `linalg`
- `ntheory`
- `graph`
- `topology`
- `measure`
- `diffgeo`
- `category`
- `typetheory`
- `computability`
- `modeltheory`
- `solver`
- `proof`
- `verified`
- `symbolic`
- `ode`
- `explorer`

### 2.3 Exceções parciais

- Há evolução prévia em `construction/real` e `construction/complex`, mas ainda sem a malha completa prevista para Fase B (Cauchy + Dedekind + integração plena com `analysis`).

---

## 3. Estratégia por Ondas (Execution Waves)

## Wave 1 — Fundamento de Expansão (Fases A + E)

**Foco:** habilitar o restante do roadmap via base algébrica + meta-fundações.  
**Módulos-alvo:** `algebra`, `polynomial`, `category`, `typetheory`, `computability`, `modeltheory`.

**Entregas mínimas**

- Hierarquia algébrica principal: Magma -> Group e Semiring -> Field.
- Instâncias-chave: `IntegerRing`, `RationalField`.
- Núcleo de `Polynomial` (uni-variado) com operações básicas e `gcd`.
- Núcleo de categorias finitas (`Category`, `Functor`, `NatTrans`).
- Type checker didático mínimo (MLTT subset).
- Simulador mínimo de computabilidade (Turing ou lambda calculo).
- Estruturas de teoria dos modelos (`Signature`, `Structure`, `Satisfaction`) para universos finitos.

**Critérios de aceite (gate)**

- Leis algébricas com property-based tests.
- Teoremas-base de homomorfismos/kernel no nível de testes.
- Testes de consistência para funtores e satisfação de fórmulas em modelos finitos.

**Riscos principais**

- Design inconsistente de interfaces algébricas.
- Crescimento precoce de escopo em category/type theory.

---

## Wave 2 — Torre Numérica e Análise Base (Fase B)

**Foco:** completar `R` e `C` em construction e abrir trilha formal de análise.  
**Módulos-alvo:** `construction/real`, `construction/complex`, `analysis`.

**Entregas mínimas**

- `CauchyReal` com módulo de convergência explícito.
- `DedekindReal` e isomorfismo `Cauchy <-> Dedekind`.
- Embedding `Q -> R` e `R -> C`.
- Estruturas de análise: sequência, limite, continuidade, derivada, integral de Riemann (nível base).

**Critérios de aceite (gate)**

- Roundtrip de isomorfismo Cauchy/Dedekind.
- Propriedades numéricas mínimas (ex.: `sqrt(2)^2` aproximando 2 com precisão parametrizada).
- Testes de limites e quociente diferencial com tolerâncias explícitas.

**Riscos principais**

- Divergência entre via construtiva e via kernel.
- Custo de precisão e performance em aproximações.

---

## Wave 3 — Matemática Aplicada Estrutural (Fases C + D)

**Foco:** ampliar capacidade matemática operacional após base estável.  
**Módulos-alvo:** `linalg`, `ntheory`, `graph`, `topology`, `measure`, `diffgeo`.

**Entregas mínimas**

- `linalg`: matrizes, sistemas lineares e decomposições centrais.
- `ntheory`: modular arithmetic, primalidade e fatoração base.
- `graph`: estrutura de grafo + algoritmos clássicos.
- `topology`/`measure`: noções fundamentais computáveis.
- `diffgeo`: estrutura introdutória (variedades/cartas e operações básicas).

**Critérios de aceite (gate)**

- Testes algébricos e de corretude algorítmica por domínio.
- Benchmarks básicos comparando baseline interno entre versões.
- Integração sem violar dependência estrita entre camadas.

**Riscos principais**

- Escopo excessivo por diversidade de domínios.
- Dependência implícita de componentes ainda não maduros.

---

## Wave 4 — Verificação, Simbólico e Síntese de Release (Fases F + G)

**Foco:** elevar garantias formais e concluir plataforma v2.0.  
**Módulos-alvo:** `solver`, `proof`, `verified`, `symbolic`, `ode`, `explorer`.

**Entregas mínimas**

- Ponte `solver` (Z3/SAT) com casos de prova reproduzíveis.
- `proof` com objetos de prova e checker mínimo funcional.
- `verified` com pipeline inicial (Isabelle/Lean bridge incremental).
- Motor simbólico base (AST + simplificação + derivação simbólica).
- Solvers de EDO (numérico base + casos simbólicos simples).
- REPL/dashboard de status (mínimo viável).

**Critérios de aceite (gate)**

- Conjunto mínimo de teoremas verificados fim-a-fim.
- Roundtrip e rastreabilidade entre prova, teorema e módulo.
- Release candidate com documentação e exemplos executáveis.

**Riscos principais**

- Complexidade de interop externa (JNI/FFI/toolchains).
- Inconsistência entre semântica simbólica e numérica.

---

## 4. Ordem Recomendada de Execução

1. Wave 1 (A + E)  
2. Wave 2 (B)  
3. Wave 3 (C + D)  
4. Wave 4 (F + G)

> Nota: embora o roadmap original tenha paralelização entre fases, para equipe pequena a ordem acima minimiza risco de bloqueio e retrabalho.

---

## 5. Definition of Done (Global)

Cada módulo novo só é considerado concluído quando:

- possui API estável mínima;
- possui testes unitários + property-based para leis relevantes;
- compila em JVM e JS no pipeline local do projeto;
- inclui exemplos em `examples/` quando aplicável;
- respeita arquitetura em camadas e `Dual Mode` (kernel eficiente + construção rigorosa quando aplicável).

---

## 6. Backlog de Kickoff (Próxima Etapa)

1. Criar scaffolding de módulos da Wave 1 (`algebra`, `polynomial`, `category`, `typetheory`, `computability`, `modeltheory`).
2. Definir contratos mínimos de `algebra` (interfaces + leis).
3. Subir suíte de testes de leis algébricas como gate inicial.
4. Fechar baseline de design review antes de avançar para Wave 2.

---

## 7. Fora de Escopo deste Plano

- Implementação de código desta expansão.
- Ajustes de performance fina por bridge externa antes dos gates de corretude.
- Entregas de UI avançada além do mínimo de inspeção (REPL/dashboard básico).

