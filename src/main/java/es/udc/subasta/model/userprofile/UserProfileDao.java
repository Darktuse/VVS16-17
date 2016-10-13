package es.udc.subasta.model.userprofile;

import java.util.List;

import es.udc.pojo.modelutil.dao.GenericDao;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.product.Product;

public interface UserProfileDao extends GenericDao<UserProfile, Long> {

	/**
	 * Returns an UserProfile by login name (not user identifier)
	 *
	 * @param loginName
	 *            the user identifier
	 * @return the UserProfile
	 */
	public UserProfile findByLoginName(String loginName)
			throws InstanceNotFoundException;

	public List<Bid> findBidsByUserId(Long userId, int startIndex, int count);

	public List<Product> findAdvertisedProductsByUserId(Long userId,
			int startIndex, int count);
}
