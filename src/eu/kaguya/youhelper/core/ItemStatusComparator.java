package eu.kaguya.youhelper.core;

import java.util.Comparator;

import eu.kaguya.youhelper.ItemStatus;

public class ItemStatusComparator implements Comparator<ItemStatus> {

	@Override
	public int compare(ItemStatus o1, ItemStatus o2) {
		return Integer.compare(o1.order(), o2.order());
	}

}
