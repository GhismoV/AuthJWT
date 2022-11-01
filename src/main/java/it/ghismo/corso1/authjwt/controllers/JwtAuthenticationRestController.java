package it.ghismo.corso1.authjwt.controllers;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.ghismo.corso1.authjwt.dto.JwtTokenRequest;
import it.ghismo.corso1.authjwt.dto.JwtTokenResponse;
import it.ghismo.corso1.authjwt.exceptions.AuthenticationException;
import it.ghismo.corso1.authjwt.security.JwtTokenUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
 
@RestController
@Slf4j
public class JwtAuthenticationRestController 
{

	@Value("${sicurezza.header}")
	private String tokenHeader;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	@Qualifier("customUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@PostMapping(value = "${sicurezza.uri}")
	@SneakyThrows
	public ResponseEntity<JwtTokenResponse> createAuthenticationToken(@RequestBody JwtTokenRequest authenticationRequest) {
		log.info("Autenticazione e Generazione Token");
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		log.warn(String.format("Token [{}] generated!!!", token));
		return ResponseEntity.ok(new JwtTokenResponse(token));
	}

	@GetMapping(value = "${sicurezza.refresh}")
	@SneakyThrows
	public ResponseEntity<JwtTokenResponse> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		log.info("Tentativo Refresh Token");
		String authToken = request.getHeader(tokenHeader);
		if (authToken == null) {
			throw new Exception("Token assente o non valido!");
		}
		
		final String token = authToken; 
		if (jwtTokenUtil.canTokenBeRefreshed(token)) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);
			log.warn(String.format("Refreshed Token %s", refreshedToken));
			return ResponseEntity.ok(new JwtTokenResponse(refreshedToken));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@SneakyThrows
	private void authenticate(String username, String password) {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			log.debug("Utente autenticato!!!!!");	
		} catch (DisabledException e) {
			log.warn("UTENTE DISABILITATO");
			throw new AuthenticationException(e, "UTENTE DISABILITATO");
		} catch (BadCredentialsException e) {
			log.warn("CREDENZIALI NON VALIDE");
			throw new AuthenticationException(e, "CREDENZIALI NON VALIDE");
		}
	}
	
}
