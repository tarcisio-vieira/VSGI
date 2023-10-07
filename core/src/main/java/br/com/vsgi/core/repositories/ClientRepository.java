package br.com.vsgi.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vsgi.core.domain.client.ClientModel;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, Long> {
}
