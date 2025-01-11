package by.danilakuzin.schoolApplication.config;

import by.danilakuzin.schoolApplication.services.fileServices.DocFileService;
import by.danilakuzin.schoolApplication.services.fileServices.YandexDiskDownloader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class UpdateFileByDelay {
    static final Logger LOGGER = Logger.getLogger(UpdateFileByDelay.class.getName());
    private final DocFileService docFileService;

    // Внедрение зависимости через конструктор
    public UpdateFileByDelay(DocFileService docFileService) {
        this.docFileService = docFileService;
    }


    @Scheduled(fixedDelay = 43_000_000L)
    private void downloadAndUpdate() throws InterruptedException, IOException {
        // После завершения загрузки, выполняем следующий шаг
        docFileService.reDownload();
        LOGGER.info("Файл прочитан");
    }
}
