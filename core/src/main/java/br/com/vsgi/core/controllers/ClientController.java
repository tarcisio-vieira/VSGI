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

import br.com.vsgi.core.domain.client.ClientDto;
import br.com.vsgi.core.domain.client.ClientModel;
import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.repositories.ClientRepository;
import br.com.vsgi.core.type.CoreErrorException;
import br.com.vsgi.core.repositories.AuthenticationRepository;
import jakarta.validation.Valid; 

/**
 * @author Tarcisio Vieira
 *
 */
@RestController
public class ClientController {
	
	/**
	 * Logger LOGGER
	 */
	private static final Logger LOGGER = LogManager.getLogger(ClientController.class);

	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	private AuthenticationRepository authenticationRepository;
	
	/**
	 * Create a new instance of ClientController
	 * 
	 * @param clientRecordDto
	 * @return ResponseEntity<ClientModel> 
	 */
	@PostMapping("/clients")
	public ResponseEntity<ClientModel> saveClient(@RequestBody @Valid ClientDto clientRecordDto) {
		LOGGER.info(authenticated().getName() + SAVE_STARTING);
		try {
			var clientModel = new ClientModel();
			BeanUtils.copyProperties(clientRecordDto, clientModel);

			clientModel.setVsgi_client_uuid(UUID.randomUUID());
			clientModel.setCreatedby(authenticated().getVsgi_user_id());
			clientModel.setUpdatedby(authenticated().getVsgi_user_id());
			clientModel.setVsgi_client_id(authenticated().getVsgi_client_id());
			clientModel.setVsgi_org_id(authenticated().getVsgi_org_id());
			if (clientModel.getDescription() == null || clientModel.getDescription().trim().equals("")) {
				clientModel.setDescription(clientModel.getName());
			}
			clientRepository.save(clientModel);
			LOGGER.info(authenticated().getName() + SAVED_SUCCESSFULLY + clientModel.getVsgi_client_id());
			return ResponseEntity.status(HttpStatus.CREATED).body(clientModel);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Use this method to get list object
	 * 
	 * @return ResponseEntity<List<ClientModel>>
	 */
	@GetMapping("/clients")
	public ResponseEntity<List<ClientModel>> getAllClients() {
		LOGGER.info(authenticated().getName() + GET_ALL);
		try {
			List<ClientModel> clientsList = clientRepository.findAll();
			if (!clientsList.isEmpty()) {
				for (ClientModel client : clientsList) {
					Long id = client.getVsgi_client_id();
					client.add(linkTo(methodOn(ClientController.class).getOneClient(id)).withSelfRel());
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body(clientRepository.findAll());
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
	@GetMapping("/clients/{id}")
	public ResponseEntity<Object> getOneClient(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + GET_ONE + id);
		try {
			Optional<ClientModel> clientOptional = clientRepository.findById(id);
			if (clientOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			clientOptional.get().add(linkTo(methodOn(ClientController.class).getAllClients()).withSelfRel());
			return ResponseEntity.status(HttpStatus.OK).body(clientOptional.get());
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
	@PutMapping("/clients/{id}")
	public ResponseEntity<Object> updateClient(@PathVariable(value = "id") Long id,
			@RequestBody @Valid ClientDto clientRecordDto) {
		LOGGER.info(authenticated().getName() + UPDATE_STARTING + id);
		try {
		Optional<ClientModel> clientOptional = clientRepository.findById(id);
		if (clientOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
		}		
		var clientModel = clientOptional.get();
		BeanUtils.copyProperties(clientRecordDto, clientModel);
		clientModel.setUpdatedby(authenticated().getVsgi_user_id());
		clientRepository.save(clientModel);

		LOGGER.info(authenticated().getName() + UPDATE_SUCCESSFULLY + id);
		return ResponseEntity.status(HttpStatus.OK).body(clientModel);
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
	@DeleteMapping("/clients/{id}")
	public ResponseEntity<Object> deleteClient(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + DELETE_STARTING + id);
		try {
		Optional<ClientModel> clientOptional = clientRepository.findById(id);
		if (clientOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
		}		
		clientRepository.delete(clientOptional.get());
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
