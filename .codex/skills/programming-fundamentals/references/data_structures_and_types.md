# data_structures_and_types

Tipos Primitivos e Memória (resumo)
- Integers: representação binária com tamanho fixo (8/16/32/64 bits); overflow precisa ser considerado em baixo nível.
- Floats: IEEE 754 (precision e rounding); cuidado com comparações de igualdade.
- Booleans: geralmente 1 bit lógico; implementação física varia por arquitetura.
- Chars: unidade de código (UTF-16/UTF-8 dependendo da linguagem/runtime); atenção a codepoints e grapheme clusters.
- Memória: tipos primitivos ocupam espaço contíguo; alinhamento e padding afetam layout de structs/objetos.

Estruturas de Dados Essenciais
- Array (vetor contíguo)
  - Acesso por índice: O(1)
  - Inserção no fim (amortizada): O(1) amortizado
  - Inserção/remocão no meio: O(n)
  - Uso: buffers, acesso aleatório rápido; escolher quando tamanho conhecido ou crescimento amortizado aceitável.

- Linked List (lista ligada)
  - Inserção no início/fim (com ponteiros): O(1)
  - Acesso por índice: O(n)
  - Uso: quando remoções/insersões frequentes e iteração sequencial são primordiais.

- Stack (pilha)
  - Push/Pop: O(1)
  - Uso: chamadas recursivas, avaliação de expressões, undo stacks.

- Queue (fila) / Deque
  - Enqueue/Dequeue: O(1)
  - Deque: operações nos dois extremos O(1)
  - Uso: filas de trabalho, breadth-first traversal.

- Hash Table / Map
  - Busca/Inserção/Remoção: O(1) média, O(n) pior caso (dependendo da estratégia de colisão)
  - Uso: dicionários, caches; dimensionar capacidade e escolher boa função hash para evitar degradação.

- Tree (árvore binária, AVL, Red-Black)
  - Binary Search Tree (BST) balanceada (AVL/Red-Black)
    - Busca/Inserção/Remoção: O(log n)
    - Uso: índices ordenados, sets/trees; preferir árvores balanceadas para performance estável.
  - Heaps (max/min)
    - Inserção/Remoção do topo: O(log n)
    - Uso: priority queues, seleção de k maiores.

- Graphs (direcionados, não direcionados)
  - Representação: adjacency list (esparso) ou adjacency matrix (denso)
  - Operações:
    - Traversal (DFS/BFS): O(V + E)
    - Busca de caminho mínimo (Dijkstra): O(E + V log V) com heap
  - Uso: redes, dependências, rotas; escolher representação conforme densidade.

Complexidades (Big O) resumo para operações comuns
- Acesso aleatório: Array O(1), LinkedList O(n)
- Inserção:
  - Array (fim amortizado) O(1) amortizado, no meio O(n)
  - LinkedList O(1) (quando posição conhecida)
  - HashTable O(1) média
  - BST balanceada O(log n)
- Busca:
  - HashTable O(1) média
  - BST balanceada O(log n)
  - Linear scan O(n)
- Remoção:
  - HashTable O(1) média
  - BST balanceada O(log n)
  - Array (no meio) O(n)

Notas práticas e trade-offs
- Escolher estrutura segundo operações críticas: se busca frequente por chave → HashTable; se ordenação e intervalos → Tree.
- Prefira representações que minimizem alocações em cenários de alta performance.
- Considerar custo de memória versus ganho de tempo; estruturas cache-friendly (arrays) costumam ser mais rápidas em práticas reais apesar de mesmas notações assintóticas.

Quando consultar este ficheiro
- Ao justificar a escolha de uma estrutura para um requisito de performance ou memória.
- Ao reestruturar código que sofre com latência de operações frequentes.
- Ao desenhar algoritmos que dependem de operações específicas (enqueue, random access, neighbor iteration).

(Fim do documento)
