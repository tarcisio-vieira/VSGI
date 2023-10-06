package br.com.vsgi.core.domain.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vsgi_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel extends RepresentationModel<ProductModel>  implements Serializable {

	/**
	 * long serialVersionUID
	 */	
	private static final long serialVersionUID = -2536233679470348316L;

	/**
	 * Long vsgi_product_id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vsgi_product_seq")
	@SequenceGenerator(name="vsgi_product_seq", sequenceName = "vsgi_product_id_seq", initialValue=1000001, allocationSize=1)
    private Long vsgi_product_id;
	
	/**
	 * UUID vsgi_product_uuid
	 */
	private UUID vsgi_product_uuid;
	
	/**
	 * boolean isactive
	 */
	@NotNull
	@Builder.Default
	private boolean isactive = true;

	/**
	 * Long vsgi_client_id
	 */
	private Long vsgi_client_id;

	/**
	 * Long vsgi_org_id
	 */
	private Long vsgi_org_id;
	
	/**
	 * String name
	 */
	private String name;
	
	/**
	 * String description
	 */
	private String description;

	/**
	 * Long createdby
	 */
	private Long createdby;

	/**
	 * String created
	 */
	@CreationTimestamp
	private String created;

	/**
	 * Long updatedby
	 */
	private Long updatedby;

	/**
	 * String updated
	 */
	@UpdateTimestamp
	private String updated;
	
	/**
	 * BigDecimal value
	 */
	private BigDecimal value;

	/**
	 * @return the vsgi_product_id
	 */
	public Long getVsgi_product_id() {
		return vsgi_product_id;
	}

	/**
	 * @param vsgi_product_id the vsgi_product_id to set
	 */
	public void setVsgi_product_id(Long vsgi_product_id) {
		this.vsgi_product_id = vsgi_product_id;
	}

	/**
	 * @return the vsgi_product_uuid
	 */
	public UUID getVsgi_product_uuid() {
		return vsgi_product_uuid;
	}

	/**
	 * @param vsgi_product_uuid the vsgi_product_uuid to set
	 */
	public void setVsgi_product_uuid(UUID vsgi_product_uuid) {
		this.vsgi_product_uuid = vsgi_product_uuid;
	}

	/**
	 * @return the isactive
	 */
	public boolean isIsactive() {
		return isactive;
	}

	/**
	 * @param isactive the isactive to set
	 */
	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}

	/**
	 * @return the vsgi_client_id
	 */
	public Long getVsgi_client_id() {
		return vsgi_client_id;
	}

	/**
	 * @param vsgi_client_id the vsgi_client_id to set
	 */
	public void setVsgi_client_id(Long vsgi_client_id) {
		this.vsgi_client_id = vsgi_client_id;
	}

	/**
	 * @return the vsgi_org_id
	 */
	public Long getVsgi_org_id() {
		return vsgi_org_id;
	}

	/**
	 * @param vsgi_org_id the vsgi_org_id to set
	 */
	public void setVsgi_org_id(Long vsgi_org_id) {
		this.vsgi_org_id = vsgi_org_id;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the createdby
	 */
	public Long getCreatedby() {
		return createdby;
	}

	/**
	 * @param createdby the createdby to set
	 */
	public void setCreatedby(Long createdby) {
		this.createdby = createdby;
	}

	/**
	 * @return the created
	 */
	public String getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(String created) {
		this.created = created;
	}

	/**
	 * @return the updatedby
	 */
	public Long getUpdatedby() {
		return updatedby;
	}

	/**
	 * @param updatedby the updatedby to set
	 */
	public void setUpdatedby(Long updatedby) {
		this.updatedby = updatedby;
	}

	/**
	 * @return the updated
	 */
	public String getUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(String updated) {
		this.updated = updated;
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
