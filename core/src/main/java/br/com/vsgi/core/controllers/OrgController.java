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

import br.com.vsgi.core.domain.organization.OrgDto;
import br.com.vsgi.core.domain.organization.OrgModel;
import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.repositories.OrgRepository;
import br.com.vsgi.core.repositories.AuthenticationRepository;
import jakarta.validation.Valid; 

@RestController
public class OrgController {

	@Autowired
	OrgRepository orgRepository;
	
	@Autowired
	private AuthenticationRepository userRepository;

	/**
	 * @param orgRecordDto
	 * @return
	 */
	@PostMapping("/orgs")
	public ResponseEntity<OrgModel> saveOrg(@RequestBody @Valid OrgDto orgRecordDto) {
		var orgModel = new OrgModel();
		BeanUtils.copyProperties(orgRecordDto, orgModel);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel authenticatedUser = (UserModel) this.userRepository.findByLogin(authentication.getName());
		orgModel.setVsgi_org_uuid(UUID.randomUUID());
		orgModel.setCreatedby(authenticatedUser.getVsgi_user_id());
		orgModel.setUpdatedby(authenticatedUser.getVsgi_user_id());
		orgModel.setVsgi_client_id(authenticatedUser.getVsgi_client_id());
		orgModel.setVsgi_org_id(authenticatedUser.getVsgi_org_id());
		if (orgModel.getDescription() == null || orgModel.getDescription().trim().equals("")) {
			orgModel.setDescription(orgModel.getName());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(orgRepository.save(orgModel));
	}

	/**
	 * @return
	 */
	@GetMapping("/orgs")
	public ResponseEntity<List<OrgModel>> getAllOrgs(){
		List<OrgModel> orgsList = orgRepository.findAll();
		if(!orgsList.isEmpty()) {
			for(OrgModel org : orgsList) {
				 Long id = org.getVsgi_org_id();
				org.add(linkTo(methodOn(OrgController.class).getOneOrg(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(orgRepository.findAll());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/orgs/{id}")
	public ResponseEntity<Object> getOneOrg(@PathVariable(value="id") Long id) {
		Optional<OrgModel> orgOptional = orgRepository.findById(id);
		if(orgOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Org not found");
		}		
		orgOptional.get().add(linkTo(methodOn(OrgController.class).getAllOrgs()).withSelfRel());		
		return ResponseEntity.status(HttpStatus.OK).body(orgOptional.get());
	}
	
	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/orgs/{id}")
	public ResponseEntity<Object> updateOrg(@PathVariable(value = "id") Long id,
			@RequestBody @Valid OrgDto orgRecordDto) {
		Optional<OrgModel> orgOptional = orgRepository.findById(id);
		if (orgOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Org not found");
		}		
		var orgModel = orgOptional.get();
		BeanUtils.copyProperties(orgRecordDto, orgModel);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserModel authenticatedUser = (UserModel) this.userRepository.findByLogin(authentication.getName());
		orgModel.setUpdatedby(authenticatedUser.getVsgi_user_id());

		return ResponseEntity.status(HttpStatus.OK).body(orgRepository.save(orgModel));
	}
	
	/**
	 * @param id
	 * @return
	 */
	@DeleteMapping("/orgs/{id}")
	public ResponseEntity<Object> deleteOrg(@PathVariable(value = "id") Long id) {
		Optional<OrgModel> orgOptional = orgRepository.findById(id);
		if (orgOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Org not found");
		}		
		orgRepository.delete(orgOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Org deleted successfuly");
	}
}
