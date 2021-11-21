package com.test.user.controller;

import com.test.entity.Result;
import com.test.entity.StatusCode;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/address")
@CrossOrigin
public class AddressController {

   


    @PostMapping(value = "/hello" )
    public Result hello(){
    	
        return new Result(true,StatusCode.OK,"查询成功","hello");
    }

    
   
}
