package br.com.vsgi.core.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.domain.user.UserDto;
import br.com.vsgi.core.repositories.UserRepository;

import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn; 

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	/**
	 * @param userRecordDto
	 * @return
	 */
	@PostMapping("/users")
	public ResponseEntity<UserModel> saveUser(@RequestBody @Valid UserDto userRecordDto) {
		var userModel = new UserModel();
		BeanUtils.copyProperties(userRecordDto, userModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userModel));
	}
	
	/**
	 * @return
	 */
	@GetMapping("/users")
	public ResponseEntity<List<UserModel>> getAllUsers(){
		List<UserModel> usersList = userRepository.findAll();
		if(!usersList.isEmpty()) {
			for(UserModel user : usersList) {
				UUID id = user.getId();
				user.add(linkTo(methodOn(UserController.class).getOneUser(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getOneUser(@PathVariable(value="id") UUID id) {
		Optional<UserModel> userOptional = userRepository.findById(id);
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}		
		userOptional.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());		
		return ResponseEntity.status(HttpStatus.OK).body(userOptional.get());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable(value = "id") UUID id,
			@RequestBody @Valid UserDto userRecordDto) {
		Optional<UserModel> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}		
		var userModel = userOptional.get();
		BeanUtils.copyProperties(userRecordDto, userModel);
		return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(userModel));
	}
	
	/**
	 * @param id
	 * @return
	 */
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") UUID id) {
		Optional<UserModel> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}		
		userRepository.delete(userOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("User deleted successfuly");
	}

}
