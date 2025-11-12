package bplus;
import java.util.ArrayList;
import java.util.List;

abstract class BNode<K, V> {
    protected List<K> keys;
    protected final int order;
    protected BNode<K, V> parent;

    public BNode(int order) {
        if(order < 2){
            throw new IllegalArgumentException("order must be >= 2");
        }
        this.order = order;
        this.keys = new ArrayList<>();
    }

    public int getKeyCount(){
        return this.keys.size();
    }

    public boolean isFull(){
        return this.getKeyCount() == this.order;
    }

    public abstract boolean isLeaf();
    public abstract V search(K key);
    public abstract SplitResult<K, V> insert(K key, V value);
    public abstract void delete(K key);
    public abstract K getFirstLeafKey();
    
    public abstract String toString(String indent);
}