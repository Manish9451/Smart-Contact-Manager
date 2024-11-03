package com.smartContactManager.SmartContactManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartContactManager.SmartContactManager.dao.UserRepository;
import com.smartContactManager.SmartContactManager.entities.User;
import com.smartContactManager.SmartContactManager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @Autowired 
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model, HttpSession session) {
        model.addAttribute("title", "Signup - Smart Contact Manager");
        model.addAttribute("user", new User());

        // Retrieve message from session and add to model if present
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message"); // Clear message after displaying
        }

        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
                               Model model, HttpSession session) {

        try {
            if (!agreement) {
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("You have not agreed to the terms and conditions");
            }

            if (result1.hasErrors()) {
                System.out.println("ERROR" + result1.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            

            User result = this.userRepository.save(user);

            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
            return "redirect:/signup"; // Redirect to prevent form resubmission

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
            return "redirect:/signup"; // Redirect to signup
        }
    }

    @RequestMapping("/login")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login - Smart Contact Manager");
        return "login";
    }
}
