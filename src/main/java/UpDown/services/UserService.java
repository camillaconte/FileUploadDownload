package UpDown.services;

import UpDown.DTO.DownloadProfilePicture.DownLoadProfilePictureDTO;
import UpDown.entities.User;
import UpDown.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileStorageService fileStorageService;

    private User getUser(long userId) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new Exception("User is not present!");
        return optionalUser.get();
    }

    // vedi uso di @SneakyThrows ma richiede Lombok!!!
    public User uploadProfilePicture(long userId, MultipartFile profilePicture) throws Exception {
        User user = getUser(userId);
        if(user.getProfilePicture() != null){
            fileStorageService.remove(user.getProfilePicture());
        }
        String fileName = fileStorageService.upload(profilePicture);
        user.setProfilePicture(fileName);
        return userRepository.save(user);
    }

    public DownLoadProfilePictureDTO downLoadProfilePicture(long userId) throws Exception {
        //prendo l'utente (e se non c'è verrà sollevata un'eccezione)
        User user = getUser(userId);
        //creiamo il nostro "DTO di uscita" e ci mettiamo dentro l'utente
        DownLoadProfilePictureDTO dto = new DownLoadProfilePictureDTO();
        dto.setUser(user);
        if(user.getProfilePicture() == null){
            return dto;
        }
        byte[] profilePictureBytes = fileStorageService.download(user.getProfilePicture());
        dto.setProfileImage(profilePictureBytes);
        return dto;
    }

    public void removeUser(long userId) throws Exception {
        //DEVO ANCHE CANCELLARE il file dal DB
        User user = getUser(userId); //vabbé dovrei controllare se esiste!
        if(user.getProfilePicture() != null) {
            fileStorageService.remove(user.getProfilePicture());
        }
        userRepository.deleteById(userId);
    }
}
