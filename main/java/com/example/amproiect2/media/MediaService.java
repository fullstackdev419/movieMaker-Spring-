package com.example.amproiect2.media;

import com.example.amproiect2.config.BucketName;
import com.example.amproiect2.datasource.MediaDatasource;
import com.example.amproiect2.datasource.async.VideoLoadService;
import com.example.amproiect2.datasource.storage.StorageFileDto;
import com.example.amproiect2.entities.LocalFileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    public final static String URL_IMAGE = "http://localhost:8080/api/v1/user-profile/images/download/";
    public final static String URL_AUDIO = "http://localhost:8080/api/v1/user-profile/audio/download/";

    private final MediaDatasource mediaDatasource;
    private final VideoLoadService videoLoadService;


    @Autowired
    public MediaService(MediaDatasource mediaDatasource, VideoLoadService videoLoadService) {
        this.mediaDatasource = mediaDatasource;
        this.videoLoadService = videoLoadService;
    }

    public void uploadFile(MultipartFile file, String folderName) {
        //check if image is not empty
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + " ]");
        }

        //get some metadata from file
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put("Content-Type", file.getContentType());
        metaData.put("Content-Length", String.valueOf(file.getSize()));

        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), folderName);

        try {
            StorageFileDto storageFileDto = new StorageFileDto(path, file.getOriginalFilename(),
                    metaData, file.getInputStream());

            mediaDatasource.saveFile(storageFileDto, folderName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LocalFileDto> fetchImagesEndpoint() {
        return mediaDatasource.createParseImageEndpoint();
    }

    public List<LocalFileDto> fetchAudioEndpoint() {
        return mediaDatasource.createParseAudioEndpoint();
    }

    public byte[] getImageFileByIndex(int index) {
        System.out.println(Thread.currentThread().getName());
        return Optional.of(mediaDatasource
                        .getCachedImages()
                        .get(index))
                .orElseThrow(() -> new RuntimeException("Could not get image at index " + index));
    }

    public byte[] getAudioFileByIndex(int index) {
        return Optional.of(mediaDatasource
                        .getCachedAudio()
                        .get(index))
                .orElseThrow(() -> new RuntimeException("Could not get audio at index " + index));
    }

    public byte[] provideTestVideo() throws Exception {
        return videoLoadService.loadVideoFile().get();
    }
}

