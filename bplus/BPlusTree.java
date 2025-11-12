package bplus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPlusTree<K extends Comparable<K>, V> {
    private BNode<K,V> root;
    private final int order;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new LeafBNode<>(order);
    }

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
    
    public V search(K key) {
        if (key == null){
            return null;
        }
        return this.root.search(key);
    }

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
    
    
    @Override
    public String toString() {
        if (this.root == null) {
            return "Árvore Vazia";
        }
        return this.root.toString("");
    }
}