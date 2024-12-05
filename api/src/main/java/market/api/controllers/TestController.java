package market.api.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/test")
public class TestController {
	@GetMapping("/user-protected")
	@PreAuthorize("hasRole('USER')")
	public String protectedPath() {
		return "User Protected";
	}

	@GetMapping("/admin-protected")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminPath() {
		return "Admin Protected";
	}

	@GetMapping("/public")
	public String publicPath() {
		return "Public";
	}


}
