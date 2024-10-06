package market.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/test")
public class TestController {
	@GetMapping("/protected")
	public String protectedPath() {
		return "Protected";
	}

	@GetMapping("/public")
	public String publicPath() {
		return "222";
	}
}
