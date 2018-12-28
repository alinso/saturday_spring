package com.alinso.myapp.controller;


import com.alinso.myapp.dto.AlbumDto;
import com.alinso.myapp.service.MapValidationErrorService;
import com.alinso.myapp.service.PhotoService;
import com.alinso.myapp.validator.AlbumValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    AlbumValidator albumValidator;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam MultipartFile[] files){

        AlbumDto albumDto =  new AlbumDto();
        albumDto.setFiles(files);

        DataBinder binder = new DataBinder(albumDto);
        binder.setValidator(albumValidator);
        binder.validate();

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(binder.getBindingResult());
        if(errorMap != null) return errorMap;

        photoService.savePhotos(files);

        return new ResponseEntity<String>("{result:saved}",HttpStatus.ACCEPTED);
    }


}
