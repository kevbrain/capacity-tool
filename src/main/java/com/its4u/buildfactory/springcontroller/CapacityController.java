import org.springframework.stereotype.Controller;

@Controller
public class CapacityController {
	
	private final HttpServletRequest request;
	
	@GetMapping(value = "/")
	public String getHome() {
		return "index";
	}

}
