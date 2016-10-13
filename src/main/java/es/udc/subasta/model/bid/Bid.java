package es.udc.subasta.model.bid;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.userprofile.UserProfile;

@Entity
@Immutable
/**
 * Para los casos en los que tienen el mismo nombre la clase y la tabla en la BD
 * no es necesario poner esta anotacion
 **/
@Table(name = "Bid")
public class Bid {
	private Long bidId;
	private UserProfile user;
	private BigDecimal amount;
	private Calendar date;
	private BigDecimal currentPriceProduct;
	private UserProfile currentWinnerProduct;
	private Product product;

	public Bid() {
		/**
		 * A persistent class should has a empty constructor.
		 **/
	}

	public Bid(UserProfile user, BigDecimal amount, Calendar date,
			BigDecimal currentPriceProduct, UserProfile currentWinnerProduct,
			Product product) {
		/**
		 * NOTE: "bidId" *must* be left as "null" since its value is
		 * automatically generated.
		 */

		this.user = user;
		this.amount = amount;
		this.date = date;
		this.currentPriceProduct = currentPriceProduct;
		this.currentWinnerProduct = currentWinnerProduct;
		this.product = product;
	}

	@Column(name = "bidId")
	@SequenceGenerator( // It only takes effect for
	name = "BidIdGenerator", // databases providing identifier
	sequenceName = "BidSeq")
	// generators.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "BidIdGenerator")
	public Long getBidId() {
		return bidId;
	}

	public void setBidId(Long bidId) {
		this.bidId = bidId;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "userBidId")
	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateInit")
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public BigDecimal getCurrentPriceProduct() {
		return currentPriceProduct;
	}

	public void setCurrentPriceProduct(BigDecimal currentPriceProduct) {
		this.currentPriceProduct = currentPriceProduct;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "currentWinnerProductId")
	public UserProfile getCurrentWinnerProduct() {
		return currentWinnerProduct;
	}

	public void setCurrentWinnerProduct(UserProfile currentWinnerProduct) {
		this.currentWinnerProduct = currentWinnerProduct;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "productId")
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
