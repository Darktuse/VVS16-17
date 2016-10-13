package es.udc.subasta.model.userprofile;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.product.Product;

@Repository("userProfileDao")
public class UserProfileDaoHibernate extends
		GenericDaoHibernate<UserProfile, Long> implements UserProfileDao {

	public UserProfile findByLoginName(String loginName)
			throws InstanceNotFoundException {

		UserProfile userProfile = (UserProfile) getSession()
				.createQuery(
						"SELECT u FROM UserProfile u WHERE u.loginName = :loginName")
				.setParameter("loginName", loginName).uniqueResult();
		if (userProfile == null) {
			throw new InstanceNotFoundException(loginName,
					UserProfile.class.getName());
		} else {
			return userProfile;
		}

	}

	/*
	 * Este metodo no comprueba si el usuario existe, necesario comprobarlo en
	 * el servicio
	 * 
	 * @see
	 * es.udc.subasta.model.userprofile.UserProfileDao#findBidsByUserId(java
	 * .lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Bid> findBidsByUserId(Long userId, int startIndex, int count) {

		String findQuery = "SELECT u FROM Bid u WHERE u.user.userProfileId = :userId "
				+ "ORDER BY u DESC";
		Query query = getSession().createQuery(findQuery);
		query.setParameter("userId", userId);
		query.setFirstResult(startIndex);
		query.setMaxResults(count);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Product> findAdvertisedProductsByUserId(Long userId,
			int startIndex, int count) {

		String findQuery = "SELECT u FROM Product u WHERE u.owner.userProfileId = :userId "
				+ "ORDER BY u.endingDate DESC";

		Query query = getSession().createQuery(findQuery);
		query.setParameter("userId", userId);
		query.setFirstResult(startIndex);
		query.setMaxResults(count);
		return query.list();
	}

}