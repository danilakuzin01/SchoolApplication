package by.danilakuzin.schoolApplication.config;

import by.danilakuzin.schoolApplication.services.DocFileService;
import by.danilakuzin.schoolApplication.services.YandexDiskDownloader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@Service
public class UpdateFileByDelay {
    static final Logger LOGGER = Logger.getLogger(UpdateFileByDelay.class.getName());
    int i = 0;

    @Scheduled(fixedDelay = 43_000_000L)
    private void downloadAndUpdate() throws InterruptedException, IOException {
        CountDownLatch latch = new CountDownLatch(1);

        // Запускаем загрузку в фоновом потоке
        new Thread(() -> {
            try {
                YandexDiskDownloader.download();
                LOGGER.info("Загрузка завершена");
                latch.countDown(); // Уменьшаем счетчик, сигнализируя о завершении
            } catch (IOException e) {
                LOGGER.info("Ошибка при загрузке: "+ e);
            }
        }).start();

        // Ждем завершения загрузки
        latch.await();

        // После завершения загрузки, выполняем следующий шаг
        var docFile = new DocFileService();
        docFile.makeClasses();

        LOGGER.info("Файл прочитан");
    }
}
