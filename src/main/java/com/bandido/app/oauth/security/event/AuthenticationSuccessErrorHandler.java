package com.bandido.app.oauth.security.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bandido.app.oauth.service.IUserService;
import com.bandido.app.user.commons.models.entity.Usuario;

import feign.FeignException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher{
	
	@Autowired
	private IUserService userService;

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		UserDetails user = (UserDetails) authentication.getPrincipal();
		log.info(String.format("SUCCESS LOGIN : %s", user.getUsername()));
		
		Usuario usuario = userService.findByUsername(authentication.getName());
		if (usuario.getIntentos() != null && usuario.getIntentos() > 0) {
			usuario.setIntentos(0);
			userService.update(usuario, usuario.getId());
			log.error(String.format("Contador de intentos del Usuario [ %s ] ha sido reiniciado a [ %s ]", usuario.getUsername(), usuario.getIntentos()));
		}
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		log.error(String.format("ERROR LOGIN : %s", exception.getMessage()));
		
		try {
			
			Usuario usuario = userService.findByUsername(authentication.getName());
			if (usuario.getIntentos() == null) {
					usuario.setIntentos(0);
			}
			
			log.info(String.format("Intentos actuales [ %s ]", usuario.getIntentos()));
			usuario.setIntentos(usuario.getIntentos()+1);
			log.info(String.format("Intentos actualizados [ %s ]", usuario.getIntentos()));
			
			if (usuario.getIntentos() >=3) {
				log.error(String.format("El Usuario [ %s ] ha sido des-habilitado por máximos intentos", usuario.getUsername()));
				usuario.setEnabled(false);
			}
			
			userService.update(usuario, usuario.getId());
			
		} catch (FeignException e) {
			log.error(String.format("El Usuario [ %s ] no existe en el sistema", authentication.getName()));
		}
	}

}
