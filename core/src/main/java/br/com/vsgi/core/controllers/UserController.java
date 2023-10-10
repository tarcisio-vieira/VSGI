/*
 * COPYRIGHT VSGI 2023 - ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the prior
 * written consent of VSGI.
 */
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

import br.com.vsgi.core.domain.user.UserDto;
import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.repositories.AuthenticationRepository;
import br.com.vsgi.core.repositories.UserRepository;
import br.com.vsgi.core.type.CoreErrorException;
import jakarta.validation.Valid; 

/**
 * @author Tarcisio Vieira
 * 
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
	 * Create a new instance of UserController
	 * 
	 * @param userRecordDto
	 * @return ResponseEntity<UserModel> 
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
			userRepository.save(userModel);
			LOGGER.info(authenticated().getName() + SAVED_SUCCESSFULLY + userModel.getVsgi_user_id());
			return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Use this method to get list object
	 * 
	 * @return ResponseEntity<List<UserModel>>
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
	 * Use this method to get object by id
	 * 
	 * @param id
	 * @return ResponseEntity<Object>
	 */
	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getOneUser(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + GET_ONE + id);
		try {
			Optional<UserModel> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			userOptional.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
			return ResponseEntity.status(HttpStatus.OK).body(userOptional.get());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Class responsible for receiving and updating save data
	 * 
	 * @param id
	 * @return ResponseEntity<Object>
	 */
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable(value = "id") Long id,
			@RequestBody @Valid UserDto userRecordDto) {
		LOGGER.info(authenticated().getName() + UPDATE_STARTING + id);
		try {
			Optional<UserModel> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			var userModel = userOptional.get();
			BeanUtils.copyProperties(userRecordDto, userModel);
			userModel.setUpdatedby(authenticated().getVsgi_user_id());
			userRepository.save(userModel);

			LOGGER.info(authenticated().getName() + UPDATE_SUCCESSFULLY + id);
			return ResponseEntity.status(HttpStatus.OK).body(userModel);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Class responsible for deleting data
	 * 
	 * @param id
	 * @return ResponseEntity<Object>
	 */
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + DELETE_STARTING + id);
		try {
			Optional<UserModel> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			userRepository.delete(userOptional.get());
			return ResponseEntity.status(HttpStatus.OK).body(authenticated().getLogin() + DELETE_SUCCESSFULLY + id);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Use this class to get authenticated user data
	 * 
	 * @return UserModel
	 */
	private UserModel authenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel userAuthenticated = (UserModel) this.authenticationRepository.findByLogin(authentication.getName());
		return userAuthenticated;
	}
}
