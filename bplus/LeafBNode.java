package bplus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class LeafBNode<K extends Comparable<K>, V> extends BNode<K, V> {
    protected List<V> values;
    protected LeafBNode<K, V> next;
    protected LeafBNode<K, V> previous;

    public LeafBNode(int order) {
        super(order);
        this.values = new ArrayList<>();
        this.next = null;
        this.previous = null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public V search(K key){
        int index = Collections.binarySearch(this.keys, key);
        if (index >= 0) {
            return this.values.get(index);
        }
        return null;
    }

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

    private SplitResult<K, V> split(){
        System.out.println("### SPLIT DE NÓ FOLHA ###");
        LeafBNode<K, V> sibling = new LeafBNode<>(this.order);
        
        int originalSize = this.getKeyCount(); // Salva o tamanho original
        int midIndex = originalSize / 2;
        
        List<K> siblingKeys = new ArrayList<>(this.keys.subList(midIndex, originalSize));
        List<V> siblingValues = new ArrayList<>(this.values.subList(midIndex, originalSize));
        sibling.keys.addAll(siblingKeys);
        sibling.values.addAll(siblingValues);
        
        this.keys.subList(midIndex, originalSize).clear();
        this.values.subList(midIndex, originalSize).clear();
        
        sibling.next = this.next;
        if (this.next != null) {
            this.next.previous = sibling;
        }
        this.next = sibling;
        sibling.previous = this;
        
        K promotedKey = sibling.getFirstLeafKey();
        return new SplitResult<>(promotedKey, sibling);
    }
    
    @Override
    public K getFirstLeafKey(){
        if(keys.isEmpty()){
            return null;
        }
        return keys.get(0);
    }

    @Override
    public void delete(K key) {
        int index = Collections.binarySearch(this.keys, key);
        if (index < 0) {
            return;
        }
        this.keys.remove(index);
        this.values.remove(index);

        int minKeys = (int) Math.ceil((this.order - 1) / 2.0);
        
        if (this.getKeyCount() < minKeys && this.parent != null) {
            ((InternalBNode<K, V>) this.parent).handleChildUnderflow(this);
        }
    }

    K redistributeFrom(LeafBNode<K, V> sibling, boolean isLeftSibling) {
        if (isLeftSibling) {
            int lastIndex = sibling.getKeyCount() - 1;
            K key = sibling.keys.remove(lastIndex);
            V value = sibling.values.remove(lastIndex);
            this.keys.add(0, key);
            this.values.add(0, value);
            return this.keys.get(0);
        } else {
            K key = sibling.keys.remove(0);
            V value = sibling.values.remove(0);
            this.keys.add(key);
            this.values.add(value);
            
            if (sibling.getKeyCount() == 0) {
                return this.keys.get(0);
            }
            return sibling.keys.get(0);
        }
    }

    void mergeWith(LeafBNode<K, V> sibling, boolean isLeftSibling) {
        System.out.println("### FUSÃO DE NÓ FOLHA ###");
        if (isLeftSibling) {
            sibling.keys.addAll(this.keys);
            sibling.values.addAll(this.values);
            sibling.next = this.next;
            if (this.next != null) {
                this.next.previous = sibling;
            }
        } else {
            this.keys.addAll(sibling.keys);
            this.values.addAll(sibling.values);
            this.next = sibling.next;
            if (sibling.next != null) {
                sibling.next.previous = this;
            }
        }
    }
    
    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Folha: ").append(this.keys);
        sb.append(" (Valores: ").append(this.values).append(")");
        return sb.toString();
    }
}