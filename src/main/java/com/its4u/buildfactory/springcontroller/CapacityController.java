import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CapacityController {
	
	private final HttpServletRequest request;
	
	@GetMapping(value = "/")
	public String getHome() {
		return "index";
	}

}
