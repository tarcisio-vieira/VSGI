package br.com.vsgi.core.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vsgi.core.domain.product.ProductModel;

@Repository
public interface ProductUUIDRepository extends JpaRepository<ProductModel, UUID> {
}
