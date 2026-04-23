package com.demo.socialmedia.controller;

import com.demo.socialmedia.model.Post;
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
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private FollowService followService;
    @Autowired
    private AuthUtil authUtil;

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model, HttpServletResponse httpServletResponse) {
        Integer userId = authUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            httpServletResponse.addCookie(cookie);
            return "redirect:/login";
        }

        List<Post> posts = postService.getPostsByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("followingUsers", followService.getFollowing(userId));
        model.addAttribute("followerUsers", followService.getFollowers(userId));
        return "profile";
    }
}
