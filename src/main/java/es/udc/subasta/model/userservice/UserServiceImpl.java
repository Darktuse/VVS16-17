package es.udc.subasta.model.userservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bid.BidBlock;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductBlock;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userprofile.UserProfileDao;
import es.udc.subasta.model.userservice.util.PasswordEncrypter;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserProfileDao userProfileDao;

	public UserProfile registerUser(String loginName, String clearPassword,
			UserProfileDetails userProfileDetails)
			throws DuplicateInstanceException {

		try {
			userProfileDao.findByLoginName(loginName);
			throw new DuplicateInstanceException(loginName,
					UserProfile.class.getName());
		} catch (InstanceNotFoundException e) {
			String encryptedPassword = PasswordEncrypter.crypt(clearPassword);

			UserProfile userProfile = new UserProfile(loginName,
					encryptedPassword, userProfileDetails.getFirstName(),
					userProfileDetails.getLastName(),
					userProfileDetails.getEmail());

			userProfileDao.save(userProfile);
			return userProfile;
		}

	}

	@Transactional(readOnly = true)
	public UserProfile login(String loginName, String password,
			boolean passwordIsEncrypted) throws InstanceNotFoundException,
			IncorrectPasswordException {

		UserProfile userProfile = userProfileDao.findByLoginName(loginName);
		String storedPassword = userProfile.getEncryptedPassword();

		if (passwordIsEncrypted) {
			if (!password.equals(storedPassword)) {
				throw new IncorrectPasswordException(loginName);
			}
		} else {
			if (!PasswordEncrypter.isClearPasswordCorrect(password,
					storedPassword)) {
				throw new IncorrectPasswordException(loginName);
			}
		}
		return userProfile;

	}

	@Transactional(readOnly = true)
	public UserProfile findUserProfile(Long userProfileId)
			throws InstanceNotFoundException {

		return userProfileDao.find(userProfileId);
	}

	public void updateUserProfileDetails(Long userProfileId,
			UserProfileDetails userProfileDetails)
			throws InstanceNotFoundException {

		UserProfile userProfile = userProfileDao.find(userProfileId);
		userProfile.setFirstName(userProfileDetails.getFirstName());
		userProfile.setLastName(userProfileDetails.getLastName());
		userProfile.setEmail(userProfileDetails.getEmail());

	}

	public void changePassword(Long userProfileId, String oldClearPassword,
			String newClearPassword) throws IncorrectPasswordException,
			InstanceNotFoundException {

		UserProfile userProfile;
		userProfile = userProfileDao.find(userProfileId);

		String storedPassword = userProfile.getEncryptedPassword();

		if (!PasswordEncrypter.isClearPasswordCorrect(oldClearPassword,
				storedPassword)) {
			throw new IncorrectPasswordException(userProfile.getLoginName());
		}

		userProfile.setEncryptedPassword(PasswordEncrypter
				.crypt(newClearPassword));

	}

	@Transactional(readOnly = true)
	public BidBlock findBidsByUserId(Long userId, int startIndex, int count)
			throws InstanceNotFoundException {

		userProfileDao.find(userId);
		List<Bid> bids = userProfileDao.findBidsByUserId(userId, startIndex,
				count + 1);

		boolean existMoreBids = bids.size() == (count + 1);

		if (existMoreBids) {
			bids.remove(bids.size() - 1);

		}
		return new BidBlock(bids, existMoreBids);

	}

	@Transactional(readOnly = true)
	public ProductBlock findAdvertisedProductsByUserId(Long userId,
			int startIndex, int count) throws InstanceNotFoundException {

		userProfileDao.find(userId);
		List<Product> products = userProfileDao.findAdvertisedProductsByUserId(
				userId, startIndex, count + 1);

		boolean existMoreProducts = products.size() == (count + 1);

		if (existMoreProducts) {
			products.remove(products.size() - 1);

		}
		return new ProductBlock(products, existMoreProducts);

	}

}
