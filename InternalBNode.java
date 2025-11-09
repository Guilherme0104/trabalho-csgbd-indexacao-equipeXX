import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nó interno de uma B+ Tree.
 * 
 * @param <K> tipo da chave deve implementar {@link  Comparable}
 * @param <V> tipo do valor armazenado nas folhas
 */
class InternalBNode<K extends Comparable<K>, V> extends BNode<K, V> {

    // Lista de ponteiros para os filhos deste nó (tamanho = keys.size() + 1)
    protected List<BNode<K, V>> children;

    /**
     * inicializa um nó interno vazio com a ordem informada.
     *
     * @param order capacidade da árvore/nó
     */
    public InternalBNode(int order) {
        super(order);
        this.children = new ArrayList<>();
    }

    /**
     * Nós internos não são folhas.
     *
     * @return false
     */
    @Override
    public boolean isLeaf() {
        return false;
    }

    /**
     * determina o filho adequado e delega a busca.
     *
     * @param key chave a buscar
     * @return o valor encontrado pelo filho ou null se não existir
     */
    @Override
    public V search(K key) {
        int i = 0;
        while (i < this.keys.size() && key.compareTo(this.keys.get(i)) >= 0) {
            i++;
        }
        return this.children.get(i).search(key);
    }

    /**
     * Insere um par chave-valor descendo ao filho correto. Se o filho se splitar,
     * insere a chave promovida e o novo filho neste nó interno.
     *
     * @param key chave a inserir
     * @param value valor associado
     * @return SplitResult caso este nó precise também se splitar,
     * ou null se não houver split adicional
     */
    @Override
    public SplitResult<K, V> insert(K key, V value){
        int index = Collections.binarySearch(this.keys, key);
        int childIndex;
        if (index >= 0) {
            childIndex = index + 1;
        } else {
            childIndex = -(index + 1);
        }
        
        SplitResult<K, V> splitResult = this.children.get(childIndex).insert(key, value);

        if (splitResult == null) {
            return null;
        }

        K promotedKey = splitResult.promotedKey();
        BNode<K, V> newSibling = splitResult.newSiblingNode();

        int insertionPoint = Collections.binarySearch(this.keys, promotedKey);
        if (insertionPoint < 0) {
            insertionPoint = -(insertionPoint + 1);
        }
        this.keys.add(insertionPoint, promotedKey);
        this.children.add(insertionPoint + 1, newSibling);
        newSibling.parent = this;
        
        if (this.isFull()) {
            return this.split();
        }
        return null;
    }
    
    /**
     * Divide este nó interno quando excede a capacidade.
     *
     * @return SplitResult contendo a chave promovida e o novo nó-irmão
     */
    private SplitResult<K, V> split() {
        System.out.println("### SPLIT DE NÓ INTERNO ###");
        int midIndex = this.getKeyCount() / 2;
        K promotedKey = this.keys.get(midIndex);
        
        InternalBNode<K, V> sibling = new InternalBNode<>(this.order);
        
        List<K> siblingKeys = new ArrayList<>(this.keys.subList(midIndex + 1, this.getKeyCount()));
        List<BNode<K, V>> siblingChildren = new ArrayList<>(this.children.subList(midIndex + 1, this.children.size()));
        
        sibling.keys.addAll(siblingKeys);
        sibling.children.addAll(siblingChildren);
        
        for(BNode<K, V> child : siblingChildren) {
            child.parent = sibling;
        }
        
        this.keys.subList(midIndex, this.getKeyCount()).clear();
        this.children.subList(midIndex + 1, this.children.size()).clear();
        
        return new SplitResult<>(promotedKey, sibling);
    }

    /**
     * Retorna a primeira chave presente na folha mais à esquerda sob este nó.
     *
     * @return a primeira chave da folha mais à esquerda, ou null se não houver filhos
     */
    @Override
    public K getFirstLeafKey(){
        if (children.isEmpty()){
            return null;
        }
        return children.get(0).getFirstLeafKey();
    }

    /**
     * Remove a chave delegando ao filho apropriado.
     *
     * @param key chave a ser removida
     */
    @Override
    public void delete(K key) {
        int index = Collections.binarySearch(this.keys, key);
        int childIndex;
        if (index >= 0) {
            childIndex = index + 1;
        } else {
            childIndex = -(index + 1);
        }
        this.children.get(childIndex).delete(key);
    }

