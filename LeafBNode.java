import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nó folha de uma B+ Tree.
 * @param <K> tipo da chave deve implementar {@link  Comparable}
 * @param <V> tipo do valor armazenado
 */
class LeafBNode<K extends Comparable<K>, V> extends BNode<K, V> {
    // Valores correspondentes às chaves indexadas em keys
    protected List<V> values;

    // Próxima folha à direita ou null se esta for a última
    protected LeafBNode<K, V> next;

    //Folha anterior à esquerda (ou {@code null} se esta for a primeira)
    protected LeafBNode<K, V> previous;

    /**
     * Cria uma folha vazia com a ordem informada.
     *
     * @param order capacidade da árvore/nó
     */
    public LeafBNode(int order) {
        super(order);
        this.values = new ArrayList<>();
        this.next = null;
        this.previous = null;
    }

    /**
     * Indica que este nó é uma folha.
     *
     * @return true
     */
    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * Busca pelo valor associado à chave nesta folha.
     *
     * @param key chave a buscar
     * @return valor associado, ou null se não encontrado
     */
    @Override
    public V search(K key){
        int index = Collections.binarySearch(this.keys, key);
        if (index >= 0) {
            return this.values.get(index);
        }
        return null;
    }

    /**
     * Insere a chave e o valor nesta folha. Se a chave já existir, substitui o valor.
     *
     * @param key chave a inserir
     * @param value valor associado
     * @return SplitResult caso a folha precise ser dividida, ou null se não houver split
     */
    @Override
    public SplitResult<K, V> insert(K key, V value){
        int index = Collections.binarySearch(this.keys, key);
        int insertionPoint;
        if (index >= 0) {
            this.values.set(index, value);
            return null;
        } else {
            insertionPoint = -(index + 1);
        }
        this.keys.add(insertionPoint, key);
        this.values.add(insertionPoint, value);
        
        if (this.isFull()) {
            return this.split();
        }
        return null;
    }

    /**
     * Divide esta folha criando um irmão à direita e movendo metade
     * das chaves/valores para o novo nó. Atualiza os ponteiros next
     * e {@code previous} entre folhas.
     *
     * @return SplitResult com a chave promovida (primeira chave do irmão)
     * e a referência para o nó-irmão
     */
    private SplitResult<K, V> split(){
        System.out.println("### SPLIT DE NÓ FOLHA ###");
        LeafBNode<K, V> sibling = new LeafBNode<>(this.order);
        int midIndex = this.getKeyCount() / 2;
        
        List<K> siblingKeys = new ArrayList<>(this.keys.subList(midIndex, this.getKeyCount()));
        List<V> siblingValues = new ArrayList<>(this.values.subList(midIndex, this.getKeyCount()));
        sibling.keys.addAll(siblingKeys);
        sibling.values.addAll(siblingValues);
        
        this.keys.subList(midIndex, this.getKeyCount()).clear();
        this.values.subList(midIndex, this.getKeyCount()).clear();
        
        sibling.next = this.next;
        if (this.next != null) {
            this.next.previous = sibling;
        }
        this.next = sibling;
        sibling.previous = this;
        
        K promotedKey = sibling.getFirstLeafKey();
        return new SplitResult<>(promotedKey, sibling);
    }
    
    /**
     * Retorna a primeira chave armazenada nesta folha.
     *
     * @return primeira chave ou null se a folha estiver vazia
     */
    @Override
    public K getFirstLeafKey(){
        if(keys.isEmpty()){
            return null;
        }
        return keys.get(0);
    }

    /**
     * Remove a chave desta folha, ajustando o pai em caso de underflow.
     *
     * @param key chave a ser removida
     */
    @Override
    public void delete(K key) {
        // 1. Encontra e remove a chave
        int index = Collections.binarySearch(this.keys, key);
        if (index < 0) {
            return;
        }
        this.keys.remove(index);
        this.values.remove(index);

        // 2. Verifica se ocorreu "underflow"
        int minKeys = (int) Math.ceil((this.order - 1) / 2.0);
        
        if (this.getKeyCount() < minKeys && this.parent != null) {
            // Pede ao pai para consertar
            ((InternalBNode<K, V>) this.parent).handleChildUnderflow(this);
        }
    }

    /**
     * Redistribui uma chave/valor a partir do irmão e retorna a nova chave
     * separadora que deve ser atualizada no pai.
     *
     * @param sibling folha-irmã que cede uma chave
     * @param isLeftSibling true se sibling for o irmão esquerdo
     * @return a nova chave separadora a ser armazenada no pai
     */
    K redistributeFrom(LeafBNode<K, V> sibling, boolean isLeftSibling) {
    if (isLeftSibling) {
        // Armazena o índice da *última* chave/valor
        int lastIndex = sibling.getKeyCount() - 1;

        // Remove a chave e o valor USANDO O MESMO ÍNDICE
        K key = sibling.keys.remove(lastIndex);
        V value = sibling.values.remove(lastIndex);

        // Adiciona no *início* do nó atual
        this.keys.add(0, key);
        this.values.add(0, value);

        // Retorna a nova primeira chave do *nó atual*
        return this.keys.get(0);
    } else {
        K key = sibling.keys.remove(0);
        V value = sibling.values.remove(0);
        this.keys.add(key);
        this.values.add(value);
        return sibling.keys.get(0);
    }
}

    /**
     * Faz o merge entre esta folha e a folha-irmã.
     *
     * @param sibling folha-irmã a ser fundida
     * @param isLeftSibling true se sibling estiver à esquerda
     */
    void mergeWith(LeafBNode<K, V> sibling, boolean isLeftSibling) {
    System.out.println("### FUSÃO DE NÓ FOLHA ###");
    if (isLeftSibling) {
        // O irmão está à esquerda.
        // Estratégia: Mover tudo de 'this' para 'sibling'.
        sibling.keys.addAll(this.keys);
        sibling.values.addAll(this.values);

        // Atualiza a lista encadeada de folhas
        sibling.next = this.next;
        if (this.next != null) {
            this.next.previous = sibling;
        }
    } else {
        // O irmão está à direita.
        // Estratégia: Mover tudo de 'sibling' para 'this'.
        this.keys.addAll(sibling.keys);
        this.values.addAll(sibling.values);

        // Atualiza a lista encadeada de folhas
        this.next = sibling.next;
        if (sibling.next != null) {
            sibling.next.previous = this;
        }
    }
}
}