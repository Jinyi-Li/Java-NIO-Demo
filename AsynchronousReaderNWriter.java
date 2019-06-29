import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This is a demo program for {@code AsynchronousFileChannel} with its read and write
 * functionality in Java NIO packages. It writes small pieces of text (only for demonstration
 * purpose) to a given file, and reads its content to show the success of work.
 *
 * @author Jinyi Li
 */
public class AsynchronousReaderNWriter {

    private static final int BUFFER_SIZE = 1024;
    private static final String DEMO_TEXT = "It is a beautiful day!";

    /**
     * This is the main routine to test read & write functionality.
     *
     * @param args we do not need command line arguments
     */
    public static void main(String[] args) {
        String fileName = UUID.randomUUID().toString();
        Path path = Paths.get(fileName);

        System.out.printf("[Thread is: %s]\n", Thread.currentThread());
        writeContent(path);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String content = readContent(path);
        System.out.println(content);
    }

    /**
     * Asynchronously Write pre-prepared content to the given file.
     *
     * @param file the file path
     */
    private static void writeContent(Path file) {
        try {
            ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    file, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            writeBuffer.put(DEMO_TEXT.getBytes());
            writeBuffer.flip();

            fileChannel.write(writeBuffer, 0, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                /**
                 * Be executed if the task has successfully been completed.
                 * @param result number of bytes written
                 * @param attachment the buffer
                 */
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.out.printf("[Thread is: %s]\n", Thread.currentThread());
                }

                /**
                 * Be executed if the task has failed.
                 * @param exc the exception
                 * @param attachment the buffer
                 */
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    System.out.println("Failed");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read content from the given file.
     *
     * @param file the file path
     * @return the content
     */
    private static String readContent(Path file) {
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        try {
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    file, StandardOpenOption.READ);
            Future<Integer> operation = fileChannel.read(readBuffer, 0);
            // run other code as operation continues in background
            operation.get();

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        String fileContent = new String(readBuffer.array()).trim();
        readBuffer.clear();
        return fileContent;
    }
}
