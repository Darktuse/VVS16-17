package es.udc.subasta.model.product;

import java.util.List;

public class ProductBlock {

	private List<Product> Products;
	private boolean existMoreProducts;

	public ProductBlock(List<Product> Products, boolean existMoreProducts) {

		this.Products = Products;
		this.existMoreProducts = existMoreProducts;

	}

	public List<Product> getProducts() {
		return Products;
	}

	public boolean getExistMoreProducts() {
		return existMoreProducts;
	}
}
