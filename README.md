Implementação de Estruturas de Indexação: Hash Extensível e Árvore B+

Disciplina: Construção de Sistemas de Gerência de Bancos de Dados
Professora: Lívia Almada

Equipe
[Luis Guilherme Xavier da Silva]
[Kauan Nepomuceno Fontenele]

Objetivo

Este trabalho consiste na implementação, utilizando a linguagem Java (versão 17+), de duas estruturas de dados fundamentais para a indexação em Sistemas Gerenciadores de Bancos de Dados (SGBDs): Hash Extensível e Árvore B+.

O foco principal é a compreensão prática do funcionamento interno destas estruturas, incluindo as operações de inserção, busca e remoção, e o gerenciamento algorítmico de divisão (split) e fusão (merge) de blocos de dados (páginas/buckets).

Estrutura do Projeto

O código-fonte foi modularizado em pacotes (diretórios) para uma clara separação de responsabilidades:

* `/hash`: Contém a implementação do Hash Extensível (`ExtendibleHash.java`, `Bucket.java`).
* `/bplus`: Contém a implementação da Árvore B+ (`BPlusTree.java`, `BNode.java`, `LeafBNode.java`, `InternalBNode.java`, `SplitResult.java`).
* `/` (Diretório Raiz): Contém os *drivers* de teste interativos `Main.java` (para o Hash Extensível) e `MainBPlus.java` (para a Árvore B+).

---

Compilação e Execução

O projeto foi desenvolvido em Java 17.

Compilação

Para compilar todos os ficheiros-fonte, execute o seguinte comando no terminal, a partir do diretório raiz do projeto:

```bash
javac *.java hash/*.java bplus/*.java
