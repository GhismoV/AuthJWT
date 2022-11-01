package it.ghismo.corso1.authjwt.errors;

import java.util.Objects;

import it.ghismo.corso1.authjwt.dto.ResultDto;


public enum ResultEnum {
	@Result(code = "0", msg = "Operazione eseguita")	
	Ok

	,@Result(code = "0", msg = "Autenticazione OK")	
	AuthOk

	,@Result(code = "1", msg = "Elemento non trovato")
	AuthKo
	
	,@Result(code = "2", msg = "Autenticazione KO su con esito [%1$s]")
	AuthKoParam1

	,@Result(code = "3", msg = "Errore di validazione sul campo [%1$s.%2$s] avente valore [%3$s]")
	BindingValidationError
	
	,@Result(code = "5", msg = "Il campo [%1$s] non può assumere il valore [%2$s]")
	InfoInvalidValueError

	;
	
	public Result getResult() {
		try {
			return ResultEnum.class.getDeclaredField(this.name()).getAnnotation(Result.class);
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}
	
	public ResultDto getDto() {
		Result r = getResult();
		return Objects.nonNull(r) ? new ResultDto(r.code(), r.msg()) : null; 
	}

	public ResultDto getDto(Object... params) {
		Result r = getResult();
		return Objects.nonNull(r) ? new ResultDto(r.code(), String.format(r.msg(), params) ) : null; 
	}
	
}
