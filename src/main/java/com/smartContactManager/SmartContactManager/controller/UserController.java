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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
                contact.setImage("contactdefault.jpg");

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
    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,Model model, Principal principal) {
        model.addAttribute("title", "Show User Contacts");
        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);
        
        PageRequest pageable = PageRequest.of(page, 5); // (page, size)

        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show_contacts";
    }


    // showing particular contact details
    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        System.out.println("CID " + cId);
        try {
            Contact contact = this.contactRepository.findById(cId).get();

            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);

            if (user.getId() == contact.getUser().getId()) {
                model.addAttribute("contact", contact);
                model.addAttribute("title", contact.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "normal/contact_detail";
    }


    // delete contact
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {
        Contact contact = this.contactRepository.findById(cId).get();

        User user = this.userRepository.getUserByUserName(principal.getName());
        
        // contact.setUser(null);
        
        user.getContacts().remove(contact);


        this.userRepository.save(user);

        System.out.println("DELETED");

        redirectAttributes.addFlashAttribute("message", new Message("Contact deleted successfully...", "success"));

        return "redirect:/user/show_contacts/0";
    }

    // open update form
    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model model) {
        model.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.findById(cId).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    // update contact
    @PostMapping("/process-update")
    public String updateHandler(@Valid @ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
            Model model, Principal principal, RedirectAttributes redirectAttributes) {

        try {

            // old contact details
            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();

            //image
            if(!file.isEmpty()){
               
                //delete old photo
                // File deleteFile = new ClassPathResource("static/img").getFile();
                // File file1 = new File(deleteFile, oldContactDetail.getImage());
                // file1.delete();
                

                //update new photo
                File savFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(savFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());



            }
            else{
                contact.setImage(oldContactDetail.getImage());
            }
            
            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);

            redirectAttributes.addFlashAttribute("message", new Message("Your contact is updated...", "success"));
        }
           catch (Exception e) {
                e.printStackTrace();  
            }  
      

        return "redirect:/user/"+contact.getcId()+"/contact";
    }

    // your profile
    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile");
        return "normal/profile";
    }
    

}
