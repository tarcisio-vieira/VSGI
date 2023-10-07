package br.com.vsgi.core.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.vsgi.core.domain.user.UserModel;

@Repository
public interface AuthenticationRepository extends JpaRepository<UserModel, UUID> {	
	UserDetails findByLogin(String login);	
}
