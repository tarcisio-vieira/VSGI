package br.com.vsgi.core.controllers;

import static br.com.vsgi.core.type.CoreConstantType.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.vsgi.core.domain.product.ProductDto;
import br.com.vsgi.core.domain.product.ProductModel;
import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.repositories.AuthenticationRepository;
import br.com.vsgi.core.repositories.ProductRepository;
import br.com.vsgi.core.type.CoreErrorException;
import jakarta.validation.Valid;

/**
 * @author Tarcisio Vieira
 *
 */
@RestController
public class ProductController {

	/**
	 * Logger LOGGER
	 */
	private static final Logger LOGGER = LogManager.getLogger(ProductController.class);

	@Autowired
	ProductRepository productRepository;

	@Autowired
	private AuthenticationRepository userRepository;

	/**
	 * @param productRecordDto
	 * @return
	 */
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productRecordDto) {
		LOGGER.info(authenticated().getName() + SAVE_STARTING);
		try {
			var productModel = new ProductModel();
			BeanUtils.copyProperties(productRecordDto, productModel);

			productModel.setVsgi_product_uuid(UUID.randomUUID());
			productModel.setCreatedby(authenticated().getVsgi_user_id());
			productModel.setUpdatedby(authenticated().getVsgi_user_id());
			productModel.setVsgi_client_id(authenticated().getVsgi_client_id());
			productModel.setVsgi_org_id(authenticated().getVsgi_org_id());
			if (productModel.getDescription() == null || productModel.getDescription().trim().equals("")) {
				productModel.setDescription(productModel.getName());
			}
			productRepository.save(productModel);
			LOGGER.info(authenticated().getName() + SAVED_SUCCESSFULLY + productModel.getVsgi_product_id());
			return ResponseEntity.status(HttpStatus.CREATED).body(productModel);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @return
	 */
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts() {
		LOGGER.info(authenticated().getName() + GET_ALL);
		try {
			List<ProductModel> productsList = productRepository.findAll();
			if (!productsList.isEmpty()) {
				for (ProductModel product : productsList) {
					Long id = product.getVsgi_product_id();
					product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + GET_ONE + id);
		try {
			Optional<ProductModel> productOptional = productRepository.findById(id);
			if (productOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			productOptional.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
			return ResponseEntity.status(HttpStatus.OK).body(productOptional.get());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") Long id,
			@RequestBody @Valid ProductDto productRecordDto) {
		LOGGER.info(authenticated().getName() + UPDATE_STARTING + id);
		try {
			Optional<ProductModel> productOptional = productRepository.findById(id);
			if (productOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			var productModel = productOptional.get();
			BeanUtils.copyProperties(productRecordDto, productModel);
			productModel.setUpdatedby(authenticated().getVsgi_user_id());

			productRepository.save(productModel);

			LOGGER.info(authenticated().getName() + UPDATE_SUCCESSFULLY + id);
			return ResponseEntity.status(HttpStatus.OK).body(productModel);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param id
	 * @return
	 */
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + DELETE_STARTING + id);
		try {
			Optional<ProductModel> productOptional = productRepository.findById(id);
			if (productOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			productRepository.delete(productOptional.get());
			return ResponseEntity.status(HttpStatus.OK).body(authenticated().getLogin() + DELETE_SUCCESSFULLY + id);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @return UserModel
	 */
	private UserModel authenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel userAuthenticated = (UserModel) this.userRepository.findByLogin(authentication.getName());
		return userAuthenticated;
	}
}
