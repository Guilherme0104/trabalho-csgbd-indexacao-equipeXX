package bplus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class InternalBNode<K extends Comparable<K>, V> extends BNode<K, V> {

    protected List<BNode<K, V>> children;

    public InternalBNode(int order) {
        super(order);
        this.children = new ArrayList<>();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public V search(K key) {
        int i = 0;
        while (i < this.keys.size() && key.compareTo(this.keys.get(i)) >= 0) {
            i++;
        }
        return this.children.get(i).search(key);
    }

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

    @Override
    public K getFirstLeafKey(){
        if (children.isEmpty()){
            return null;
        }
        return children.get(0).getFirstLeafKey();
    }

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

    void handleChildUnderflow(BNode<K, V> childInUnderflow) {
        int childIndex = this.children.indexOf(childInUnderflow);
        
        int minKeys = (int) Math.ceil((this.order + 1) / 2.0) - 1;

        if (childIndex > 0 && tryRedistribute(childInUnderflow, this.children.get(childIndex - 1), childIndex, true, minKeys)) {
            return;
        }
        
        if (childIndex < this.children.size() - 1 && tryRedistribute(childInUnderflow, this.children.get(childIndex + 1), childIndex, false, minKeys)) {
            return;
        }

        if (childIndex > 0) {
            mergeChildren(this.children.get(childIndex - 1), childInUnderflow, childIndex - 1);
        } else {
            mergeChildren(childInUnderflow, this.children.get(childIndex + 1), childIndex);
        }

        if (this.getKeyCount() < minKeys && this.parent != null) {
            ((InternalBNode<K, V>) this.parent).handleChildUnderflow(this);
        }
    }

    private boolean tryRedistribute(BNode<K, V> child, BNode<K, V> sibling, int childIndex, boolean isLeftSibling, int minKeys) {
        
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
    
    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Interno: ").append(this.keys).append("\n");
        for (BNode<K, V> child : this.children) {
            sb.append(child.toString(indent + "  "));
            sb.append("\n");
        }
        return sb.toString();
    }
}