package br.com.vsgi.core.domain.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Entity
@Table(name = "vsgi_user")
public class UserModel extends RepresentationModel<UserModel> implements Serializable, UserDetails {

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 4313934384680406993L;	

	/**
	 * Long vsgi_user_id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vsgi_user_seq")
	@SequenceGenerator(name="vsgi_user_seq", sequenceName = "vsgi_user_id_seq", initialValue=1000001, allocationSize=1)
    private Long vsgi_user_id;
	
	/**
	 * UUID vsgi_user_uuid
	 */
	private UUID vsgi_user_uuid;		

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
	 * String login
	 */
	private String login;
	
	/**
	 * String password
	 */
	private String password;

	/**
	 * String role
	 */
	private UserRole role;	

	/**
	 * No Args Constructor
	 */
	public UserModel() {		
	}
	
	/**
	 * @param login
	 * @param password
	 * @param role
	 */
	public UserModel(String login, String password, UserRole role, UUID vsgi_user_uuid) {
		this.login = login;
		this.password = password;
		this.role = role;
		this.vsgi_user_uuid = vsgi_user_uuid;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		if(this.role == UserRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
		else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String getUsername() {
		return login;
	}

	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}	
	
	/**
	 * @return the role
	 */
	public UserRole getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(UserRole role) {
		this.role = role;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @return the vsgi_user_id
	 */
	public Long getVsgi_user_id() {
		return vsgi_user_id;
	}

	/**
	 * @param vsgi_user_id the vsgi_user_id to set
	 */
	public void setVsgi_user_id(Long vsgi_user_id) {
		this.vsgi_user_id = vsgi_user_id;
	}

	/**
	 * @return the vsgi_user_uuid
	 */
	public UUID getVsgi_user_uuid() {
		return vsgi_user_uuid;
	}

	/**
	 * @param vsgi_user_uuid the vsgi_user_uuid to set
	 */
	public void setVsgi_user_uuid(UUID vsgi_user_uuid) {
		this.vsgi_user_uuid = vsgi_user_uuid;
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
}