package com.demo.socialmedia.controller;

import com.demo.socialmedia.model.User;
import com.demo.socialmedia.service.UserService;
import com.demo.socialmedia.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse res) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        if(auth!= null && auth.isAuthenticated()){
            User user = userService.getUserByUsername(username);
            String token = jwtUtil.createToken(user.getUsername(), "ROLE_" + user.getRole(),user.getId());
            Cookie cookie = new Cookie("jwt", token);
             cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            res.addCookie(cookie);
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterForm(HttpSession session) {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        User user = userService.register(username, password);
        if (user == null) {
            model.addAttribute("error", "Username đã tồn tại");
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        res.addCookie(cookie);
        return "redirect:/login";
    }
}
