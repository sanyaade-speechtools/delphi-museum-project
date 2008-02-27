/**
 *
 */
package museum.delphi;

/**
 * A priority queue based on a binary heap.
 *
 * @author Patrick Schmitz
 */
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
//import java.io.Serializable;

public class PriorityQueue <I> implements Iterator<I>, Cloneable {

	int size;
	int capacity;
	List<I> items;
	double[] priorities;

	public PriorityQueue() {
		this(100);
	}

	public PriorityQueue(int capacity) {
		int properCapacity = 0;
		while (properCapacity < capacity) {
			properCapacity = 2 * properCapacity + 1;
		}
		grow(properCapacity);
	}

	protected void grow(int newCapacity) {
		List<I> newItems = new ArrayList<I>(newCapacity);
		double[] newPriorities = new double[newCapacity];
		if (size > 0) {
			newItems.addAll(items);
			System.arraycopy(priorities, 0, newPriorities, 0, priorities.length);
		}
		items = newItems;
		priorities = newPriorities;
		capacity = newCapacity;
	}

	protected int parent(int loc) {
		return (loc - 1) / 2;
	}

	protected int leftChild(int loc) {
		return 2 * loc + 1;
	}

	protected int rightChild(int loc) {
		return 2 * loc + 2;
	}

	protected void resortUp(int loc) {
		if (loc == 0) return;
		int parent = parent(loc);
		if (priorities[loc] > priorities[parent]) {
			swap(loc, parent);
			resortUp(parent);
		}
	}

	protected void resortDown(int loc) {
		int max = loc;
		int leftChild = leftChild(loc);
		if (leftChild < size()) {
			double priority = priorities[loc];
			double leftChildPriority = priorities[leftChild];
			if (leftChildPriority > priority)
				max = leftChild;
			int rightChild = rightChild(loc);
			if (rightChild < size()) {
				double rightChildPriority = priorities[rightChild(loc)];
				if (rightChildPriority > priority && rightChildPriority > leftChildPriority)
					max = rightChild;
			}
		}
		if (max == loc)
			return;
		swap(loc, max);
		resortDown(max);
	}

	protected void swap(int loc1, int loc2) {
		double tempPriority = priorities[loc1];
		I tempItem = items.get(loc1);
		priorities[loc1] = priorities[loc2];
		items.set(loc1, items.get(loc2));
		priorities[loc2] = tempPriority;
		items.set(loc2, tempItem);
	}

	protected void removeFirst() {
		if (size < 1) return;
		swap(0, size - 1);
		size--;
		items.remove(size);
		resortDown(0);
	}

	/**
	 * Returns true if the priority queue is non-empty
	 */
	 public boolean hasNext() {
		 return ! isEmpty();
	 }

	/**
	 * Returns the item in the queue with highest priority, and pops it from
	 * the queue.
	 */
	 public I next() {
		 I first = peek();
		 removeFirst();
		 return first;
	 }

	 /**
	  * Not supported -- next() already removes the head of the queue.
	  */
	 public void remove() {
		 throw new UnsupportedOperationException();
	 }

	 /**
	  * Returns the highest-priority item in the queue, but does not pop it.
	  */
	 public I peek() {
		 if (size() > 0)
			 return items.get(0);
		 throw new NoSuchElementException();
	 }

	 /**
	  * Gets the priority of the highest-priority item of the queue.
	  */
	 public double getPriority() {
		 if (size() > 0)
			 return priorities[0];
		 throw new NoSuchElementException();
	 }

	 /**
	  * Number of items in the queue.
	  */
	 public int size() {
		 return size;
	 }

	 /**
	  * True if the queue is empty (size == 0).
	  */
	 public boolean isEmpty() {
		 return size == 0;
	 }

	 /**
	  * Adds a key to the queue with the given priority.  If the key is already in
	  * the queue, it will be added an additional time, NOT promoted/demoted.
	  *
	  * @param key
	  * @param priority
	  */
	 public boolean add(I key, double priority) {
		 if (size == capacity) {
			 grow(2 * capacity + 1);
		 }
		 items.add(key);
		 priorities[size] = priority;
		 resortUp(size);
		 size++;
		 return true;
	 }

	 /**
	  * Returns a representation of the queue in decreasing priority order.
	  */
	 public String toString(boolean fWithNewlines) {
		 return toString(0, fWithNewlines);
	 }

	 /**
	  * Returns a representation of the queue in decreasing priority order,
	  * down to those with a priority of minCountToPrint.
	  *
	  * @param minPrioToPrint
	  * @return partial string representation
	  */
	 public String toString(int minPrioToPrint, boolean fWithNewlines) {
		 PriorityQueue<I> pq = clone();
		 StringBuilder sb = new StringBuilder("[");
		 int numItemsPrinted = 0;
		 while(pq.hasNext()) {
			 double priority = pq.getPriority();
			 if(priority < minPrioToPrint)
				 break;
			 I item = pq.next();
			 sb.append(item.toString());
			 sb.append(" : ");
			 sb.append(priority);
			 if (numItemsPrinted < size() - 1) {
				 sb.append(fWithNewlines?"\n":", ");
			 }
			 numItemsPrinted++;
		 }
		 if (numItemsPrinted < size())
			 sb.append("...");
		 sb.append("]");
		 return sb.toString();
	 }

	 /**
	  * Returns a representation of the queue in decreasing priority order,
	  * down to those with a priority of minCountToPrint.
	  *
	  * @param minPrioToPrint
	  */
	 public void write( java.io.BufferedWriter writer, boolean fDestructive, int minPrioToPrint, boolean fWithNewlines)
	 			throws java.io.IOException {
		 PriorityQueue<I> pq = fDestructive?this:clone();
		 int numItemsPrinted = 0;
		 int nTotalMinus1 = size()-1;
		 while(pq.hasNext()) {
			 double priority = pq.getPriority();
			 if(priority < minPrioToPrint)
				 break;
			 I item = pq.next();
			 writer.append(item.toString()+" : "+priority);
			 if (numItemsPrinted < nTotalMinus1) {
				 if(fWithNewlines)
					 writer.newLine();
				 else
					 writer.append(", ");
			 }
			 numItemsPrinted++;
		 }
		 if (numItemsPrinted < size())
			 writer.append("...");
		 writer.flush();
		 return;
	 }

	 /**
	  * Returns a counter whose keys are the items in this priority queue, and
	  * whose counts are the priorities in this queue.  In the event there are
	  * multiple instances of the same item in the queue, the counter's count
	  * will be the sum of the instances' priorities.
	  *
	  * @return new Counter for this priority queue.
	  */
	 public Counter asCounter() {
		 PriorityQueue<I> pq = clone();
		 Counter<I> counter = new Counter<I>();
		 while (pq.hasNext()) {
			 double priority = pq.getPriority();
			 I item = pq.next();
			 counter.incrementCount(item, priority);
		 }
		 return counter;
	 }

	 /**
	  * Returns a clone of this priority queue.  Modifications to one will not
	  * affect modifications to the other.
	  */
	 public PriorityQueue<I> clone() {
		 PriorityQueue<I> clonePQ = new PriorityQueue<I>();
		 clonePQ.size = size;
		 clonePQ.capacity = capacity;
		 clonePQ.items = new ArrayList<I>(capacity);
		 clonePQ.priorities = new double[capacity];
		 if (size() > 0) {
			 clonePQ.items.addAll(items);
			 System.arraycopy(priorities, 0, clonePQ.priorities, 0, size());
		 }
		 return clonePQ;
	 }

}