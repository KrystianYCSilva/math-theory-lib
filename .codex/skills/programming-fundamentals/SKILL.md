---
name: programming-fundamentals
description: |
  Estabelecer diretrizes e paradigmas universais de engenharia de software para raciocínio, análise e solução de problemas computacionais. Use when: o agente precisa explicar, projetar, refatorar ou justificar decisões arquiteturais e algorítmicas agnósticas de linguagem.
---

# programming-fundamentals

Overview
Programação é a atividade de modelar e automatizar soluções para problemas computacionais usando abstrações, estruturas de dados e algoritmos. Esta skill concentra princípios universais, heurísticas de projeto e roteamento de referência para decidir quando aprofundar em estruturas de dados ou algoritmos específicos.

Paradigmas de Programação
- Imperativo: expressa passo a passo como atingir um estado desejado; foco em controle de fluxo e estado mutável. Use para operações de baixo nível e quando o controle explícito do estado for necessário.
- Declarativo: descreve o que se quer em vez de como obter; inclui SQL, regras e programação reativa. Use onde a expressão do objetivo é mais robusta que o gerenciamento do estado.
- Orientado a Objetos (OO): modela o domínio com objetos que encapsulam estado e comportamento; favorece encapsulamento, abstração e polimorfismo. Use para modelagem de domínios ricos e extensão por composição.
- Funcional: privilegia funções puras, imutabilidade e composição; favorece raciocínio matemático, concorrência segura e facilitação de testes. Use para transformações de dados e pipelines previsíveis.

Boas Práticas e Clean Code
- SOLID (resumo prático)
  - Single Responsibility: cada módulo/função tem uma única razão para mudar.
  - Open/Closed: estender sem modificar o comportamento existente.
  - Liskov Substitution: subclasses substituem superclasses sem alterar o contrato.
  - Interface Segregation: interfaces específicas ao uso.
  - Dependency Inversion: depender de abstrações, não de concretos.
- DRY: consolidar regras e evitar duplicação por abstrações claras.
- KISS: preferir soluções simples e legíveis em primeiro lugar.
- YAGNI: implementar apenas o que é necessário hoje; evitar complexidade antecipada.
- Documentação mínima e contratos explícitos: preconditions/postconditions, invariantes e casos de erro.
- Testabilidade: projetar código com dependências injetáveis e limites bem definidos; preferir testes unitários rápidos e determinísticos.

Principais Erros (Pitfalls) e Como Corrigi-los
- Mutabilidade global: resolver isolando estado, usar escopos, ou transformar em passagem explícita de estado.
- Acoplamento forte: introduzir abstrações/portas e aplicar injeção de dependências.
- Falta de tratamento de erros: definir política de erros (exceções, códigos, resultados) e normalizar por camadas.
- Falta de invariantes e contratos: especificar pré-condições e validar cedo (fail-fast).
- Algoritmos ineficientes por negligência de complexidade: medir, escolher estrutura de dados adequada e documentar trade-offs.
- Tests frágeis: usar testes baseados em comportamentos e doubles controlados; evitar dependências externas não determinísticas em unit tests.

Reference / Guidelines
- Use este ficheiro para diretrizes de alto nível, avaliações arquiteturais e explicações pedagógicas.
- Carregar references/data_structures_and_types.md quando precisar escolher ou justificar uma estrutura de dados específica para requisitos de performance, memória ou mutabilidade.
- Carregar references/algorithms.md quando a decisão envolver complexidade temporal/espacial, algoritmos de ordenação, busca ou estratégias de otimização.
- Roteiro prático: aplicar primeiro princípios de design (SOLID, KISS), depois escolher estruturas de dados; só então otimizar algoritmos com base em perfis reais.

How to aplicar paradigmas de forma prática
1. Avaliar requisitos: mutabilidade, concorrência, latência, previsibilidade.
2. Mapear domínio para paradigma adequado: pipelines de dados → funcional; UIs ricas → OO/comp.; transformação de estado simples → imperativo controlado.
3. Misturar paradigmas conscientemente: isolar efeitos colaterais e usar funções puras para lógica determinística.

How to aplicar princípios de clean code em revisões
1. Verificar responsabilidades de classes/módulos e extrair quando houver multiplicidade de razões para mudança.
2. Identificar duplicação e refatorar com abstrações que preservem legibilidade.
3. Confirmar contratos e adicionar testes de propriedade/invariante quando aplicável.

How to diagnosticar e corrigir pitfalls comuns
1. Reproduzir cenário mínimo que evidencia o problema (reduzir escopo).
2. Medir (profiling/timers/allocations) antes de otimizar.
3. Aplicar correção: isolar estado, introduzir abstração, adicionar validação, e escrever testes que evitem regressões.

How to decidir entre performance, simplicidade e manutenção
1. Priorizar correções que removam bugs e melhorem legibilidade.
2. Otimizar com dados (perf tests) apenas onde o custo é material.
3. Documentar trade-offs e considerar custo de manutenção ao propor micro-otimizações.

How to usar as referências
1. Ler data_structures_and_types.md quando a escolha de DS for central ao requisito (e.g., latência, throughput, memória).
2. Ler algorithms.md quando a solução exige análise de complexidade ou uso de algoritmos clássicos.
3. Evitar leitura desnecessária: consultar referências somente quando a decisão envolver trade-offs técnicos ou justificar escolhas para revisão/PR.

(Fim do SKILL.md)
