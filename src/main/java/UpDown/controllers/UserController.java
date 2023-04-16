package UpDown.controllers;

import UpDown.DTO.DownloadProfilePicture.DownLoadProfilePictureDTO;
import UpDown.entities.User;
import UpDown.repositories.UserRepository;
import UpDown.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    //non creo lo UserService perché in questa demo
    //non mi serve della logica per l'utente
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/profile-pic-upload")
    public User uploadProfileImage(@PathVariable long userId, @RequestParam MultipartFile profilePicture) throws Exception {
        return userService.uploadProfilePicture(userId, profilePicture);
    }

    @GetMapping("{userId}/profile-pic-download")
    public @ResponseBody byte[] downloadProfileImage(@PathVariable long userId, HttpServletResponse response) throws Exception {
        DownLoadProfilePictureDTO downLoadProfileDTO = userService.downLoadProfilePicture(userId);
        //System.out.println("Downloading " + profilePictureName);
        //cosa da fare nel Controller: dire qual'è l'ESTENSIONE
        //qui il profilePictureName lo prendo dal DTO!!!
        String fileName = downLoadProfileDTO.getUser().getProfilePicture();
        if(fileName == null) throw new Exception ("User does not have a profile picture!");
        String extension = FilenameUtils.getExtension(fileName);
        switch (extension){
            case "gif":
                response.setContentType(MediaType.IMAGE_GIF_VALUE);
                break;
            case "jpg":
            case "jpeg":
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                break;
            case "png":
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                break;
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"" );
        return downLoadProfileDTO.getProfileImage();
    }

    @PostMapping("/create-user")
    public User create(@RequestBody User user){
        //user.setId(null); <-- ma perché lo fa?
        return userRepository.save(user);
    }

    @GetMapping("/get-all-users")
    public List<User> getAll(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getOne(@PathVariable long id) throws Exception {
        Optional<User> userToFind = userRepository.findById(id);
        if(!userToFind.isPresent()){
            throw new Exception ("User with id " + id + " not present in database");
        }
        User newUser = userToFind.get();
        return newUser;
    }

    @GetMapping("/{id}/profile")
    public void getProfileImage(){}

    @PutMapping("/{id}")
    public void update(@RequestBody User user, @PathVariable long id){
        user.setId(id);
        userRepository.save(user);
    }

    @DeleteMapping("/delete-one/{userId}")
    public void delete(@PathVariable long userId) throws Exception {
        //userRepository.deleteById(id); : questo non basta, mi serve della logica!
        userService.removeUser(userId);
    }
}
