package br.com.vsgi.core.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import br.com.vsgi.core.repositories.ProductRepository;
import br.com.vsgi.core.repositories.UserRepository;
import jakarta.validation.Valid; 

@RestController
public class ProductController {

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;

	/**
	 * @param productRecordDto
	 * @return
	 */
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productRecordDto) {
		var productModel = new ProductModel();
		BeanUtils.copyProperties(productRecordDto, productModel);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel userModel = (UserModel) this.userRepository.findByLogin(authentication.getName());
		productModel.setVsgi_product_uuid(UUID.randomUUID());
		productModel.setCreatedby(userModel.getVsgi_user_id());
		productModel.setUpdatedby(userModel.getVsgi_user_id());
		productModel.setVsgi_client_id(userModel.getVsgi_client_id());
		productModel.setVsgi_org_id(userModel.getVsgi_org_id());
		if (productModel.getDescription() == null || productModel.getDescription().trim().equals("")) {
			productModel.setDescription(productModel.getName());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}

	/**
	 * @return
	 */
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		List<ProductModel> productsList = productRepository.findAll();
		if(!productsList.isEmpty()) {
			for(ProductModel product : productsList) {
				UUID id = product.getVsgi_product_uuid();
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id) {
		Optional<ProductModel> productOptional = productRepository.findById(id);
		if(productOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}		
		productOptional.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());		
		return ResponseEntity.status(HttpStatus.OK).body(productOptional.get());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
			@RequestBody @Valid ProductDto productRecordDto) {
		Optional<ProductModel> productOptional = productRepository.findById(id);
		if (productOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}		
		var productModel = productOptional.get();
		BeanUtils.copyProperties(productRecordDto, productModel);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel userModel = (UserModel) this.userRepository.findByLogin(authentication.getName());
		productModel.setUpdatedby(userModel.getVsgi_user_id());

		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
	}
	
	/**
	 * @param id
	 * @return
	 */
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> productOptional = productRepository.findById(id);
		if (productOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}		
		productRepository.delete(productOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfuly");
	}

}
