package com.bandido.app.oauth.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bandido.app.oauth.client.UserFeignClient;
import com.bandido.app.oauth.service.IUserService;
import com.bandido.app.user.commons.models.entity.Usuario;

import feign.FeignException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UserService implements IUserService, UserDetailsService {

	@Autowired
	private UserFeignClient client;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		try {

			Usuario user = client.findByUsername(username);

			List<GrantedAuthority> authorities = user.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getName()))
					.peek(authority -> log.info(String.format("Role: [%s]", authority.getAuthority())))
					.collect(Collectors.toList());

			log.info(String.format("Usuario autenticado", username));

			return new User(user.getUsername(), user.getPassword(), user.getEnabled(), true, true, true, authorities);

		} catch (FeignException e) {
			log.error(String.format("Error en el login, no existe el usuario %s en el sistema", username));
			throw new UsernameNotFoundException(
					String.format("Error en el login, no existe el usuario %s en el sistema", username));
		}
	}

	@Override
	public Usuario findByUsername(String username) {
		return client.findByUsername(username);
	}

	@Override
	public Usuario update(Usuario usuario, Long id) {
		return client.update(usuario, id);
	}

}
