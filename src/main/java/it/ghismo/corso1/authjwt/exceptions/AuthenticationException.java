package it.ghismo.corso1.authjwt.exceptions;

import org.springframework.http.HttpStatus;

import it.ghismo.corso1.authjwt.errors.ResultEnum;


public class AuthenticationException extends BaseResultException {
	private static final long serialVersionUID = -2404717895107950786L;
	
	public AuthenticationException(String esito) {
		super(ResultEnum.AuthKoParam1, esito); 
	}
	
	public AuthenticationException(Throwable cause, String esito) {
		super(cause, ResultEnum.AuthKoParam1, esito); 
	}	


	@Override public HttpStatus getHttpStatus() { return HttpStatus.UNAUTHORIZED; }

}
