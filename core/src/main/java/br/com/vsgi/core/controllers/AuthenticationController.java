package br.com.vsgi.core.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vsgi.core.domain.authentication.AuthenticationDto;
import br.com.vsgi.core.domain.authentication.LoginResponseDto;
import br.com.vsgi.core.domain.authentication.RegisterDto;
import br.com.vsgi.core.domain.user.UserModel;
import br.com.vsgi.core.infra.security.TokenService;
import br.com.vsgi.core.repositories.AuthenticationRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Autowired
	private TokenService tokenService;

	@SuppressWarnings("rawtypes")
	@PostMapping("/login")
	public ResponseEntity login(@RequestBody @Valid AuthenticationDto dataAuthenticationDto) {
		
		var usernamePassword = new UsernamePasswordAuthenticationToken(dataAuthenticationDto.login(), dataAuthenticationDto.password());
		var auth = this.authenticationManager.authenticate(usernamePassword);		
		
		var token = tokenService.generateToken((UserModel) auth.getPrincipal());
		
		return ResponseEntity.ok(new LoginResponseDto(token)); 		
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/register")
	public ResponseEntity register(@RequestBody @Valid RegisterDto dataRegisterDto) {
		if(this.authenticationRepository.findByLogin(dataRegisterDto.login()) != null) return ResponseEntity.badRequest().build();
		
		String encryptedPassword = new BCryptPasswordEncoder().encode(dataRegisterDto.password());
		UserModel newUser = new UserModel(dataRegisterDto.login(), encryptedPassword, dataRegisterDto.role(), UUID.randomUUID());   
		
		this.authenticationRepository.save(newUser);
		
		return ResponseEntity.ok().build(); 		
	}	
}
