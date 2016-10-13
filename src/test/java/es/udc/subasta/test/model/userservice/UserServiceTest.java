package es.udc.subasta.test.model.userservice;

import static es.udc.subasta.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.subasta.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bid.BidBlock;
import es.udc.subasta.model.bidservice.BidService;
import es.udc.subasta.model.bidservice.MinimumBidPriceException;
import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.category.CategoryDao;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductBlock;
import es.udc.subasta.model.productservice.DatesException;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userservice.IncorrectPasswordException;
import es.udc.subasta.model.userservice.UserProfileDetails;
import es.udc.subasta.model.userservice.UserService;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class UserServiceTest {

	private final long NON_EXISTENT_USER_PROFILE_ID = -1;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private ProductService productService;

	@Autowired
	private BidService bidService;

	@Test
	public void testRegisterUserAndFindUserProfile()
			throws DuplicateInstanceException, InstanceNotFoundException {

		/* Register user and find profile. */
		UserProfile userProfile = userService.registerUser("user",
				"userPassword", new UserProfileDetails("name", "lastName",
						"user@udc.es"));

		UserProfile userProfile2 = userService.findUserProfile(userProfile
				.getUserProfileId());

		/* Check data. */
		assertEquals(userProfile, userProfile2);

	}

	@Test(expected = DuplicateInstanceException.class)
	public void testRegisterDuplicatedUser() throws DuplicateInstanceException,
			InstanceNotFoundException {

		String loginName = "user";
		String clearPassword = "userPassword";
		UserProfileDetails userProfileDetails = new UserProfileDetails("name",
				"lastName", "user@udc.es");

		userService.registerUser(loginName, clearPassword, userProfileDetails);

		userService.registerUser(loginName, clearPassword, userProfileDetails);

	}

	@Test
	public void testLoginClearPassword() throws IncorrectPasswordException,
			InstanceNotFoundException {

		String clearPassword = "userPassword";
		UserProfile userProfile = registerUser("user", clearPassword);

		UserProfile userProfile2 = userService.login(
				userProfile.getLoginName(), clearPassword, false);

		assertEquals(userProfile, userProfile2);

	}

	@Test
	public void testLoginEncryptedPassword() throws IncorrectPasswordException,
			InstanceNotFoundException {

		UserProfile userProfile = registerUser("user", "clearPassword");

		UserProfile userProfile2 = userService.login(
				userProfile.getLoginName(), userProfile.getEncryptedPassword(),
				true);

		assertEquals(userProfile, userProfile2);

	}

	@Test(expected = IncorrectPasswordException.class)
	public void testLoginIncorrectPasword() throws IncorrectPasswordException,
			InstanceNotFoundException {

		String clearPassword = "userPassword";
		UserProfile userProfile = registerUser("user", clearPassword);

		userService.login(userProfile.getLoginName(), 'X' + clearPassword,
				false);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testLoginWithNonExistentUser()
			throws IncorrectPasswordException, InstanceNotFoundException {

		userService.login("user", "userPassword", false);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testFindNonExistentUser() throws InstanceNotFoundException {

		userService.findUserProfile(NON_EXISTENT_USER_PROFILE_ID);

	}

	@Test
	public void testUpdate() throws InstanceNotFoundException,
			IncorrectPasswordException {

		/* Update profile. */
		String clearPassword = "userPassword";
		UserProfile userProfile = registerUser("user", clearPassword);

		UserProfileDetails newUserProfileDetails = new UserProfileDetails(
				'X' + userProfile.getFirstName(),
				'X' + userProfile.getLastName(), 'X' + userProfile.getEmail());

		userService.updateUserProfileDetails(userProfile.getUserProfileId(),
				newUserProfileDetails);

		/* Check changes. */
		userService.login(userProfile.getLoginName(), clearPassword, false);
		UserProfile userProfile2 = userService.findUserProfile(userProfile
				.getUserProfileId());

		assertEquals(newUserProfileDetails.getFirstName(),
				userProfile2.getFirstName());
		assertEquals(newUserProfileDetails.getLastName(),
				userProfile2.getLastName());
		assertEquals(newUserProfileDetails.getEmail(), userProfile2.getEmail());

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testUpdateWithNonExistentUser()
			throws InstanceNotFoundException {

		userService.updateUserProfileDetails(NON_EXISTENT_USER_PROFILE_ID,
				new UserProfileDetails("name", "lastName", "user@udc.es"));

	}

	@Test
	public void testChangePassword() throws InstanceNotFoundException,
			IncorrectPasswordException {

		/* Change password. */
		String clearPassword = "userPassword";
		UserProfile userProfile = registerUser("user", clearPassword);
		String newClearPassword = 'X' + clearPassword;

		userService.changePassword(userProfile.getUserProfileId(),
				clearPassword, newClearPassword);

		/* Check new password. */
		userService.login(userProfile.getLoginName(), newClearPassword, false);

	}

	@Test(expected = IncorrectPasswordException.class)
	public void testChangePasswordWithIncorrectPassword()
			throws InstanceNotFoundException, IncorrectPasswordException {

		String clearPassword = "userPassword";
		UserProfile userProfile = registerUser("user", clearPassword);

		userService.changePassword(userProfile.getUserProfileId(),
				'X' + clearPassword, 'Y' + clearPassword);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testChangePasswordWithNonExistentUser()
			throws InstanceNotFoundException, IncorrectPasswordException {

		userService.changePassword(NON_EXISTENT_USER_PROFILE_ID,
				"userPassword", "XuserPassword");

	}

	/**
	 * QueryStateBidsByUser show the bids state by user
	 */

	@Test
	public void queryStateBidsByUser() throws DuplicateInstanceException,
			InstanceNotFoundException, IncorrectPasswordException,
			DatesException, MinimumBidPriceException, ExpiredDateException {

		/* This is an array which will contain the expected result */
		List<Bid> expectedBidsByUser = new ArrayList<Bid>();

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");

		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.YEAR, 1);
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, 1);

		/* Create a user who has created the Advert */
		UserProfile AdvertUser1 = createUsuario("User1", "userPassword");
		UserProfile AdverUser2 = createUsuario("User2", "userPassword");

		/* create the profile who see the state of bids */

		UserProfile userProfile = registerUser("owner", "userPassword");
		UserProfile authenticatedUser = userService.login(
				userProfile.getLoginName(), userProfile.getEncryptedPassword(),
				true);

		Product product1 = productService
				.insertAd("iPad Mini 3", "This is a product for the test",
						new BigDecimal(10.0), cal1, "NA",
						AdvertUser1.getUserProfileId(),
						category1.getCategoryId());

		Product product2 = productService.insertAd("Apple iPhone 6 Plus",
				"This is a product for the test", new BigDecimal(10.0), cal2,
				"NA", AdverUser2.getUserProfileId(), category2.getCategoryId());

		/* owner bids */
		Bid bid1 = bidService.createBid(authenticatedUser.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());
		expectedBidsByUser.add(bid1);

		Bid bid2 = bidService.createBid(authenticatedUser.getUserProfileId(),
				new BigDecimal(12.0), product1.getProductId());
		expectedBidsByUser.add(bid2);

		Bid bid3 = bidService.createBid(authenticatedUser.getUserProfileId(),
				new BigDecimal(10.0), product2.getProductId());
		expectedBidsByUser.add(bid3);

		/* Other user bids */
		bidService.createBid(AdverUser2.getUserProfileId(), new BigDecimal(
				11.00), product2.getProductId());

		bidService.createBid(AdvertUser1.getUserProfileId(), new BigDecimal(
				13.00), product1.getProductId());

		/* Sort expectedBidsByUser in DESC way */
		Collections.sort(expectedBidsByUser, BidComparator);

		/* Find bids by Owner user and check with expectedBidsByUser */
		BidBlock bidBlock;
		int count = 5;
		int startIndex = 0;
		short resultIndex = 0;

		do {
			bidBlock = userService.findBidsByUserId(
					authenticatedUser.getUserProfileId(), startIndex, count);

			assertTrue(bidBlock.getBids().size() <= count);
			for (Bid bid : bidBlock.getBids()) {
				assertTrue(bid == expectedBidsByUser.get(resultIndex++));

			}
			startIndex += count;
		} while (bidBlock.getExistMoreBids());

		assertTrue(expectedBidsByUser.size() == startIndex - count
				+ bidBlock.getBids().size());

	}

	@Test
	public void queryStateBidsEmpty() throws InstanceNotFoundException,
			DuplicateInstanceException, IncorrectPasswordException,
			DatesException, MinimumBidPriceException, ExpiredDateException {

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");

		Calendar endigDate1 = Calendar.getInstance();
		endigDate1.add(Calendar.MONTH, 5);

		/* Create users to create other bids */

		UserProfile advertUser1 = createUsuario("User1", "userPassword");
		UserProfile advertUser2 = createUsuario("User2", "userPassword");
		UserProfile bidUser1 = createUsuario("Userb1", "userPassword");
		UserProfile bidUser2 = createUsuario("Userb2", "userPassword");

		/* create the owner profile who see the state of bids */
		UserProfile userProfile = registerUser("owner", "userPassword");
		UserProfile authenticatedUser = userService.login(
				userProfile.getLoginName(), userProfile.getEncryptedPassword(),
				true);

		/* The bids are created by other users */
		Product product1 = productService.insertAd("iPad Mini 3",
				"This is a product for the test", new BigDecimal(10.0),
				endigDate1, "NA", advertUser1.getUserProfileId(),
				category1.getCategoryId());

		endigDate1.add(Calendar.MONTH, 3);
		endigDate1.add(Calendar.YEAR, 10);

		Product product2 = productService.insertAd("Apple iPhone 6 Plus",
				"This is a product for the test", new BigDecimal(10.0),
				endigDate1, "NA", advertUser2.getUserProfileId(),
				category2.getCategoryId());

		bidService.createBid(bidUser1.getUserProfileId(), new BigDecimal(10.0),
				product1.getProductId());

		bidService.createBid(bidUser2.getUserProfileId(), new BigDecimal(12.0),
				product1.getProductId());

		bidService.createBid(bidUser1.getUserProfileId(), new BigDecimal(10.0),
				product2.getProductId());

		bidService.createBid(bidUser2.getUserProfileId(),
				new BigDecimal(11.00), product2.getProductId());

		bidService.createBid(bidUser1.getUserProfileId(),
				new BigDecimal(13.00), product1.getProductId());

		/* The result must be empty because the bid was create by other users */

		assertEquals(
				userService
						.findBidsByUserId(authenticatedUser.getUserProfileId(),
								0, 5).getBids().size(), 0);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void queryStateBidsNotExistUser() throws InstanceNotFoundException,
			DuplicateInstanceException, IncorrectPasswordException,
			DatesException, MinimumBidPriceException, ExpiredDateException {

		userService.findBidsByUserId(NON_EXISTENT_USER_PROFILE_ID, 0, 5);

	}

	@Test
	public void queryAdvertisedProductsByUser()
			throws DuplicateInstanceException, InstanceNotFoundException,
			IncorrectPasswordException, DatesException {
		/* This is an array which will contain the expected result */
		List<Product> expectedAdviceProducts = new ArrayList<Product>();

		/* Create a owner user */
		UserProfile userProfile = registerUser("owner", "userPassword");
		UserProfile authenticatedUser = userService.login(
				userProfile.getLoginName(), userProfile.getEncryptedPassword(),
				true);

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");
		Category category3 = createCategory("Computer");

		Calendar endingDate1 = Calendar.getInstance();
		endingDate1.set(Calendar.YEAR, 2017);
		Calendar endingDate2 = Calendar.getInstance();
		endingDate2.set(Calendar.YEAR, 2017);
		Calendar endingDate3 = Calendar.getInstance();
		endingDate3.set(Calendar.YEAR, 2018);

		/* Create some Products by owner user */
		Product product1 = createProduct("iPad Mini 3", "test of prueba 1",
				new BigDecimal(10.0), endingDate1, "NA",
				authenticatedUser.getUserProfileId(), category1.getCategoryId());
		expectedAdviceProducts.add(product1);

		Product product2 = createProduct("iPad Air 2 ", "description",
				new BigDecimal(10.0), endingDate2, "NA",
				authenticatedUser.getUserProfileId(), category1.getCategoryId());
		expectedAdviceProducts.add(product2);

		Product product3 = createProduct("Apple iPhone 6 Plus",
				"This is a special product", new BigDecimal(10.0), endingDate3,
				"NA", authenticatedUser.getUserProfileId(),
				category2.getCategoryId());
		expectedAdviceProducts.add(product3);

		Product product4 = createProduct("Apple MacBook Air", "description",
				new BigDecimal(10.0), endingDate2, "NA",
				authenticatedUser.getUserProfileId(), category3.getCategoryId());
		expectedAdviceProducts.add(product4);

		/* Sort the array by ending date in desc way */

		Collections.sort(expectedAdviceProducts, ProductComparator);

		/* Check the result of findAdvertiseProductsByUserId */
		ProductBlock productBlock;
		int count = 4;
		int startIndex = 0;
		short resultIndex = 0;

		do {

			productBlock = userService.findAdvertisedProductsByUserId(
					authenticatedUser.getUserProfileId(), startIndex, count);

			assertTrue(productBlock.getProducts().size() <= count);

			for (Product product : productBlock.getProducts()) {
				assertTrue(product == expectedAdviceProducts.get(resultIndex++));
			}
			startIndex += count;
		} while (productBlock.getExistMoreProducts());

		assertTrue(expectedAdviceProducts.size() == startIndex - count
				+ productBlock.getProducts().size());
	}

	public void queryStateAdvertNotExistUser()
			throws InstanceNotFoundException, DuplicateInstanceException,
			IncorrectPasswordException, DatesException,
			MinimumBidPriceException, ExpiredDateException {

		userService.findAdvertisedProductsByUserId(
				NON_EXISTENT_USER_PROFILE_ID, 0, 5);

	}

	private Product createProduct(String name, String description,
			BigDecimal startingPrice, Calendar endingDate,
			String deliveryInformation, Long ownerId, Long categoryId)
			throws InstanceNotFoundException, DatesException {

		return productService.insertAd(name, description, startingPrice,
				endingDate, deliveryInformation, ownerId, categoryId);
	}

	private UserProfile createUsuario(String user, String password)
			throws DuplicateInstanceException {
		return userService.registerUser(user, password, new UserProfileDetails(
				"name", "lastName", "user@udc.es"));
	}

	private Category createCategory(String typeOfCategory) {
		Category category = new Category(typeOfCategory);
		categoryDao.save(category);
		return category;

	}

	/*
	 * Order by more time to conclude his term bid -> sort by desc
	 */

	public static Comparator<Product> ProductComparator = new Comparator<Product>() {
		public int compare(Product p1, Product p2) {

			return p2.getEndingDate().compareTo(p1.getEndingDate());
		}
	};

	/*
	 * Order by Latest Bids -> 
	 */

	public static Comparator<Bid> BidComparator = new Comparator<Bid>() {
		public int compare(Bid bid1, Bid bid2) {
			return bid2.getDate().compareTo(bid1.getDate());
		}
	};

	private UserProfile registerUser(String loginName, String clearPassword) {

		UserProfileDetails userProfileDetails = new UserProfileDetails("name",
				"lastName", "user@udc.es");

		try {

			return userService.registerUser(loginName, clearPassword,
					userProfileDetails);

		} catch (DuplicateInstanceException e) {
			throw new RuntimeException(e);
		}

	}

}
