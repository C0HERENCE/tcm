package cn.ccwisp.tcm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/abc")
    public String test()
    {
        return "jdskfa;jsafkjdl;ajfsd";
    }

    @GetMapping("/def")
    public String Def()
    {
        return "dkf jas fdlasfk dl;ads ";
    }
}
