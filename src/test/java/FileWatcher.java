import lombok.SneakyThrows;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher {
    @SneakyThrows
    public static void main(String[] args){
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Path.of(String.join(System.getProperty("file.separator"), System.getProperty("user.home"), ".config", "macropad"));
        path.register(watchService,ENTRY_MODIFY);

        for (;;){
            WatchKey key;
            key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                // The filename is the
                // context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();
//                the first event name ends with ~
//                IDK why though
                if (filename.toString().equals("macropad.conf~")){

                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }
}
