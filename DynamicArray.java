@SuppressWarnings("unchecked")
public class DynamicArray<T> implements Iterable<T>{
     //creates static array, length (to monitor current index size), and capacity to show total size allowed.
     private T[] arr;
     private int len = 0;
     private int capacity = 0;

     //sets basic constructor to static array with possible 16 elements.
     public DynamicArray() {
         this.capacity = 16;
     }

     //specified capacity for constructor
     public DynamicArray(int capacity) {
         if (capacity < 0) {
             throw new IllegalArgumentException("Illegal Capacity: " + capacity);
         }
         this.capacity = capacity;
         arr = (T[]) new Object[capacity];
     }

     //returns the size of the dynamic array
     public int size() { return len; }
     //tells if DA is empty or not
     public boolean isEmpty() { return size() == 0; }

     //getter and setter methods for the dynamic array
     public T get(int index) {return arr[index];}
     public void set(int index, T elem) {arr[index] = elem;}

     //sets each of the elements to null
     public void clear() {
         for(int i = 0; i < capacity; i++)
             arr[i] = null;
         len = 0;
     }

     //In order to add, it will add to the end of the array.
     //However, if the next added element is greater than capacity, then capacity is doubled and a new array is developed.
     //This new array will then be created with the T implementation and each new element from the original is added to the new.
     //Finally, the original arr is set to reference of the new array created.
     public void add(T elem) {
         if(len+1 >= capacity) {
             if (capacity == 0) capacity = 1;
             else capacity *= 2; //doubling size of arr
             T[] new_arr = (T[]) new Object[capacity];
             for (int i = 0; i < len; i++)
                 new_arr[i] = arr[i];
             arr = new_arr;
         }
         arr[len++] = elem;
     }

     //removes element from specified index and shifts down all remaining elements.
     public T removeAt(int rm_index) {
         if (rm_index >= len && rm_index < 0)
             throw new IndexOutOfBoundsException();
         T data = arr[rm_index];
         T[] new_arr = (T[]) new Object[len-1];
         for(int i = 0, j = 0; i < len; i++, j++) {
             if (i == rm_index)
                 j--;
             else
                 new_arr[j] = arr[i];
         }
         arr = new_arr;
         capacity = --len;
         return data;
     }

     //Finds where the specified element obj is and removes it.
     //Uses the removeAt method once found the index.
     public boolean remove(Object obj) {
         for (int i = 0; i < len; i++) {
             if (arr[i].equals(obj)) {
                 removeAt(i);
                 return true;
             }
         }
         return false;
     }

     //Searches for the object's index point and returns index value.
     public int indexOf(Object obj) {
         for(int i = 0; i < len; i++) {
             if (arr[i].equals(obj))
                 return i;
         }
         return -1;
     }

     //Identifies if obj is in the list
     public boolean contains(Object obj) {
         return indexOf(obj) != -1;
     }

     //Iterator method for DA to use
     @Override public java.util.Iterator <T> iterator() {
        return new java.util.Iterator <T> () {
            int index = 0;
            public boolean hasNext() {
                return index < len;
            }
            public T next() {
                return arr[index++];
            }
        };
     }

     //Tostring conversion to print the elements of the array in string format.
     @Override public String toString() {
         if (len == 0)
             return "[]";
         else {
             StringBuilder sb = new StringBuilder(len).append("[");
             for (int i = 0; i < len-1; i++) {
                 sb.append(arr[i] + ", ");
             }
             return sb.append(arr[len-1] + "]").toString();
         }
     }
}
