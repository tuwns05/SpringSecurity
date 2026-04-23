package com.demo.socialmedia.controller;

import com.demo.socialmedia.model.User;
import com.demo.socialmedia.service.FollowService;
import com.demo.socialmedia.service.PostService;
import com.demo.socialmedia.service.UserService;
import com.demo.socialmedia.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;
    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

    @Autowired
    private AuthUtil authUtil;

    @GetMapping("/")
    public String home(Model model, HttpServletResponse response) {
        Integer userId = authUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", postService.getFeedByUserId(userId));
        model.addAttribute("followingUsers", followService.getFollowing(userId));
        model.addAttribute("followerUsers", followService.getFollowers(userId));
        model.addAttribute("suggestedUsers", followService.getSuggestedUsers(userId));
        return "home";
    }
}
