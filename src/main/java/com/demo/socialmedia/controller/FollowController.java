package com.demo.socialmedia.controller;

import com.demo.socialmedia.service.FollowService;
import com.demo.socialmedia.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/add")
    public String follow(@RequestParam("followedUserId") int followedUserId ) {
       Integer userId = authUtil.getCurrentUserId();
        if (userId == null) return "redirect:/login";

        followService.follow(userId, followedUserId);
        return "redirect:/";
    }

    @PostMapping("/remove")
    public String unfollow(@RequestParam("followedUserId") int followedUserId) {
        Integer userId = authUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        followService.unfollow(userId, followedUserId);
        return "redirect:/";
    }
}
