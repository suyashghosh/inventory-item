package inventory.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "item")
public class Item {
	
	@Id
	@Column(name = "i_id")
	private String itemId;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "quantity")
	private float quantity;
	
	@Column(name = "min_quantity")
	private float minQuantity;
	
	@Column(name = "unit")
	private String unit;
		
	@Transient
	private List<ItemUsedIn> itemUsedIn;
		
	public Item() {}

	public Item(String itemId, String itemName, float quantity, float minQuantity, String unit) {
		super();
		this.itemId = itemId;
		this.itemName = itemName;
		this.quantity = quantity;
		this.minQuantity = minQuantity;
		this.unit = unit;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public float getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(float minQuantity) {
		this.minQuantity = minQuantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<ItemUsedIn> getItemUsedIn() {
		return itemUsedIn;
	}

	public void setItemUsedIn(List<ItemUsedIn> itemUsedIn) {
		this.itemUsedIn = itemUsedIn;
	}
	
}
