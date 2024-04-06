package inhatc.k8sProject.fineDust.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/map")
    public String map() {
        return "map"; // resources/templates/map.html을 가리킵니다.
    }

}
