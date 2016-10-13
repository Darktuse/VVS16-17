package es.udc.subasta.model.product;

@SuppressWarnings("serial")
public class ExpiredDateException extends Exception {

    private Long productId;

    public ExpiredDateException(Long productId) {
        super("Date expired to view the product => productId = " + productId);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
	
}
