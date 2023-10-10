package br.com.vsgi.core.controllers;

import static br.com.vsgi.core.type.CoreConstantType.SAVE_STARTING;
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
import br.com.vsgi.core.repositories.AuthenticationRepository;
import jakarta.validation.Valid; 

@RestController
public class ClientController {
	
	/**
	 * Logger LOGGER
	 */
	private static final Logger LOGGER = LogManager.getLogger(ClientController.class);

	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	private AuthenticationRepository userRepository;
	
	/**
	 * @param clientRecordDto
	 * @return
	 */
	@PostMapping("/clients")
	public ResponseEntity<ClientModel> saveClient(@RequestBody @Valid ClientDto clientRecordDto) {
		LOGGER.info(SAVE_STARTING);
		var clientModel = new ClientModel();
		BeanUtils.copyProperties(clientRecordDto, clientModel);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel authenticatedUser = (UserModel) this.userRepository.findByLogin(authentication.getName());
		clientModel.setVsgi_client_uuid(UUID.randomUUID());
		clientModel.setCreatedby(authenticatedUser.getVsgi_user_id());
		clientModel.setUpdatedby(authenticatedUser.getVsgi_user_id());
		clientModel.setVsgi_client_id(authenticatedUser.getVsgi_client_id());
		clientModel.setVsgi_org_id(authenticatedUser.getVsgi_org_id());
		if (clientModel.getDescription() == null || clientModel.getDescription().trim().equals("")) {
			clientModel.setDescription(clientModel.getName());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(clientRepository.save(clientModel));
	}

	/**
	 * @return
	 */
	@GetMapping("/clients")
	public ResponseEntity<List<ClientModel>> getAllClients(){
		List<ClientModel> clientsList = clientRepository.findAll();
		if(!clientsList.isEmpty()) {
			for(ClientModel client : clientsList) {
				 Long id = client.getVsgi_client_id();
				client.add(linkTo(methodOn(ClientController.class).getOneClient(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(clientRepository.findAll());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/clients/{id}")
	public ResponseEntity<Object> getOneClient(@PathVariable(value="id") Long id) {
		Optional<ClientModel> clientOptional = clientRepository.findById(id);
		if(clientOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
		}		
		clientOptional.get().add(linkTo(methodOn(ClientController.class).getAllClients()).withSelfRel());		
		return ResponseEntity.status(HttpStatus.OK).body(clientOptional.get());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/clients/{id}")
	public ResponseEntity<Object> updateClient(@PathVariable(value = "id") Long id,
			@RequestBody @Valid ClientDto clientRecordDto) {
		Optional<ClientModel> clientOptional = clientRepository.findById(id);
		if (clientOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
		}		
		var clientModel = clientOptional.get();
		BeanUtils.copyProperties(clientRecordDto, clientModel);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel authenticatedUser = (UserModel) this.userRepository.findByLogin(authentication.getName());
		clientModel.setUpdatedby(authenticatedUser.getVsgi_user_id());

		return ResponseEntity.status(HttpStatus.OK).body(clientRepository.save(clientModel));
	}
	
	/**
	 * @param id
	 * @return
	 */
	@DeleteMapping("/clients/{id}")
	public ResponseEntity<Object> deleteClient(@PathVariable(value = "id") Long id) {
		Optional<ClientModel> clientOptional = clientRepository.findById(id);
		if (clientOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
		}		
		clientRepository.delete(clientOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Client deleted successfuly");
	}
}
