package es.udc.subasta.model.category;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
/** @Table(name="Category") It is not necessary because the entity Category has the same name that table Category*/

public class Category {
	
	private Long categoryId;
	private String name;
	

	public Category() {
		/**
		 * A persistent class should has a empty constructor.
		 **/
	}
	
	public Category(String name) {
		/**
		 * NOTE: "categoryId" *must* be left as "null" since its value is
		 * automatically generated.
		 */
		this.name = name;
	}
	@Column(name="categoryId")
	@SequenceGenerator(name="categoryIdGenerator", sequenceName="categorySequence")
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO, generator = "categoryIdGenerator")
	public Long getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	/** @Column (name = "name") In this case is not necessary neither because the atribute name has the same name that column name in category.*/
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
