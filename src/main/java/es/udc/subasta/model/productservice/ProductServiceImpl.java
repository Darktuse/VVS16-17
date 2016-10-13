package es.udc.subasta.model.productservice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.category.CategoryDao;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductBlock;
import es.udc.subasta.model.product.ProductDao;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userprofile.UserProfileDao;

@Service("productService")
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao productDao;

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private CategoryDao categoryDao;

	@Override
	public Product insertAd(String name, String description,
			BigDecimal startingPrice, Calendar endingDate,
			String deliveryInformation, Long ownerId, Long categoryId)
			throws DatesException, InstanceNotFoundException {

		Calendar now = Calendar.getInstance();
		if (now.after(endingDate)) {
			throw new DatesException(now);
		}

		UserProfile owner = userProfileDao.find(ownerId);

		Category category = categoryDao.find(categoryId);

		Product product = new Product(name, description, startingPrice, now,
				endingDate, deliveryInformation, null, owner, category, null);

		productDao.save(product);

		return product;
	}

	@Transactional(readOnly = true)
	public List<Bid> findBidsByProductId(Long productId)
			throws InstanceNotFoundException {
		productDao.find(productId);

		return productDao.findBidsByProductId(productId);
	}

	@Transactional(readOnly = true)
	public Product viewProductDetails(Long productId)
			throws InstanceNotFoundException, ExpiredDateException {

		try {
			Product product = productDao.find(productId);

			Calendar actualTime = Calendar.getInstance();

			if (actualTime.after(product.getEndingDate())) {
				throw new ExpiredDateException(productId);
			}

			return product;

		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundException(productId,
					Product.class.getName());
		}

	}

	@Transactional(readOnly = true)
	public Product findProduct(Long productId) throws InstanceNotFoundException {
		return productDao.find(productId);

	}

	@Transactional(readOnly = true)
	public ProductBlock findProductsByKeywordsCategory(String keywords,
			Long categoryId, int startIndex, int count) {

		List<Product> products = productDao.findProductsByKeywordsCategory(
				keywords, categoryId, startIndex, count + 1);

		boolean existMoreProducts = products.size() == (count + 1);

		if (existMoreProducts) {
			products.remove(products.size() - 1);

		}
		return new ProductBlock(products, existMoreProducts);

	}

	@Override
	public List<Product> getAllProducts() {
		return productDao.getAll();
	}

}
