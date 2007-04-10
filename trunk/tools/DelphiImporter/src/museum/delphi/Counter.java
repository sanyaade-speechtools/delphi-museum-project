/**
 *
 */
package museum.delphi;

//import java.io.Serializable;
//import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * @author Patrick
 *
 * This maintains a hashMap of items and counts, with convenience methods for
 * getting, setting, and incrementing counts.  Useful for counting vocabulary, etc.
 *
 * @author Patrick Schmitz
 */
public class Counter <I> {

	HashMap<I, Double> itemMap;

	boolean fCacheStale = false;
	double cacheTotalCount = 0.0;

	public Counter() {
		itemMap = new HashMap<I, Double>();
	}

	/**
	 * The items in the counter, as a set (for iterating, etc.)
	 *
	 * @return set of items
	 */
	public Set<I> itemSet() {
		return itemMap.keySet();
	}

	/**
	 * The number of items in the counter (not the total count -- use
	 * totalCount() instead).
	 */
	public int size() {
		return itemMap.size();
	}

	/**
	 * True if there are no items in the counter
	 * (but false does not guarantee totalCount > 0)
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns whether the map contains the given item.
	 * This will return true even if the count for the item is 0,
	 * unlike getCount that will return 0 for both 0 count as well as
	 * if the map does not contain the item.
	 *
	 * @param item
	 * @return whether the counter contains the item
	 */
	public boolean containsKey(I item) {
		return itemMap.containsKey(item);
	}

	/**
	 * Get the count of the item, or zero if the item is not in the
	 * counter.
	 *
	 * @param item
	 * @return
	 */
	public double getCount(I item) {
		Double value = itemMap.get(item);
		if (value == null)
			return 0;
		return value;
	}

	/**
	 * Set the count for the given item, clobbering any previous count.
	 *
	 * @param item
	 * @param count
	 */
	public void setCount(I item, double count) {
		fCacheStale = true;
		itemMap.put(item, count);
	}

	/**
	 * Increment a item's count by the given amount.
	 *
	 * @param item
	 * @param increment
	 */
	public void incrementCount(I item, double increment) {
		setCount(item, getCount(item) + increment);
	}

	/**
	 * Finds the total of all counts in the counter.  This implementation uses
	 * cached count which may get out of sync if the itemMap map is modified in
	 * some unantipicated way.
	 *
	 * @return the counter's total
	 */
	public double totalCount() {
		if(fCacheStale) {
			double total = 0.0;
			for (Map.Entry<I, Double> item : itemMap.entrySet()) {
				total += item.getValue();
			}
			cacheTotalCount = total;
			fCacheStale = false;
		}
		return cacheTotalCount;
	}

	/**
	 * Destructively normalize this Counter in place.
	 */
	public void normalize() {
		double totalCount = totalCount();
		for (I item : itemSet()) {
			setCount(item, getCount(item) / totalCount);
		}
	}

	/**
	 * Finds the item with maximum count.  This is a linear operation, and ties are
	 * broken arbitrarily.
	 *
	 * @return a item with maximum count
	 */
	public I getTopItem() {
		double maxCount = Double.NEGATIVE_INFINITY;
		I maxKey = null;
		for (Map.Entry<I, Double> item : itemMap.entrySet()) {
			if (item.getValue() > maxCount || maxKey == null) {
				maxKey = item.getKey();
				maxCount = item.getValue();
			}
		}
		return maxKey;
	}

	/**
	 * Returns a string representation with the items ordered by decreasing
	 * counts.
	 *
	 * @return string representation
	 */
	public String toString(boolean fWithNewlines) {
		return toString(0, fWithNewlines);
	}

	/**
	 * Returns a string representation which includes items with at least
	 * a count of minCountToPrint.
	 *
	 * @param minCountToPrint
	 * @return partial string representation
	 */
	public String toString(int minCountToPrint, boolean fWithNewlines) {
		return asPriorityQueue().toString(minCountToPrint, fWithNewlines);
	}

	/**
	 * Writes the counter out in counter order, down to the threshold.
	 *
	 * @param minCountToPrint
	 * @return partial string representation
	 */
	 public void write( java.io.BufferedWriter writer, boolean fDestructive, int minCountToPrint, boolean fWithNewlines)
		throws java.io.IOException {
		asPriorityQueue().write( writer, fDestructive, minCountToPrint, fWithNewlines);
	 }

	/**
	 * Builds a priority queue whose items are the counter's items, and
	 * whose priorities are those items' counts in the counter.
	 */
	public PriorityQueue<I> asPriorityQueue() {
		PriorityQueue<I> pq = new PriorityQueue<I>(itemMap.size());
		for (Map.Entry<I, Double> item : itemMap.entrySet()) {
			pq.add(item.getKey(), item.getValue());
		}
		return pq;
	}

}