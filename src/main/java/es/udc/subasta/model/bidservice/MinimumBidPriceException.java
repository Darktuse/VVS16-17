package es.udc.subasta.model.bidservice;

@SuppressWarnings("serial")
public class MinimumBidPriceException extends Exception {

    private Long bidId;

    public MinimumBidPriceException(Long bidId) {
        super("Minimum bid price => bidId = " + bidId);
        this.bidId = bidId;
    }

    public Long getBidId() {
        return bidId;
    }
    
}