    /**
     * Trata o underflow (filho com poucas chaves) proveniente de um filho.
     *
     * @param childInUnderflow filho que entrou em underflow
     */
    void handleChildUnderflow(BNode<K, V> childInUnderflow) {
        int childIndex = this.children.indexOf(childInUnderflow);
        
        if (childIndex > 0 && tryRedistribute(childInUnderflow, this.children.get(childIndex - 1), childIndex, true)) {
            return; 
        }
        
        if (childIndex < this.children.size() - 1 && tryRedistribute(childInUnderflow, this.children.get(childIndex + 1), childIndex, false)) {
            return; 
        }

        if (childIndex > 0) {
            mergeChildren(this.children.get(childIndex - 1), childInUnderflow, childIndex - 1);
        } else {
            mergeChildren(childInUnderflow, this.children.get(childIndex + 1), childIndex);
        }

        int minKeys = (int) Math.ceil((this.order - 1) / 2.0) - 1;
        if (this.getKeyCount() < minKeys && this.parent != null) {
            ((InternalBNode<K, V>) this.parent).handleChildUnderflow(this);
        }
    }

    /**
     * Tenta redistribuir chaves entre um filho em underflow e um irmão.
     *
     * @param child filho em underflow
     * @param sibling irmão candidato para fornecer uma chave
     * @param childIndex índice do filho no vetor de children
     * @param isLeftSibling true se sibling for o irmão esquerdo
     * @return true se a redistribuição foi feita, false caso contrário
     */
    private boolean tryRedistribute(BNode<K, V> child, BNode<K, V> sibling, int childIndex, boolean isLeftSibling) {
        int minKeys = (int) Math.ceil((this.order - 1) / 2.0) - 1;
        
        if (sibling.getKeyCount() > minKeys) {
            if (child.isLeaf()) {
                K newSeparatorKey = ((LeafBNode<K, V>) child).redistributeFrom((LeafBNode<K, V>) sibling, isLeftSibling);
                this.keys.set(isLeftSibling ? childIndex - 1 : childIndex, newSeparatorKey);
            } else {
                redistributeInternal((InternalBNode<K, V>) child, (InternalBNode<K, V>) sibling, isLeftSibling, childIndex);
            }
            return true;
        }
        return false;
    }

    /**
     * Realiza redistribuição entre dois nós internos
     *
     * @param child nó que está em underflow e receberá uma chave/filho
     * @param sibling nó que cederá a última/primeira chave e filho
     * @param isLeftSibling indica se sibling é o irmão esquerdo
     * @param childIndex índice do filho no pai
     */
    private void redistributeInternal(InternalBNode<K, V> child, InternalBNode<K, V> sibling, boolean isLeftSibling, int childIndex) {
        System.out.println("### REDISTRIBUIÇÃO DE NÓ INTERNO ###");
        if (isLeftSibling) {
            K separator = this.keys.remove(childIndex - 1);
            child.keys.add(0, separator);
            K promotedKey = sibling.keys.remove(sibling.getKeyCount() - 1);
            BNode<K, V> movedChild = sibling.children.remove(sibling.children.size() - 1);
            child.children.add(0, movedChild);
            movedChild.parent = child;
            this.keys.add(childIndex - 1, promotedKey);
        } else { 
            K separator = this.keys.remove(childIndex);
            child.keys.add(separator);
            K promotedKey = sibling.keys.remove(0);
            BNode<K, V> movedChild = sibling.children.remove(0);
            child.children.add(movedChild);
            movedChild.parent = child;
            this.keys.add(childIndex, promotedKey);
        }
    }

    /**
     * Faz a fusão merge entre dois filhos adjacentes deste nó.
     *
     * @param leftChild filho à esquerda
     * @param rightChild filho à direita
     * @param leftChildIndex índice da chave separadora correspondente em this.keys
     */
    private void mergeChildren(BNode<K, V> leftChild, BNode<K, V> rightChild, int leftChildIndex) {
        System.out.println("### FUSÃO DE NÓ INTERNO ###");
        K separator = this.keys.remove(leftChildIndex);
        
        if (leftChild.isLeaf()) {
            ((LeafBNode<K, V>) leftChild).mergeWith((LeafBNode<K, V>) rightChild, false);
        } else {
            InternalBNode<K, V> leftInternal = (InternalBNode<K, V>) leftChild;
            InternalBNode<K, V> rightInternal = (InternalBNode<K, V>) rightChild;
            leftInternal.keys.add(separator);
            leftInternal.keys.addAll(rightInternal.keys);
            leftInternal.children.addAll(rightInternal.children);
            for(BNode<K, V> child : rightInternal.children) {
                child.parent = leftInternal;
            }
        }
        this.children.remove(leftChildIndex + 1);
    }
}