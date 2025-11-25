package kr.co.wave.controller.cs.ai;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AIController {

    @GetMapping("/cs/ai/cs")
    public String cs(){
        return "cs/ai/cs";
    }

}
