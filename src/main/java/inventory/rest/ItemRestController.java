package inventory.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import inventory.dao.ItemDao;
import inventory.entity.ExhaustedItems;
import inventory.entity.Item;
import inventory.entity.ItemUsedIn;
import inventory.entity.Item_Quantity;
import inventory.entity.Message;
import inventory.entity.SoldMenuDetails;

@RestController
@RequestMapping("/pos")
public class ItemRestController {
	
	@Value("${getMenuWhereItemUsed.url}")
	private String getMenuWhereItemUsedUrl;
	
	@Value("${deleteRecipe.url}")
	private String deleteRecipeUrl;
	
	@Value("${getItemForMenu.url}")
	private String getItemForMenuUrl;
	
	@Value("${getAffectedDishList.url}")
	private String getAffectedDishListUrl;
	
	private ItemDao itemDao;
	
	@Autowired
	public ItemRestController(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	//Item 
	
	@PostMapping("/addItem")
	public Message addItem(@RequestBody Item item) {
		if(itemDao.checkPresence(item.getItemName())) {
			return new Message(false, "Item already present");
		}
		item.setItemId(itemDao.generateId(item.getItemName()));
		itemDao.addItem(item);
		return new Message(true, "Item Added");
	}
	
	@PostMapping("/updateItem")
	public Message updateItem(@RequestBody Item item) {
		if (item.getItemName() == null || item.getItemName().trim() == "") {
			return new Message(false, "Invalid item name");
		}
		itemDao.updateItem(item);
		return new Message(true,"Item Updated");
		
	}		
	
	@GetMapping("/getItem/{itemId}")
	public Item getItem(@PathVariable String itemId) {	
		Item item = itemDao.getItemById(itemId);
		return item;
	}
	
	@GetMapping("/getItem")
	public List<Item> getItems() {
		List<Item> items = itemDao.getItems();
		for(Item item : items) {
			List<ItemUsedIn> menuWhereItemUsed = new RestTemplate().
					getForObject(getMenuWhereItemUsedUrl+item.getItemId(), List.class);
			item.setItemUsedIn(menuWhereItemUsed);
		}
		return items;
	}
	
	@GetMapping("/deleteItem/{itemId}")
	public Message deleteItem(@PathVariable String itemId) {
		Message recipeMsg = new RestTemplate().getForObject(deleteRecipeUrl+"?itemId="+itemId, Message.class);
		if(recipeMsg.isStatus() == false) {
			return new Message(false, "Failed to delete");
		}
		return itemDao.deleteItem(itemDao.getItemById(itemId));
	}
	
	@GetMapping("/getExhaustedItems")
	public ExhaustedItems getExhaustedItems() {
		ExhaustedItems exhaustedItems = itemDao.getExhaustedItems();
		List<String> exhaustedItemIdList = itemDao.getExhaustedItemIdList();
		List<String> affectedDishList = new RestTemplate().postForObject(getAffectedDishListUrl, exhaustedItemIdList, List.class);
		exhaustedItems.setMenuAffected(affectedDishList);
		return exhaustedItems;
	}
	
	@PostMapping("/updateItemQuantity")
	public void updateItemQuantity(@RequestBody List<SoldMenuDetails> soldMenuDetails) {
		for(SoldMenuDetails soldMenuDetailsItr : soldMenuDetails) {
			//call recipe microservice to fetch items assocoated with menu
			String json = new RestTemplate().getForObject(getItemForMenuUrl+soldMenuDetailsItr.getMenuId(), String.class);
			ObjectMapper mapper = new ObjectMapper();
			List<Item_Quantity> itemQuantity = null;
			try {
				itemQuantity = Arrays.asList(mapper.readValue(json, Item_Quantity[].class));
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			//logic to decrease item count
			for(Item_Quantity itemQuant : itemQuantity) {
				Item item = itemDao.getItemById(itemQuant.getItemId());
				float quantityUsed = item.getQuantity() - (itemQuant.getQuantity() * (float)soldMenuDetailsItr.getQuantitySold());
				item.setQuantity(quantityUsed);
				itemDao.updateItem(item);
			}
		}
	}

}

