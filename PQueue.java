import java.util.*;
public class PQueue <T extends Comparable<T>> {
    //The number of elements currently inside the heap
    private int heapSize = 0;

    //The internal capacity of the heap
    private int heapCapacity = 0;

    //A dynamic list to track the elements inside the heap
    private List <T> heap = null;

    //This map keeps track of the possible indices a particular
    //node value has found in the heap. Having this mapping lets us
    //have O(log(n)) removals and O(1) element containment
    //check at the cost of some additional space and minor overhead
    private Map<T, TreeSet<Integer>> map = new HashMap<>();

    //Construct and initially empty priority queue
    public PQueue() {
        this.heapCapacity = 1;
    }

    //Construct a priority queue with an initial capacity
    public PQueue(int sz) {
        heap = new ArrayList<>(sz);
    }

    //Construct a priority queue using heapify in O(n) time
    public PQueue(T[] elems) {
        heapSize = heapCapacity = elems.length;
        heap = new ArrayList<T>(heapCapacity);

        //Place all elements in heap
        for(int i = 0; i < heapSize; i++) {
            mapAdd(elems[i], i);
            heap.add(elems[i]);
        }

        //Heapify process, O(n)
        for(int i = Math.max(0, (heapSize/2) - 1); i >= 0; i--)
            sink(i);
    }

    //Priority queue construction for O(nlogn)
    public PQueue(Collection <T> elems) {
        this(elems.size());
        for(T elem: elems)
            add(elem);
    }

    //returns true/false depending on if the priority queue is empty
    public boolean isEmpty() {
        return heapSize == 0;
    }

    //Clears everything inside the heap, O(n)
    public void clear() {
        for(int i = 0; i< heapCapacity; i++)
            heap.set(i, null);
        heapSize = 0;
        map.clear();
    }

    //Return the size of the heap
    public int size() {
        return heapSize;
    }

    //Returns the value of the element with the lowest priority
    //in this priority queue. If the priority queue
    //is empty, null is returned.
    public T peek() {
        if(isEmpty())
            return null;
        return heap.get(0);
    }

    //Removes the root of the heap, O(logn)
    public T poll() {
        return removeAt(0);
    }

    //Test if an element is in the heap, O(1)
    public boolean contains(T elem) {
        //Map lookup to check containment, O(1)
        if(elem == null)
            return false;
        return map.containsKey(elem);
        //Above is much better process than below

        //Linear scan to check containment, O(n)
        //for(int i = 0; i < heapSize; i++)
        //  if(heap.get(i).equals(elem))
        //      return true;
        //return false;
    }

    //Adds an element to the priority queue, the
    //element must not be null, O(logn)
    public void add(T elem) {
        if (elem == null)
            throw new IllegalArgumentException();
        if (heapSize < heapCapacity) {
            heap.set(heapSize, elem);
        } else {
            heap.add(elem);
            heapCapacity++;
        }

        mapAdd(elem, heapSize);
        //bubble up process
        swim(heapSize);
        heapSize++;
    }

    //Tests if the value of node i <= node j
    //This method assumes i and j are valid indices, O(1)
    private boolean less(int i, int j) {
        T node1 = heap.get(i);
        T node2 = heap.get(j);
        return node1.compareTo(node2) <= 0;
    }

    //bottom up node swim, (bubble up function), O(logn)
    private void swim(int k) {
        //grab the index of the next parent node WRT to k.
        int parent = (k - 1) /2;

        //Keep swimming while we have not reached the root and while we're less than our parent.
        while(k > 0 && less (k, parent)) {
            //exchange k with the parent
            swap(parent, k);
            k = parent;

            //grab the index of the next parent node WRT to k
            parent = (k-1) / 2;
        }
    }

    //top down sink (bubble down function) O(logn)
    private void sink(int k) {
        while(true) {
            int left = 2 * k + 1; //left node
            int right = 2 * k + 2; //right node
            int smallest = left; //assume left is the smallest node of the two children

            //find which is smaller left or right
            //if right is smaller get smallest to be right
            if(right < heapSize && less(right, left))
                smallest = right;

            //Stop if we're outside the bounds of the tree
            //or stop early if we cannot sink k anymore
            if(left >= heapSize || less(k, smallest))
                break;

            //Move down the tree following the smallest node
            swap(smallest, k);
            k = smallest;
        }
    }

    //swap two nodes, assume i and j are valid, O(1)
    private void swap(int i, int j) {
        T i_elem = heap.get(i);
        T j_elem = heap.get(j);

        heap.set(i, j_elem);
        heap.set(j, i_elem);

        mapSwap(i_elem, j_elem, i, j);
    }

    //removes a particular element in the heap, O(logn)
    public boolean remove(T element) {
        if (element == null)
            return false;

        //logarithmic removal with map, O(logn)
        Integer index = mapGet(element);
        if(index != null)
            removeAt(index);
        return index != null;
    }

    //removes a node at a particular index, O(logn)
    private T removeAt(int i) {
        if(isEmpty())
            return null;
        heapSize--;
        T removed_data = heap.get(i);
        swap(i, heapSize);

        //Obliterate the value
        heap.set(heapSize, null);
        mapRemove(removed_data, heapSize);

        //removed last element
        if(i == heapSize)
            return removed_data;
        T elem = heap.get(i);

        //Try sinking element
        sink(i);

        //If sinking did not work try swimming
        if(heap.get(i).equals(elem))
            swim(i);
        return removed_data;
    }

    //recursively checks if this heap is a min heap
    //this method is just for testing purposes to make sure
    //the heap invariant is still being maintained
    //Called this method with k=0 to start at the root
    public boolean isMinHeap(int k) {
        //If we are outside the bounds of the heap return true
        if(k >= heapSize)
            return true;
        int left = 2 * k + 1;
        int right = 2 * k + 2;

        //make sure that the current node k is less than
        //both of its children left, and right if they
        //exist. Return false otherwise to indicate an invalid heap.
        if(left < heapSize && !less(k, left))
            return false;
        if(right < heapSize && !less(k, right))
            return false;

        //recurse on both children to make sure they're also valid heaps
        return isMinHeap(left) && isMinHeap(right);
    }

    //Add a node value and its index to the map
    private void mapAdd(T value, int index) {
        TreeSet <Integer> set = map.get(value);

        //new value being inserted in map
        if(set == null) {
            set = new TreeSet<>();
            set.add(index);
            map.put(value, set);
        }
        //value already exists in map
        else
            set.add(index);
    }

    //removes the index at a given value, O(logn)
    private void mapRemove(T value, int index) {
        TreeSet <Integer> set = map.get(value);
        set.remove(index); //treesets take O(logn) removal time
        if (set.size() == 0)
            map.remove(value);
    }

    //extract an index position for the given value
    //note: if a value exists multiple times in the heap
    //the highest index is returned (this has arbitrarily been chosen)
    private Integer mapGet(T value) {
        TreeSet <Integer> set = map.get(value);
        if (set != null)
            return set.last();
        return null;
    }

    //Exchange the index of two nodes internally within the map
    private void mapSwap(T val1, T val2, int val1Index, int val2Index) {
        Set<Integer> set1 = map.get(val1);
        Set<Integer> set2 = map.get(val2);

        set1.remove(val1Index);
        set2.remove(val2Index);

        set1.add(val2Index);
        set2.add(val1Index);
    }
}
