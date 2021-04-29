package com.bandido.app.oauth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bandido.app.user.commons.models.entity.Usuario;



@FeignClient(name = "user-service")
public interface UserFeignClient {
	
	@GetMapping("/user/search/get-username")
	public Usuario findByUsername(@RequestParam("username") String username);
	
	@GetMapping("/user/search/getUsernameByUsername")
	public Usuario getUsernameByUsername(@RequestParam("username") String username);
	
	@PutMapping("/user/{id}")
	public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id);
	
}
