import java.util.ArrayList;
import java.util.List;

/**
 * Nó genérico de uma árvore B/B+
 *
 * @param <K> tipo da chave (deve ser comparável conforme uso na árvore)
 * @param <V> tipo do valor armazenado nas folhas
 */
abstract class BNode<K, V> {

    // Lista de chaves presentes no nó, em ordem crescente
    protected List<K> keys;

    //capacidade máxima de chaves que o nó pode ter
    protected final int order;

    // Ponteiro para o nó pai; null para a raiz
    protected BNode<K, V> parent;

    /**
     * Cria um nó com a ordem informada
     *
     * @param order ordem do nó. Deve ser >= 2.
     * @throws IllegalArgumentException se {@code order < 2}.
     */
    public BNode(int order) {
        if(order < 2){
            throw new IllegalArgumentException("order must be >= 2");
        }
        this.order = order;
        this.keys = new ArrayList<>();
    }

    /**
     * @return número de chaves no nó
     */
    public int getKeyCount(){
        return this.keys.size();
    }

    /**
     * @return true se o número de chaves for igual à ordem, caso contrário false
     */
    public boolean isFull(){
        return this.getKeyCount() == this.order;
    }

    /**
     * @return true se for folha, false se for nó interno
     */
    public abstract boolean isLeaf();

    /**
     * @param key chave a ser buscada
     * @return valor associado à chave, ou null se não encontrado
     */
    public abstract V search(K key);

    /**
     * @param key chave a inserir
     * @param value valor associado à chave
     * @return resultado do split (ou null se não houve split)
     */
    public abstract SplitResult<K, V> insert(K key, V value);

    /**
     * Remove a chave (e seu valor associado se for folha) nesta sub-árvore
     * A implementação deve ajustar a árvore conforme necessário
     * @param key chave a ser removida
     */
    public abstract void delete(K key);

    /**
     * Retorna a primeira chave da folha mais à esquerda que pertence a este nó
     * Usado para atualizar chaves-guia em nós internos após operações.
     * @return a primeira chave encontrada na folha mais à esquerda
     */
    public abstract  K getFirstLeafKey();
}