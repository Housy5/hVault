package vault.serial;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import vault.fsys.*;
import vault.user.User;

import java.util.*;
import vault.Constants;
import vault.fsys.io.FileSystemFormat;
import vault.password.Password;

public class Saver {

    private static final String SEP = String.valueOf((char) 31);

    private Saver() {
    }

    private static String formatFolder(Folder folder) {
        return String.join(SEP, "folder",
                folder.getName(),
                folder.getPath(),
                folder.getParent() == null ? "root" : folder.getParent().getPath(),
                folder.getPassword() == null ? "null" : folder.getPassword().toString(),
                Boolean.toString(folder.isLocked()),
                Boolean.toString(folder.isSearchFolder()),
                folder.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private static String formatFile(FilePointer fp) {
        return String.join(SEP, "pointer",
                fp.getName(),
                fp.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                fp.getParent().getPath(),
                fp.getContentFile().getAbsolutePath(),
                fp.getFileFormat().toString(),
                String.valueOf(fp.getSize()));
    }

    private static String formatFileSystemItem(FileSystemItem item) {
        if (item instanceof Folder folder) {
            return formatFolder(folder);
        }

        if (item instanceof FilePointer fp) {
            return formatFile(fp);
        }

        throw new IllegalStateException("Saving for this object type hasn't been implemented yet!");
    }

    private static String formatFileSystem(FileSystem fs) {
        System.out.println("");
        Queue<FileSystemItem> items = new LinkedList<>();
        items.add(fs.getRoot());

        Base64.Encoder encoder = Base64.getEncoder();

        StringBuilder sb = new StringBuilder();

        while (!items.isEmpty()) {
            FileSystemItem fsi = items.poll();
            if (fsi instanceof FilePointer) {
                sb.append(encoder.encodeToString(formatFileSystemItem(fsi).getBytes())).append(SEP);
            }
            if (fsi instanceof Folder folder) {
                if (!folder.getName().equalsIgnoreCase("Root")) {
                    sb.append(encoder.encodeToString(formatFileSystemItem(fsi).getBytes())).append(SEP);
                }
                items.addAll(folder.getSubFolders());
                items.addAll(folder.getPointers());
            }
        }
        return encoder.encodeToString(sb.toString().getBytes());
    }

    private static FileSystem parseFileSystem(String b64) {
        String decoded = new String(Base64.getDecoder().decode(b64.getBytes()));
        FileSystem fs = new FileSystem();
        String[] items = decoded.split(SEP);

        Base64.Decoder decoder = Base64.getDecoder();

        HashMap<String, Folder> foldermap = new HashMap<>();

        for (String line : items) {
            String[] tokens = new String(decoder.decode(line)).split(SEP);
            if (tokens[0].equals("folder")) {

                Folder folder = new Folder();
                folder.setName(tokens[1]);
                folder.setPassword(Password.parse(tokens[4]));
                folder.setSearchFolder(Boolean.parseBoolean(tokens[6]));
                folder.setCreationDate(LocalDateTime.parse(tokens[7]));
                foldermap.put(tokens[2], folder);
                
                if (tokens[3].equalsIgnoreCase("root")) {
                    folder.setParent(fs.getRoot());
                    fs.getRoot().addFolder(folder);
                    continue;
                }
                
                folder.setParent(foldermap.get(tokens[3]));
                foldermap.get(tokens[3]).addFolder(folder);

            } else if (tokens[0].equals("pointer")) {
                FilePointer fp = new FilePointer();
                fp.setName(tokens[1]);
                fp.setCreationDate(LocalDateTime.parse(tokens[2]));
                fp.setContentFile(new File(tokens[4]));
                fp.setFileFormat(FileFormat.valueOf(tokens[5]));
                
                if (tokens[3].equalsIgnoreCase("root")) {
                    fp.setParent(fs.getRoot());
                    fs.getRoot().addFilePointer(fp);
                    continue;
                }
                
                fp.setParent(foldermap.get(tokens[3]));
                foldermap.get(tokens[3]).addFilePointer(fp);
            }
        }

        return fs;
    }

    public static boolean save(User user) {
        try {
            String filesystem = formatFileSystem(user.getFileSystem());
            String username = user.getUsername();
            String password = user.getPassword().toString();
            String data = String.join(SEP, username, password, filesystem);
            Files.write(Constants.USERS_PATH.toPath().resolve(user.getUsername() + ".usr"), Base64.getEncoder().encode(data.getBytes()));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static User load(File file) {
        try {
            String userData = new String(Base64.getDecoder().decode(Files.readAllBytes(file.toPath())));
            String[] tokens = userData.split(SEP);
            if (tokens.length != 3) {
                throw new IllegalStateException();
            }
            String username = tokens[0];
            Password password = Password.parse(tokens[1]);
            String fsdata = tokens[2];
            FileSystem fs = parseFileSystem(fsdata);

            User user = new User();
            user.setPassword(password);
            user.setUsername(username);
            user.setFileSystem(fs);

            return user;
        } catch (IOException ex) {
            return null;
        }
    }
}
