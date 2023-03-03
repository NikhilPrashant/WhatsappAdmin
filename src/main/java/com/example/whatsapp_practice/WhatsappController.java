package com.example.whatsapp_practice;

import com.example.whatsapp_practice.model.Group;
import com.example.whatsapp_practice.model.Message;
import com.example.whatsapp_practice.model.User;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("whatsapp")
public class WhatsappController {

    //Autowire will not work in this case, no need to change this and add autowire
    WhatsappService whatsappService = new WhatsappService();

    @PostMapping("/add-user")
    public ResponseEntity createUser(@PathParam("name") String name, @PathParam("mobile") String mobile) throws Exception {
        String res = whatsappService.createUser(name, mobile);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/add-group")
    public ResponseEntity createGroup(@RequestBody List<User> users){
        Group res = whatsappService.createGroup(users);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/add-message")
    public ResponseEntity createMessage(@PathParam("content") String content){
        int res = whatsappService.createMessage(content);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("/send-message")
    public ResponseEntity sendMessage(@RequestBody Message message,@RequestBody User sender,@RequestBody Group group) throws Exception{
        int res = whatsappService.sendMessage(message, sender, group);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }
    @PutMapping("/change-admin")
    public ResponseEntity changeAdmin(@RequestBody User approver,@RequestBody User user,@RequestBody Group group) throws Exception{
        String res = whatsappService.changeAdmin(approver, user, group);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/remove-user")
    public ResponseEntity removeUser(@RequestBody User user) throws Exception{
        int res = whatsappService.removeUser(user);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @GetMapping("/find-messages")
    public ResponseEntity findMessage(@PathParam("start") Date start,@PathParam("end") Date end,@PathParam("k") int K) throws Exception{
        String res = whatsappService.findMessage(start, end, K);
        return new ResponseEntity<>(res, HttpStatus.FOUND);
    }
}
