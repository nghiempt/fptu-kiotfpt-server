package com.kiotfpt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kiotfpt.model.Accessibility_item;
import com.kiotfpt.model.Brand;
import com.kiotfpt.model.Cart;
import com.kiotfpt.model.Category;
import com.kiotfpt.model.Product;
import com.kiotfpt.model.Product_Condition;
import com.kiotfpt.model.ResponseObject;
import com.kiotfpt.model.Section;
import com.kiotfpt.model.Shop;
import com.kiotfpt.model.Status;
import com.kiotfpt.repository.AccessibilityItemRepository;
import com.kiotfpt.repository.CartRepository;
import com.kiotfpt.repository.SectionRepository;
import com.kiotfpt.response.Accessibility_itemResponse;
import com.kiotfpt.response.SectionResponse;
import com.kiotfpt.response.ShopResponse;
import com.kiotfpt.response.StatusResponse;
import com.kiotfpt.utils.JsonReader;

@Service
public class CartService {

	@Autowired
	private CartRepository repository;

	@Autowired
	private SectionRepository sectionRepository;

	@Autowired
	private AccessibilityItemRepository itemRepository;

//	@Autowired
//	private AddressDeliverRepository addRepository;
	HashMap<String, String> responseMessage = new JsonReader().readJsonFile();

//	public ResponseEntity<ResponseObject> getCartByAccount(int account_id) {
//			List<Cart> carts = repository.findAllByAccountId(account_id);
//			return !carts.isEmpty()
//					? ResponseEntity.status(HttpStatus.OK)
//							.body(new ResponseObject(true, HttpStatus.OK.toString().split(" ")[0],
//									responseMessage.get("cartFound"), carts.get(0)))
//					: ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
//							HttpStatus.NOT_FOUND.toString().split(" ")[0], responseMessage.get("cartNotFound"), ""));
//	}

	public ResponseEntity<ResponseObject> getCartByID(int cart_id) {
		Optional<Cart> cart = repository.findById(cart_id);
		if (!cart.isEmpty()) {
			List<Section> sections = sectionRepository.findByCart(cart.get());
			if (!sections.isEmpty()) {

				List<SectionResponse> list = new ArrayList<SectionResponse>();
				for (Section section : sections) {

					List<Accessibility_item> list_item = itemRepository.findBySection(section);
					if (!list_item.isEmpty()) {
						List<Accessibility_itemResponse> items = new ArrayList<Accessibility_itemResponse>();
						for (Accessibility_item item : list_item) {
							Product product = item.getProduct();

							if (product == null) {
								return ResponseEntity.status(HttpStatus.NOT_FOUND)
										.body(new ResponseObject(false, HttpStatus.NOT_FOUND.toString().split(" ")[0],
												"Product of item: " + item.getItem_id() + " not found", ""));
							}
							Product_Condition condition = product.getProduct_condition();

							if (condition == null) {
								return ResponseEntity.status(HttpStatus.NOT_FOUND)
										.body(new ResponseObject(false, HttpStatus.NOT_FOUND.toString().split(" ")[0],
												"Condition of product id: " + product.getProduct_id() + " not found",
												""));
							}
							Brand brand = product.getBrand();

							if (brand == null) {
								return ResponseEntity.status(HttpStatus.NOT_FOUND)
										.body(new ResponseObject(false, HttpStatus.NOT_FOUND.toString().split(" ")[0],
												"Brand of product id: " + product.getProduct_id() + "not found", ""));
							}
							Status status = product.getStatus();

							if (status == null) {
								return ResponseEntity.status(HttpStatus.NOT_FOUND)
										.body(new ResponseObject(false, HttpStatus.NOT_FOUND.toString().split(" ")[0],
												"Status of product id: " + product.getProduct_id() + "not found", ""));
							}
							Category category = product.getCategory();

							if (category == null) {
								return ResponseEntity.status(HttpStatus.NOT_FOUND)
										.body(new ResponseObject(false, HttpStatus.NOT_FOUND.toString().split(" ")[0],
												"Category of product id: " + product.getProduct_id() + "not found",
												""));
							}

							Accessibility_itemResponse item_res = new Accessibility_itemResponse(item);
							items.add(item_res);
						}

						Shop get_shop = section.getShop();
						Status get_status = section.getStatus();

						ShopResponse shop = new ShopResponse(get_shop.getShop_id(), get_shop.getShop_name(), null, null,
								get_shop.getShop_thumbnail(), null);

						StatusResponse status = new StatusResponse(get_status.getStatus_id(), get_status.getValue());

						SectionResponse response = new SectionResponse(section.getSection_id(),
								section.getSection_total(), shop, status, items);
						list.add(response);
					} else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
								HttpStatus.NOT_FOUND.toString().split(" ")[0], "Items do not exist", ""));
					}
				}
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject(true, HttpStatus.OK.toString().split(" ")[0], "Section found", list));
			}

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
					HttpStatus.NOT_FOUND.toString().split(" ")[0], "Sections do not exist", ""));
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
				HttpStatus.NOT_FOUND.toString().split(" ")[0], responseMessage.get("cartNotFound"), ""));
	}

//	public ResponseEntity<ResponseObject> createCart(Map<String, String> map) {
//		Optional<Account> account = accountRepository.findById(Integer.parseInt(map.get("account_id")));
//		Optional<AddressDeliver> address = addRepository.findById(Integer.parseInt(map.get("address_id")));
//		Cart cart = new Cart();
//		cart.setAddress(address.get());
//		cart.setAccount(account.get());
//		cart.setStatus(map.get("status"));
//		cart.setTotal(Double.parseDouble(map.get("total")));
//		return ResponseEntity.status(HttpStatus.CREATED)
//				.body(new ResponseObject(true, HttpStatus.CREATED.toString().split(" ")[0],
//						responseMessage.get("createCartSuccess"), repository.save(cart)));
//	}
//
//	public ResponseEntity<ResponseObject> updateCart(Cart newCart) {
//		Cart updateCart = repository.findById(newCart.getId()).map(Cart -> {
//			Cart.setStatus(newCart.getStatus());
//			Cart.setTotal(newCart.getTotal());
//			return repository.save(Cart);
//		}).orElseGet(() -> {
//			return null;
//		});
//		if (updateCart == null) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
//					HttpStatus.NOT_FOUND.toString().split(" ")[0], responseMessage.get("CartNotFound"), ""));
//		} else {
//			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(false,
//					HttpStatus.OK.toString().split(" ")[0], responseMessage.get("updateCartSuccess"), updateCart));
//		}
//	}

}
