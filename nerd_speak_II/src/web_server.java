import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;

class web_server{
    public static void main(String[] args) {
        try{
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/dnd-code", new DnDCodeHandler());
            server.createContext("/", SimpleFileServer.createFileHandler(Path.of(new File(".").getCanonicalPath())));
            server.setExecutor(null);
            server.start();
            System.out.println("Server is running on port 8000");
        }
        catch(IOException e){
            System.out.println("Error starting the server: " + e.getMessage());
        }    
    }

    static class DnDCodeHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            if (exchange.getRequestMethod().equals("POST")) {
                try (InputStream inputStream = exchange.getRequestBody(); FileOutputStream fos = new FileOutputStream("uploaded.dnd")) {
                    
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    
                }

                //run the java file maybe 
                //TODO: make it so it is running the java program with the user output file
                
                String java_file = "Interpreter.java";
                String class_name = "Interpreter";

                try {
                    ProcessBuilder compile_process = new ProcessBuilder("javac", "-d", ".", "src/" + java_file);
                    compile_process.redirectErrorStream(true);
                    Process compile = compile_process.start();
                    
                    //run compiled java code
                    ProcessBuilder run_process = new ProcessBuilder("java", class_name, "../uploaded.dnd", ">result.txt");
                    run_process.redirectErrorStream(true);
                    Process run = run_process.start();
                    run.waitFor();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                String response = "posted!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            else {
                String response = ":( good try";
                exchange.sendResponseHeaders(400, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
