package com.example.demo.config;

import com.example.demo.services.DocFileService;
import com.example.demo.services.YandexDiskDownloader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;

@Service
public class UpdateFileByDelay {
    static final Logger LOGGER = Logger.getLogger(UpdateFileByDelay.class.getName());
    int i = 0;

//    @Scheduled(fixedDelay = 3_000L)
//    private void print() throws InterruptedException {
//        LOGGER.info("Выполнилось" + i);
//        i++;
//        Thread.sleep(4000);
//    }

    @Scheduled(fixedDelay = 43_000_000L)
    private void downloadAndUpdate() throws InterruptedException, IOException {
        YandexDiskDownloader.download();

        new DocFileService();
        LOGGER.info("Файл прочитан");
    }
}
