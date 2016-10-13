package es.udc.subasta.model.product;

import java.util.List;

import es.udc.pojo.modelutil.dao.GenericDao;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;

public interface ProductDao extends GenericDao<Product, Long> {

	public List<Product> findByName(String name)
			throws InstanceNotFoundException;

	public List<Bid> findBidsByProductId(Long productId);

	public List<Product> findProductsByKeywordsCategory(String keywords,
			Long categoryId, int startIndex, int count);
	
	public List<Product> getAll();
}