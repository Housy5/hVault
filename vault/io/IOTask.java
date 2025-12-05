package vault.io;

import java.nio.file.Path;
import vault.fsys.FilePointer;
import vault.fsys.FileSystemItem;
import vault.fsys.Folder;

public class IOTask {
    
    private Status status;
    private IOTaskType type;
    private Path input;
    private Path output;
    
    private Folder destination;
    private FileSystemItem source;
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    public Path getInput() {
        return input;
    }

    public void setInput(Path input) {
        this.input = input;
    }

    public Path getOutput() {
        return output;
    }

    public void setOutput(Path output) {
        this.output = output;
    }
    
    public Folder getDestination() {
        return destination;
    }
    
    public void setDestination(Folder d) {
        destination = d;
    }

    public FileSystemItem getSource() {
        return source;
    }

    public void setSource(FileSystemItem source) {
        this.source = source;
    }

    public IOTaskType getType() {
        return type;
    }

    public void setType(IOTaskType type) {
        this.type = type;
    }
    
    public static IOTask createImportTask(Folder dst, Path in) {
        IOTask ticket = new IOTask();
        ticket.setType(IOTaskType.IMPORT);
        ticket.setStatus(Status.PENDING);
        ticket.setInput(in);
        ticket.setDestination(dst);
        return ticket;
    }
    
    public static IOTask createExportTask(FileSystemItem fsys, Path out) {
        IOTask ticket = new IOTask();
        ticket.setType(IOTaskType.EXPORT);
        ticket.setStatus(Status.PENDING);
        ticket.setOutput(out);
        return ticket;
    }
    
    public static IOTask createOpenTask(FilePointer fp) {
        IOTask ticket = new IOTask();
        ticket.setType(IOTaskType.OPEN);
        ticket.setStatus(Status.PENDING);
        ticket.setSource(fp);
        return ticket;
    }
    
}
