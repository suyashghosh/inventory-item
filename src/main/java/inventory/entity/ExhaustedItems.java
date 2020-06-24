package inventory.entity;

import java.util.List;

public class ExhaustedItems {

	private List<String> itemsRunninglow;
	private List<String> itemsExhausted;
	private List<String> menuAffected;
	
	public List<String> getItemsRunninglow() {
		return itemsRunninglow;
	}
	public void setItemsRunninglow(List<String> itemsRunninglow) {
		this.itemsRunninglow = itemsRunninglow;
	}
	public List<String> getItemsExhausted() {
		return itemsExhausted;
	}
	public void setItemsExhausted(List<String> itemsExhausted) {
		this.itemsExhausted = itemsExhausted;
	}
	public List<String> getMenuAffected() {
		return menuAffected;
	}
	public void setMenuAffected(List<String> menuAffected) {
		this.menuAffected = menuAffected;
	}
	
	
	
}
