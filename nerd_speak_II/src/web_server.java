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
import java.util.Arrays;
import java.util.Scanner;

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
                System.out.println("Uploaded, processing");
                
                String interpreter_file = ".\\src\\Interpreter.java";
                String boh_file = ".\\src\\BagOfHolding.java";
                String lexer_file = ".\\src\\Lexer.java";
                String material_file = ".\\src\\Material.java";
                String off_file = ".\\src\\Off.java";
                String on_file = ".\\src\\On.java";
                String parser_file = ".\\src\\Parser.java"; 
                String token_file = ".\\src\\Token.java";
                String verbal_file =  ".\\src\\Verbal.java";
                String class_name = "Interpreter";
                System.out.println(System.getProperty("user.dir"));

                try {
                    ProcessBuilder compile_process = new ProcessBuilder("javac", "-d", ".", interpreter_file, boh_file, lexer_file, material_file, off_file, on_file, parser_file, token_file, verbal_file);
                    compile_process.redirectErrorStream(true);
                    Process compile = compile_process.start();
                    System.out.println(Arrays.toString(compile.getInputStream().readAllBytes()));
                    compile.waitFor();
                    
                    //run compiled java code
                    File result = new File("result.txt");
                    ProcessBuilder run_process = new ProcessBuilder( "java", class_name, "uploaded.dnd");
                    // run_process.redirectErrorStream(true);
                    run_process.redirectOutput(result);
                    Process run = run_process.start();
                    run.waitFor();
                    System.out.println("Compiled and ran");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StringBuilder response = new StringBuilder("{\"result\":\"");
                File result = new File("result.txt");
                Scanner scan = new Scanner(result);
                while (scan.hasNextLine()) { 
                    response.append(scan.nextLine());
                    response.append("\\n");
                }
                response.append("\"}");
                System.out.println(response.toString());
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.toString().getBytes());
                }
            }
            else {
                String response = ":( good try";
                exchange.sendResponseHeaders(400, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}
