package es.udc.subasta.model.bid;

import java.util.List;

public class BidBlock {

	private List<Bid> Bid;
	private boolean existMoreBids;

	public BidBlock(List<Bid> Bids, boolean existMoreBids) {

		this.Bid = Bids;
		this.existMoreBids = existMoreBids;

	}

	public List<Bid> getBids() {
		return Bid;
	}

	public boolean getExistMoreBids() {
		return existMoreBids;
	}
}
