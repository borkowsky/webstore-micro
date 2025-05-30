package net.rewerk.webstore.utility;

import java.util.List;

/**
 * Common utility methods
 *
 * @author rewerk
 */

public abstract class CommonUtils {

    /**
     * Retrieve new items in list
     *
     * @param oldList Old list to compare
     * @param newList New list to compare
     * @param <T>     Generic type of list item
     * @return List of newly added items
     */

    public static <T> List<T> newItemsInList(List<T> oldList, List<T> newList) {
        return newList.stream()
                .filter(i -> !oldList.contains(i))
                .toList();
    }

    /**
     * Retrieve removed items in list
     *
     * @param oldList Old list to compare
     * @param newList New list to compare
     * @param <T>     Generic type of list item
     * @return List of removed items from new list parameter
     */

    public static <T> List<T> lostItemsInList(List<T> oldList, List<T> newList) {
        return oldList.stream()
                .filter(i -> !newList.contains(i))
                .toList();
    }
}
