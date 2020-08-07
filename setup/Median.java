package setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Median<Item>  {

    /**
     * Linear time median finding algorithm
     * @param items the list of items to find the median of
     * @param comparator the comparator of how to order the items
     * @return the median item in the list of items
     */
    public Item median(Item[] items, Comparator<Item> comparator) {
        return median(items, comparator, items.length/2);
    }


    /**
     * Linear time median finding algorithm
     * Only find the median in the specified range
     * @param items the list of items to find the median of
     * @param comparator the comparator of how to order the items
     * @return the median item in the list of items
     */
    public Item median(Item[] items, Comparator<Item> comparator, int k) {
        int size = items.length;
        Item[] medians = (Item[]) new Object[size/ 5];
        if (medians.length == 1) medians = (Item[]) new Object[2];
        if (size < 6) {
            Arrays.sort(items, comparator);
            return items[k];
        }
        else {
            int pointer = 0;
            for (int i = 0; i < medians.length; i++) {
                if (i == medians.length-1)
                    medians[i] = median(Arrays.copyOfRange(items, pointer, size),comparator);
                else
                    medians[i] = median(Arrays.copyOfRange(items, pointer, pointer+5), comparator);
                pointer += 5;
            }
        }
        Item pivot = median(medians, comparator);
        ArrayList<Item> left= new ArrayList<>();
        ArrayList<Item> right = new ArrayList<>();
        ArrayList<Item> pivots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (comparator.compare(items[i], pivot) < 0 ) {
                left.add(items[i]);
            }
            else if (comparator.compare(items[i], pivot) > 0 ){
                right.add(items[i]);
            }
            else
                pivots.add(items[i]);
        }

        int l = left.size();
        if (l == k || (k > l && k < l + pivots.size())) return pivot;
        else if (l < k) return median((Item[]) right.toArray(), comparator, k-l-pivots.size());
        else return median((Item[]) left.toArray(), comparator, k);
    }
}
