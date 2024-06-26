package FileShredder;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileShredder {

    private static final Logger logger = LoggerFactory.getLogger(FileShredder.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int RANDOM_DATA_SIZE = 4096;
    private static final int OVERWRITE_PASSES = 5;
    private static final Duration LOCK_WAIT_TIMEOUT = Duration.ofSeconds(5);

    public void shredFile(String filePath) {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            logger.info("文件不存在: {}", filePath);
            return;
        }

        try {
            overwriteWithRandomData(filePath);

            Files.delete(path);
            logger.info("文件已被安全粉碎: {}", filePath);
        } catch (IOException e) {
            logger.error("粉碎文件时出错: {}", e.getMessage(), e);
        }
    }

    private void overwriteWithRandomData(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            logger.info("文件不存在: {}", filePath);
            return;
        }

        if (!file.canWrite()) {
            logger.info("文件不可写: {}", filePath);
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel channel = raf.getChannel()) {
            long fileSize = file.length();
            byte[] randomBytes = new byte[RANDOM_DATA_SIZE];
            for (int pass = 0; pass < OVERWRITE_PASSES; pass++) {
                FileLock lock = null;
                boolean lockAcquired = false;
                while (!lockAcquired) {
                    try {
                        lock = channel.tryLock();
                        lockAcquired = true;
                    } catch (OverlappingFileLockException e) {
                        System.out.println("尝试获取文件锁失败，等待...");
                        try {
                            TimeUnit.MILLISECONDS.sleep(LOCK_WAIT_TIMEOUT.toMillis());
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            System.out.println("等待文件锁时线程被中断");
                            break;
                        }
                    }
                }

                if (lock != null) {
                    try {
                        for (long position = 0; position < fileSize; position += RANDOM_DATA_SIZE) {
                            SECURE_RANDOM.nextBytes(randomBytes);
                            int writeLength = (int) Math.min(RANDOM_DATA_SIZE, fileSize - position);
                            channel.write(ByteBuffer.wrap(randomBytes, 0, writeLength), position);
                        }
                    } finally {
                        if (lockAcquired) {
                            lock.release();
                        }
                    }
                }
            }
        }
    }
}
