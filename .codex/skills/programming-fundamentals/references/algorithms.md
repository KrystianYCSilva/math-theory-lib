# algorithms

Complexidade Ciclomática e Notação Big O
- Complexidade Ciclomática
  - Mede o número de caminhos lineares independentes em uma função (aproximação do número de ramos/condicionais).
  - Uso: identificar funções complexas que precisam ser refatoradas; valores mais altos indicam maior risco de bugs e necessidade de decomposição.
- Notação Big O (Time & Space)
  - Time Complexity: crescimento do tempo de execução em função do input n.
  - Space Complexity: uso de memória adicional em função do input n.
  - Prática: comparar algoritmos pela ordem assintótica e considerar constantes e comportamento em inputs reais.

Algoritmos de Ordenação (resumo prático)
- QuickSort
  - Estratégia: divide and conquer; particiona e recursa.
  - Complexidade: O(n log n) média, O(n^2) pior caso (pivot ruim); espaço O(log n) média (recursão).
  - Uso: geralmente rápido na prática; usar randomização ou pivot mediano para evitar pior caso.
- MergeSort
  - Estratégia: divide and conquer; merge estável.
  - Complexidade: O(n log n) tempo consistente, espaço O(n) auxiliar.
  - Uso: quando estabilidade ou previsibilidade de performance é necessária; bom para listas ligadas com adaptações.
- BubbleSort / InsertionSort (simples)
  - Complexidade: O(n^2) tempo; InsertionSort é eficiente para arrays quase ordenados.
  - Uso: educacional ou pequenas coleções; InsertionSort para small n em hybrid sorts.

Busca
- Binary Search
  - Pré-requisito: dados ordenados.
  - Complexidade: O(log n) tempo.
  - Uso: localizar valores com custo logarítmico; cuidado com overflow em cálculos de meio (usar low + (high - low)/2).
- Depth-First Search (DFS)
  - Percorre recursivamente ou com pilha; exploração profunda.
  - Complexidade: O(V + E)
  - Uso: componentes conectados, topological sort (em DAG), detecção de ciclos.
- Breadth-First Search (BFS)
  - Percorre em camadas usando fila.
  - Complexidade: O(V + E)
  - Uso: menor número de arestas entre nós (unweighted shortest path).

Abordagens de Resolução de Problemas
- Recursividade vs Iteratividade
  - Recursividade: modela soluções naturais de divide and conquer; atenção a profundidade de pilha e overhead.
  - Iteratividade: evita overhead de chamadas; preferir quando a recursão não agrega clareza ou quando limitação de stack é crítica.
  - Transformações: tail recursion → otimização; converter recursão para iteração usando stack explícito quando necessário.
- Divide and Conquer
  - Dividir o problema em subproblemas independentes, resolver e combinar resultados.
  - Uso: Quicksort, Mergesort, FFT; parallelizável quando subproblemas são independentes.
- Dynamic Programming (DP) e Memoization
  - Estratégia: evitar recomputação armazenando resultados de subproblemas.
  - Tipos:
    - Top-down (recursão + memo)
    - Bottom-up (tabulação)
  - Prática: identificar subproblemas sobrepostos e propriedade de optimal substructure.
  - Exemplo curto (pseudo):
    - Fibonacci com memo:
      - f(n): if n <= 1 return n; if memo[n] defined return memo[n]; memo[n] = f(n-1)+f(n-2); return memo[n].

Heurísticas e Estratégias práticas
- Perfilar antes de otimizar: medir hotspots reais.
- Preferir algoritmos simples e testáveis; otimizar apenas quando necessário.
- Para grandes volumes de dados, considerar streaming, chunking e algoritmos sublinear quando possível (ex.: amostragem, sketches).
- Documentar trade-offs entre tempo e espaço e justificar escolhas em PRs/design docs.

Quando consultar este ficheiro
- Ao explicar ou justificar a complexidade de uma solução.
- Ao escolher algoritmo de ordenação/busca para casos com restrições de memória, estabilidade ou pior caso.
- Ao projetar soluções que requeiram DP, dividir e conquistar, ou transformação recursiva para iterativa.

(Fim do documento)
