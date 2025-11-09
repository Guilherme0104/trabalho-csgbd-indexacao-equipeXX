import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementação genérica de uma B+ Tree.
 * @param <K> tipo da chave deve implementar {@link  Comparable}
 * @param <V> tipo do valor armazenado nas folhas
 */
public class BPlusTree<K extends Comparable<K>, V> {
    //Raiz da árvore (pode ser um nó interno ou uma folha)
    private BNode<K,V> root;

    //Ordem da árvore (capacidade máxima de chaves por nó)
    private final int order;

    /**
     * Cria uma B+ Tree com a ordem especificada
     * @param order ordem da árvore (recomenda-se >= 2).
     * A validação efetiva é delegada aos nós ao criar instâncias.
     */
    public BPlusTree(int order) {
        this.order = order;
        this.root = new LeafBNode<>(order);
    }

    /**
     * Insere um par (chave, valor) na árvore.
     * @param key chave a inserir (não pode ser {@code null})
     * @param value valor associado à chave
     * @throws IllegalArgumentException se key for null
     *
     * delega a inserção à raiz. Se a raiz se splitar,
     * cria-se um novo nó interno como nova raiz contendo a chave promovida
     * e os dois filhos resultantes.
     */
    public void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Chave não pode ser nula.");
        }
        
        SplitResult<K, V> splitResult = this.root.insert(key, value);

        if (splitResult == null) {
            return;
        }

        System.out.println("### AUMENTANDO ALTURA DA ÁRVORE (RAIZ DIVIDIU) ###");
        InternalBNode<K, V> newRoot = new InternalBNode<>(this.order);
        
        K promotedKey = splitResult.promotedKey();
        BNode<K, V> newSibling = splitResult.newSiblingNode();

        newRoot.keys.add(promotedKey);
        newRoot.children.add(this.root);
        newRoot.children.add(newSibling);
        
        this.root.parent = newRoot;
        newSibling.parent = newRoot;

        this.root = newRoot;
    }
    
    /**
     * @param key chave a ser buscada (se {@code null}, retorna {@code null})
     * @return o valor associado à chave, ou {@code null} se não existir
     */
    public V search(K key) {
        if (key == null){
            return null;
        }
        return this.root.search(key);
    }

    /**
     * @param key chave a remover (não pode ser {@code null})
     * @throws IllegalArgumentException se {@code key} for {@code null}
     *
     * delega remoção à raiz. Se após a remoção a raiz ficar
     * sem chaves e não for folha, a altura da árvore diminui promovendo o
     * primeiro filho como nova raiz.
     */
    public void delete(K key) {
        if (key == null){
            throw new IllegalArgumentException("Chave não pode ser nula.");
        }

        this.root.delete(key);

        if (!this.root.isLeaf() && this.root.getKeyCount() == 0) {
            System.out.println("### DIMINUINDO ALTURA DA ÁRVORE (RAIZ ENCOLHEU) ###");
            BNode<K, V> oldRoot = this.root;
            this.root = ((InternalBNode<K, V>) oldRoot).children.get(0);
            this.root.parent = null;
        }
    }
    
    /**
     * @param key chave usada para descer pela árvore (se null, retorna null)
     * @return a folha onde a chave deve estar (ou null se key for null)
     *
     * percorre a árvore a partir da raiz, usando
     * binarySearch(List, Object)} nas chaves dos nós internos 
     * para determinar o índice do filho a seguir.
     */
    private LeafBNode<K, V> findLeafNode(K key) {
        if (key == null) { return null; }
        BNode<K, V> currentNode = this.root;
        while (!currentNode.isLeaf()) {
            InternalBNode<K, V> internalNode = (InternalBNode<K, V>) currentNode;
            int index = Collections.binarySearch(internalNode.keys, key);
            int childIndex;
            if (index >= 0) {
                childIndex = index + 1;
            } else {
                childIndex = -(index + 1);
            }
            currentNode = internalNode.children.get(childIndex);
        }
        return (LeafBNode<K, V>) currentNode;
    }

    /**
     * Busca valores no intervalo fechado
     * @param startKey limite inferior do intervalo (inclusive)
     * @param endKey limite superior do intervalo (inclusive)
     * @return lista de valores cujas chaves estão no intervalo; lista vazia
     * se parâmetros inválidos ou nenhum resultado
     *
     * Implementação: encontra a folha inicial por findLeafNode(K) e
     * percorre as folhas encadeadas via next, coletando valores enquanto
     * as chaves estiverem dentro do intervalo.
     */
    public List<V> searchRange(K startKey, K endKey){
        List<V> result = new ArrayList<>();
        if (startKey == null || endKey == null || startKey.compareTo(endKey) > 0) {
            return result;
        }
        LeafBNode<K, V> currentNode = findLeafNode(startKey);
        boolean done = false;
        while (currentNode != null && !done) {
            for (int i = 0; i < currentNode.getKeyCount(); i++) {
                K currentKey = currentNode.keys.get(i);
                if (currentKey.compareTo(startKey) >= 0 && currentKey.compareTo(endKey) <= 0) {
                    result.add(currentNode.values.get(i));
                }
                if (currentKey.compareTo(endKey) > 0) {
                    done = true;
                    break;
                }
            }
            currentNode = currentNode.next;
        }
        return result;
    }
}