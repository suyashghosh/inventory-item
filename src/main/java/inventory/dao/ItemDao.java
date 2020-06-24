package inventory.dao;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import inventory.entity.ExhaustedItems;
import inventory.entity.Item;
import inventory.entity.ItemUsedIn;
import inventory.entity.Message;

@Repository
public class ItemDao {

	private EntityManager entityManager;
	
	@Autowired
	public ItemDao(EntityManager theEntityManager) {
		entityManager = theEntityManager;
	}
	
	
	@Transactional
	public List<Item> getItems() {
		
		TypedQuery<Item> theQuery = entityManager.createQuery("from Item",Item.class); // table name should be caps		
		List<Item> items = theQuery.getResultList();		
		return items;
	}
	
	@Transactional
	public Item getItemById(String id) {
		
		TypedQuery<Item> query = entityManager.createQuery("from Item where i_id = ?1",Item.class); // table name should be caps		
		query.setParameter(1,id);
		Item item = query.getSingleResult();		
		return item;
	}
	
	@Transactional
	public void updateItem(Item item) {
		
		Item dbItem = getItemById(item.getItemId());	
		dbItem.setItemName(item.getItemName());
		dbItem.setQuantity(item.getQuantity());
		dbItem.setMinQuantity(item.getMinQuantity());
		dbItem.setUnit(item.getUnit());
	}
	
	@Transactional
	public boolean checkPresence(String searchKeyword) {
		TypedQuery query = (TypedQuery) entityManager.createQuery("from Item where lower(item_Name) = lower(:keyword)");
		query.setParameter("keyword",searchKeyword);		
		if (query.getResultList().size() > 0) {
			return true;
		}
		return false;
	}
	
	@Transactional
	public String generateId(String itemName) {
		String twoChar = itemName.trim().substring(0, 2);		
		TypedQuery<Item> query = entityManager.createQuery("from Item where lower(item_name) like lower(:keyword)",Item.class);
		query.setParameter("keyword",twoChar+"%");
		int count = query.getResultList().size();
		
		if (count > 0){
			return (twoChar.toUpperCase()+count);
		}else{
			return (twoChar.toUpperCase());
		}
	}
	
	@Transactional
	public void addItem(Item item) {
		
		entityManager.merge(item);		
	}
	
	@Transactional
	public Message deleteItem(Item item) {
		entityManager.remove(item);
		return new Message(true, "Item deleted");
	}
	
	@Transactional
	public ExhaustedItems getExhaustedItems() {		
		
		Query query = entityManager.createNativeQuery("select item_name from item where quantity <= min_quantity and quantity > 0");
		List itemBelowMin = query.getResultList();		
		
		query = entityManager.createNativeQuery("select item_name from item where quantity <= 0");
		List itemExhausted = query.getResultList();
		
		ExhaustedItems exhaustedItems = new ExhaustedItems();
		exhaustedItems.setItemsRunninglow(itemBelowMin);
		exhaustedItems.setItemsExhausted(itemExhausted);
		
		return exhaustedItems;		
	}
	
	public List<String> getExhaustedItemIdList(){
		Query query = entityManager.createNativeQuery("select i_id from item where quantity <= min_quantity");
		return query.getResultList();
	}
}
