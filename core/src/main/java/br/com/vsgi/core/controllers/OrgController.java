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

import br.com.vsgi.core.domain.organization.OrgDto;
import br.com.vsgi.core.domain.organization.OrgModel;
import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.repositories.OrgRepository;
import br.com.vsgi.core.type.CoreErrorException;
import br.com.vsgi.core.repositories.AuthenticationRepository;
import jakarta.validation.Valid; 

/**
 * @author Tarcisio Vieira
 *
 */
@RestController
public class OrgController {
	
	/**
	 * Logger LOGGER
	 */
	private static final Logger LOGGER = LogManager.getLogger(OrgController.class);

	@Autowired
	OrgRepository orgRepository;
	
	@Autowired
	private AuthenticationRepository authenticationRepository;

	/**
	 * Create a new instance of OrgController
	 * 
	 * @param orgRecordDto
	 * @return ResponseEntity<OrgModel> 
	 */
	@PostMapping("/orgs")
	public ResponseEntity<OrgModel> saveOrg(@RequestBody @Valid OrgDto orgRecordDto) {
		LOGGER.info(authenticated().getName() + SAVE_STARTING);
		try {
			var orgModel = new OrgModel();
			BeanUtils.copyProperties(orgRecordDto, orgModel);

			orgModel.setVsgi_org_uuid(UUID.randomUUID());
			orgModel.setCreatedby(authenticated().getVsgi_user_id());
			orgModel.setUpdatedby(authenticated().getVsgi_user_id());
			orgModel.setVsgi_client_id(authenticated().getVsgi_client_id());
			orgModel.setVsgi_org_id(authenticated().getVsgi_org_id());
			if (orgModel.getDescription() == null || orgModel.getDescription().trim().equals("")) {
				orgModel.setDescription(orgModel.getName());
			}
			orgRepository.save(orgModel);
			LOGGER.info(authenticated().getName() + SAVED_SUCCESSFULLY + orgModel.getVsgi_org_id());
			return ResponseEntity.status(HttpStatus.CREATED).body(orgModel);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new CoreErrorException(ERROR_EXCEPTION, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Use this method to get list object
	 * 
	 * @return ResponseEntity<List<OrgModel>>
	 */
	@GetMapping("/orgs")
	public ResponseEntity<List<OrgModel>> getAllOrgs() {
		LOGGER.info(authenticated().getName() + GET_ALL);
		try {
			List<OrgModel> orgsList = orgRepository.findAll();
			if (!orgsList.isEmpty()) {
				for (OrgModel org : orgsList) {
					Long id = org.getVsgi_org_id();
					org.add(linkTo(methodOn(OrgController.class).getOneOrg(id)).withSelfRel());
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body(orgRepository.findAll());
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
	@GetMapping("/orgs/{id}")
	public ResponseEntity<Object> getOneOrg(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + GET_ONE + id);
		try {
			Optional<OrgModel> orgOptional = orgRepository.findById(id);
			if (orgOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			orgOptional.get().add(linkTo(methodOn(OrgController.class).getAllOrgs()).withSelfRel());
			return ResponseEntity.status(HttpStatus.OK).body(orgOptional.get());
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
	@PutMapping("/orgs/{id}")
	public ResponseEntity<Object> updateOrg(@PathVariable(value = "id") Long id,
			@RequestBody @Valid OrgDto orgRecordDto) {
		LOGGER.info(authenticated().getName() + UPDATE_STARTING + id);
		try {
			Optional<OrgModel> orgOptional = orgRepository.findById(id);
			if (orgOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
			}
			var orgModel = orgOptional.get();
			BeanUtils.copyProperties(orgRecordDto, orgModel);
			orgModel.setUpdatedby(authenticated().getVsgi_user_id());
			orgRepository.save(orgModel);

			LOGGER.info(authenticated().getName() + UPDATE_SUCCESSFULLY + id);
			return ResponseEntity.status(HttpStatus.OK).body(orgModel);
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
	@DeleteMapping("/orgs/{id}")
	public ResponseEntity<Object> deleteOrg(@PathVariable(value = "id") Long id) {
		LOGGER.info(authenticated().getName() + DELETE_STARTING + id);
		try {
		Optional<OrgModel> orgOptional = orgRepository.findById(id);
		if (orgOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(authenticated().getLogin() + RECORD_NOT_FOUND + id);
		}		
		orgRepository.delete(orgOptional.get());
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
