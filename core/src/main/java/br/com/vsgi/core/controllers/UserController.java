package br.com.vsgi.core.controllers;

import static br.com.vsgi.core.type.CoreConstantType.DELETE_STARTING;
import static br.com.vsgi.core.type.CoreConstantType.ERROR_EXCEPTION;
import static br.com.vsgi.core.type.CoreConstantType.GET_ALL;
import static br.com.vsgi.core.type.CoreConstantType.GET_ONE;
import static br.com.vsgi.core.type.CoreConstantType.SAVE_STARTING;
import static br.com.vsgi.core.type.CoreConstantType.UPDATE_STARTING;
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

import br.com.vsgi.core.domain.user.UserDto;
import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.repositories.AuthenticationRepository;
import br.com.vsgi.core.repositories.UserRepository;
import br.com.vsgi.core.type.CoreErrorException;
import jakarta.validation.Valid; 

/**
 * @author Tarcisio Vieira
 *
 */
@RestController
public class UserController {
	
	/**
	 * Logger LOGGER
	 */
	private static final Logger LOGGER = LogManager.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private AuthenticationRepository authenticationRepository;

	/**
	 * @param userRecordDto
	 * @return
	 */
	@PostMapping("/users")
	public ResponseEntity<UserModel> saveUser(@RequestBody @Valid UserDto userRecordDto) {
		LOGGER.info(authenticated().getName() + SAVE_STARTING);
		try {
			var userModel = new UserModel();
			BeanUtils.copyProperties(userRecordDto, userModel);

			userModel.setVsgi_user_uuid(UUID.randomUUID());
			userModel.setCreatedby(authenticated().getVsgi_user_id());
			userModel.setUpdatedby(authenticated().getVsgi_user_id());
			userModel.setVsgi_client_id(authenticated().getVsgi_client_id());
			userModel.setVsgi_org_id(authenticated().getVsgi_org_id());
			if (userModel.getDescription() == null || userModel.getDescription().trim().equals("")) {
				userModel.setDescription(userModel.getName());
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userModel));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @return
	 */
	@GetMapping("/users")
	public ResponseEntity<List<UserModel>> getAllUsers() {
		LOGGER.info(authenticated().getName() + GET_ALL);
		try {
			List<UserModel> usersList = userRepository.findAll();
			if (!usersList.isEmpty()) {
				for (UserModel user : usersList) {
					Long id = user.getVsgi_user_id();
					user.add(linkTo(methodOn(UserController.class).getOneUser(id)).withSelfRel());
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getOneUser(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + GET_ONE + id);
		try {
			Optional<UserModel> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
			userOptional.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
			return ResponseEntity.status(HttpStatus.OK).body(userOptional.get());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable(value = "id") Long id,
			@RequestBody @Valid UserDto userRecordDto) {
		LOGGER.info(authenticated().getName() + UPDATE_STARTING + id);
		try {
			Optional<UserModel> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
			var userModel = userOptional.get();
			BeanUtils.copyProperties(userRecordDto, userModel);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserModel authenticatedUser = (UserModel) this.authenticationRepository
					.findByLogin(authentication.getName());
			userModel.setUpdatedby(authenticatedUser.getVsgi_user_id());
			return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(userModel));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * @param id
	 * @return
	 */
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + DELETE_STARTING + id);
		try {
			Optional<UserModel> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
			userRepository.delete(userOptional.get());
			return ResponseEntity.status(HttpStatus.OK).body("User deleted successfuly");
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
		UserModel userAuthenticated = (UserModel) this.authenticationRepository.findByLogin(authentication.getName());
		return userAuthenticated;
	}
}
