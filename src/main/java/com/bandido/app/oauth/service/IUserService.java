package com.bandido.app.oauth.service;

import com.bandido.app.user.commons.models.entity.Usuario;

public interface IUserService {
	
	public Usuario findByUsername(String username);
	
	public Usuario update(Usuario usuario, Long id);

}
