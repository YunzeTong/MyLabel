package RedCloudRule.bs.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebMainController {
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
    //以下自己加
    @RequestMapping(value = "/mypic")
    public String mypic(){
        return "index";
    }

    @RequestMapping(value = "/login")
    public String login(){
        return "index";
    }

    @RequestMapping(value = "/register")
    public String register(){
        return "index";
    }

    @RequestMapping(value = "/missionsquare")
    public String missionsquare(){
        return "index";
    }

    @RequestMapping(value = "/mytask")
    public String mytask(){
        return "index";
    }

    @RequestMapping(value = "/*")
    public String errorpage(){
        return "index";
    }

}
