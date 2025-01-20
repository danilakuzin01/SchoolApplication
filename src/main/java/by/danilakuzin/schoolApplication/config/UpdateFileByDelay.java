package by.danilakuzin.schoolApplication.config;

import by.danilakuzin.schoolApplication.services.fileComponents.DocFileComponent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class UpdateFileByDelay {
    static final Logger LOGGER = Logger.getLogger(UpdateFileByDelay.class.getName());
    private final DocFileComponent docFileComponent;

    // Внедрение зависимости через конструктор
    public UpdateFileByDelay(DocFileComponent docFileComponent) {
        this.docFileComponent = docFileComponent;
    }


    @Scheduled(fixedDelay = 43_000_000L)
    private void downloadAndUpdate() throws InterruptedException, IOException {
        // После завершения загрузки, выполняем следующий шаг
        docFileComponent.reDownload();
        LOGGER.info("Файл прочитан");
    }
}
