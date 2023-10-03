package br.com.vsgi.core.domain.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_PRODUCTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idProduct")
public class ProductModel extends RepresentationModel<ProductModel>  implements Serializable {
	private static final long serialVersionUID = -5843186687915272030L;
	
	/**
	 * UUID idProduct
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID idProduct;
	
	/**
	 * String name
	 */
	private String name;
	
	/**
	 * BigDecimal value
	 */
	private BigDecimal value;
	
	/**
	 * @return the idProduct
	 */
	public UUID getIdProduct() {
		return idProduct;
	}
	/**
	 * @param idProduct the idProduct to set
	 */
	public void setIdProduct(UUID idProduct) {
		this.idProduct = idProduct;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public BigDecimal getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
