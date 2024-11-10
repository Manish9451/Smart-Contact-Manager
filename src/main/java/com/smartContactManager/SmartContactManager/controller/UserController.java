package com.smartContactManager.SmartContactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartContactManager.SmartContactManager.dao.ContactRepository;
import com.smartContactManager.SmartContactManager.dao.UserRepository;
import com.smartContactManager.SmartContactManager.entities.Contact;
import com.smartContactManager.SmartContactManager.entities.User;
import com.smartContactManager.SmartContactManager.helper.Message;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        // System.out.println("USERNAME "+userName);

        User user = this.userRepository.getUserByUserName(userName);
        // System.out.println("USER "+user);
        model.addAttribute("user", user);
    }

    // dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {

        return "normal/user_dashboard";
    }

    // open add form handeler
    @GetMapping("/add_contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result,
            @RequestParam("profileImage") MultipartFile file, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {

            if (result.hasErrors()) {
                System.out.println("Result" + result.toString());
                model.addAttribute("contact", contact);

                return "normal/add_contact_form";
            }

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            // processing and uploading file
            if (file.isEmpty()) {
                // if the file is empty then try our message
                System.out.println("File is empty");

            } else {
                // file the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("File is uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            this.userRepository.save(user);

            System.out.println("Data" + contact);

            System.out.println("Added to data Base");

            // message success
            // Use flash attribute instead of session attribute
            redirectAttributes.addFlashAttribute("message",
                    new Message("Your contact is added! Add more...", "success"));

        } catch (Exception e) {
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("message", new Message("Something went wrong try again...", "danger"));

        }

        return "redirect:/user/add_contact";

    }

    // show contacts
    @GetMapping("/show_contacts")
    public String showContacts(Model model, Principal principal) {
        model.addAttribute("title", "Show User Contacts");
        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);

        List<Contact> contacts = this.contactRepository.findContactByUser(user.getId());
        model.addAttribute("contacts", contacts);
        return "normal/show_contacts";
    }

}
