package br.com.vsgi.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vsgi.core.domain.organization.OrgModel;

@Repository
public interface OrgRepository extends JpaRepository<OrgModel, Long> {
}
