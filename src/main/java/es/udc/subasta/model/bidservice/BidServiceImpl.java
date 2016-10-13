package es.udc.subasta.model.bidservice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.hibernate.metamodel.relational.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bid.BidDao;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductDao;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userprofile.UserProfileDao;

@Service("bidService")
@Transactional
public class BidServiceImpl implements BidService {

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private BidDao bidDao;

	@Override
	public Bid createBid(Long bidUserId, BigDecimal amount, Long productId)
			throws MinimumBidPriceException, InstanceNotFoundException,
			ExpiredDateException {

		Product product = productDao.find(productId);
		Bid currentBid = product.getCurrentBid();
		BigDecimal currentPrice = product.getCurrentPrice();
		UserProfile owner = product.getOwner();
		UserProfile userBid = userProfileDao.find(bidUserId);
		UserProfile currentWinnerProduct = null;

		if (product.getCurrentBid() != null)
			currentWinnerProduct = product.getCurrentBid().getUser();

		if (currentWinnerProduct != null) {
			// AUMENTAR PUJA
			if (currentWinnerProduct.getUserProfileId() == bidUserId) {

				Calendar now = Calendar.getInstance();
				if (now.after(product.getEndingDate())) {
					throw new ExpiredDateException(productId);
				}
				Bid bid = new Bid(userBid, amount, now, currentPrice,
						currentWinnerProduct, product);
				product.setCurrentBid(bid);
				bidDao.save(bid);
				return bid;
				// SOBREPUJAR
			} else {

				currentPrice = product.getCurrentPrice();
				BigDecimal MinimumIncrementBid = new BigDecimal(0.5);
				if (currentPrice.add(MinimumIncrementBid).compareTo(amount) == 1) {
					throw new MinimumBidPriceException(currentBid.getBidId());
				}

				if (amount.compareTo(currentBid.getAmount().add(
						MinimumIncrementBid)) == 1) {
					product.setCurrentPrice(currentBid.getAmount().add(
							MinimumIncrementBid));
				} else {
					product.setCurrentPrice(amount);
				}

			}
			// PUJAR POR PRIMERA VEZ
		} else {
			currentPrice = product.getStartingPrice();
			if (currentPrice.compareTo(amount) == 1) {
				throw new MinimumBidPriceException(currentBid.getBidId());
			}
			product.setCurrentPrice(amount);
		}

		Calendar now = Calendar.getInstance();
		if (now.after(product.getEndingDate())) {
			throw new ExpiredDateException(productId);
		}

		Bid bid = new Bid(userBid, amount, now, currentPrice,
				currentWinnerProduct, product);
		product.setCurrentBid(bid);
		bidDao.save(bid);
		return bid;

	}

	@Transactional(readOnly = true)
	public Bid findBid(Long bidId) throws InstanceNotFoundException {

		return bidDao.find(bidId);
	}
}
