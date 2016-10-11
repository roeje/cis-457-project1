import java.io.* ;
import java.net.* ;
import java.util.* ;

final class FtpRequestServer implements Runnable {
    final static String CRLF = "\r\n";
    String clientName;
    int dataPort;
    Socket controlSocket;
   //  Socket dataSocket;

    // Control Connection
    DataInputStream controlIn;
    DataOutputStream controlOut;

    // Constructor
    FtpRequestServer(Socket socket) throws Exception {
      try {
         controlSocket = socket;
         controlIn = new DataInputStream(controlSocket.getInputStream());
         controlOut = new DataOutputStream(controlSocket.getOutputStream());
      } catch (Exception e) {
         System.out.println(e);
      }
    }

    void listDirContents() {

      // System.out.println("List all files in current directory requet recieved:");
      // try {
      //    System.out.println(clientName);
      //
      //    OutputStream out = dataSocket.getOutputStream();
      //    DataOutputStream dout = new DataOutputStream(out);
      //
      //    File[] files = new File(".").listFiles();
      //
      //    // byte[] bytes = new byte[(int)file.length()];
      //
      //    for (File file : files) {
      //       dout.writeUTF(file.getName());
      //    }
      //    dout.writeUTF("END");
      //    System.out.println("Files Sent!");
      //
      //    dout.flush();
      //
      // } catch (Exception e) {
      //    System.out.println(e);
      // }
   }

   void retreveFile(String fileName) {
      System.out.println("Getting File: " + fileName);

      try{

         Socket dataSocket = new Socket("localhost", 10004);
         DataOutputStream dout = new DataOutputStream(dataSocket.getOutputStream());

         // OutputStream out = dataSocket.getOutputStream();
         // DataOutputStream dout = new DataOutputStream(out);

         File file = new File(fileName);
         byte[] bytes = new byte[(int)file.length()];

         FileInputStream fin = new FileInputStream(file);

         BufferedInputStream buffin = new BufferedInputStream(fin);

         DataInputStream datain = new DataInputStream(buffin);
         datain.readFully(bytes, 0, bytes.length);

         dout.writeLong(bytes.length);
         dout.write(bytes, 0, bytes.length);
         dout.flush();
         dout.close();
         dataSocket.close();
         System.out.println("File Sent!");

      } catch (Exception e) {
        System.out.println(e);
      }

   }

   void saveFile(String fileName) {
      // System.out.println("File " + fileName + " received from Client.");
      // try {
      //
      //    // Socket dataSocket = new ServerSocket(10004).accept();
      //    InputStream in = dataSocket.getInputStream();
      //    DataInputStream din = new DataInputStream(in);
      //    // DataInputStream din = new DataInputStream(dataSocket.getInputStream());
      //
      //    int bytes;
      //
      //    OutputStream out = new FileOutputStream(("New" + fileName));
      //    long sizeOfData = din.readLong();
      //    byte[] buffer = new byte[1024];
      //    while (sizeOfData > 0 && (bytes = din.read(buffer, 0, (int) Math.min(buffer.length, sizeOfData))) != -1) {
      //       out.write(buffer, 0, bytes);
      //       sizeOfData -= bytes;
      //    }
      //
      //    out.close();
      //
      //    //   dataSocket.close();
      //
      //    //   in.close();
      //    System.out.println("File Saved Successfully...");
      //
      // } catch (Exception e) {
      //      System.out.println(e);
      // }

   }

   // Implement the run() method of the Runnable interface.
   public void run() {
      // Listen on command connection for Command from client. Trigger correct function based on client command.
      System.out.println("Server Thread Started:");
      while(true) {
         try {
            String cmd = controlIn.readUTF();
            // String[] command = cmd.split("\\s");

            System.out.println("Server recieve command: " + cmd);

            switch(cmd.toUpperCase()) {
               case "LIST":
                  listDirContents();
                  break;
               case "RETR":
                  String fileName = controlIn.readUTF();
                  retreveFile(fileName);
                  break;
               case "STOR":
                  String fileName2 = controlIn.readUTF();
                  saveFile(fileName2);
                  break;
               case "QUIT":
                  System.out.println("Client Disconnecting...");
                  controlSocket.close();
                  // dataSocket.close();
                  return;
               case "DATA":
                  clientName = controlIn.readUTF();
                  dataPort = Integer.parseInt(controlIn.readUTF());
                  // dataSocket = new Socket("localhost", 10004);
                  break;
               case "TEST":
                  System.out.println("Test Recieved!!!");
                  break;
            }

     		} catch (Exception e) {
     		    System.out.println(e);
     		}
      }
   }
}
