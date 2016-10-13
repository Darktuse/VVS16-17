package es.udc.subasta.test.model.bidservice;

import static es.udc.subasta.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.subasta.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bidservice.BidService;
import es.udc.subasta.model.bidservice.MinimumBidPriceException;
import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.category.CategoryDao;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.productservice.DatesException;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userservice.UserProfileDetails;
import es.udc.subasta.model.userservice.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class BidServiceTest {

	private final long NON_EXISTENT_PRODUCT_ID = -1;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private BidService bidService;

	@Autowired
	private CategoryDao categoryDao;

	private void createData() throws DuplicateInstanceException,
			InstanceNotFoundException, DatesException {

		UserProfile userProfile = userService.registerUser("user",
				"userPassword", new UserProfileDetails("name", "lastName",
						"user@udc.es"));

		UserProfile userProfile2 = userService.registerUser("user2",
				"userPassword", new UserProfileDetails("name2", "lastName2",
						"user2@udc.es"));

		Category category = new Category("Category Test");
		categoryDao.save(category);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2018);

		Product product1 = productService.insertAd("ProductTest1",
				"This is a product for the test", new BigDecimal(10.0), cal,
				"NA", userProfile.getUserProfileId(), category.getCategoryId());
		System.out.println(product1.getProductId());

	}

	@Test
	public void testCreateAndFindBid() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;

		Category category = null;

		Product product1 = null;

		try {
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", userProfile.getUserProfileId(),
					category.getCategoryId());
		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		Bid bid2 = bidService.findBid(bid.getBidId());

		assertEquals(bid, bid2);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testBidNonExistentProduct() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;

		Category category = null;

		try {
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

		} catch (DuplicateInstanceException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), NON_EXISTENT_PRODUCT_ID);

	}

	@Test
	public void testOverBid() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;

		Category category = null;

		Product product1 = null;

		try {
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", userProfile.getUserProfileId(),
					category.getCategoryId());

		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		assertEquals(product1.getCurrentBid().getUser().getEmail(),
				"user@udc.es");
		assertEquals(product1.getCurrentPrice(), new BigDecimal(10.0));
		assertEquals(product1.getCurrentBid().getAmount(), new BigDecimal(10.0));
		
		Bid bid2 = bidService.createBid(userProfile2.getUserProfileId(),
				new BigDecimal(12), product1.getProductId());

		assertEquals(product1.getCurrentBid().getUser().getEmail(),
				"user2@udc.es");
		assertEquals(product1.getCurrentPrice(), new BigDecimal(10.5));
		assertEquals(product1.getCurrentBid().getAmount(), new BigDecimal(12.0));

	}

	@Test(expected = MinimumBidPriceException.class)
	public void testOverBidMinimumNotReached()
			throws InstanceNotFoundException, MinimumBidPriceException,
			ExpiredDateException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;

		Category category = null;

		Product product1 = null;

		try {
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", userProfile.getUserProfileId(),
					category.getCategoryId());

		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		Bid bid2 = bidService.createBid(userProfile2.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

	}

	@Test
	public void testIncreaseBid() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException {

		UserProfile userProfile = null;

		Category category = null;

		Product product1 = null;

		try {
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", userProfile.getUserProfileId(),
					category.getCategoryId());

		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		Bid bid2 = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(12), product1.getProductId());

		assertEquals(product1.getCurrentBid().getUser().getEmail(),
				"user@udc.es");

		assertEquals(product1.getCurrentPrice(), new BigDecimal(10.0));

	}

}
