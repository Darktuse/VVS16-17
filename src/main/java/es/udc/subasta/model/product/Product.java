package es.udc.subasta.model.product;

import java.math.BigDecimal;
import java.util.Calendar;

import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.userprofile.UserProfile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
public class Product {
	private Long productId;
	private String name;
	private String description;
	private BigDecimal startingPrice;
	private Calendar startingDate;
	private Calendar endingDate;
	private String deliveryInformation;
	private BigDecimal currentPrice;
	private UserProfile owner;
	private Category category;
	private Bid currentBid;
	private long version;

	public Product() {
		/**
		 * A persistent class should has a empty constructor.
		 **/
	}

	public Product(String name, String description, BigDecimal startingPrice,
			Calendar startingDate, Calendar endingDate,
			String deliveryInformation, BigDecimal currentPrice,
			UserProfile owner, Category category, Bid currentBid) {
		/**
		 * NOTE: "productId" *must* be left as "null" since its value is
		 * automatically generated.
		 */

		this.name = name;
		this.description = description;
		this.startingPrice = startingPrice;
		this.startingDate = startingDate;
		this.endingDate = endingDate;
		this.deliveryInformation = deliveryInformation;
		this.currentPrice = currentPrice;
		this.owner = owner;
		this.category = category;
		this.currentBid = currentBid;
	}

	@Column(name = "productId")
	@SequenceGenerator( // It only takes effect for
	name = "productIdGenerator", // databases providing identifier
	sequenceName = "productSeq")
	// generators.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "productIdGenerator")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getStartingPrice() {
		return startingPrice;
	}

	public void setStartingPrice(BigDecimal startingPrice) {
		this.startingPrice = startingPrice;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Calendar startingDate) {
		this.startingDate = startingDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getEndingDate() {
		return endingDate;
	}

	public void setEndingDate(Calendar endingDate) {
		this.endingDate = endingDate;
	}

	public String getDeliveryInformation() {
		return deliveryInformation;
	}

	public void setDeliveryInformation(String deliveryInformation) {
		this.deliveryInformation = deliveryInformation;
	}

	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	public UserProfile getOwner() {
		return owner;
	}

	public void setOwner(UserProfile owner) {
		this.owner = owner;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId")
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "currentBidId")
	public Bid getCurrentBid() {
		return currentBid;
	}

	public void setCurrentBid(Bid currentBid) {
		this.currentBid = currentBid;
	}

	@Version
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Transient
	public double getRemainingMinutes() {

		// Cálculo de la diferencia de tiempo
		long milliSec1 = Calendar.getInstance().getTimeInMillis();
		long milliSec2 = this.getEndingDate().getTimeInMillis();

		long timeDifInMilliSec;
		if (milliSec1 >= milliSec2) {
			timeDifInMilliSec = milliSec1 - milliSec2;
		} else {
			timeDifInMilliSec = milliSec2 - milliSec1;
		}

		return timeDifInMilliSec / (60 * 1000);

	}

}
