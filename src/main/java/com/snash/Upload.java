package com.snash;

import java.io.ByteArrayInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.BufferedReader;
import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFile.Info;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxUser;
import com.box.sdk.FileUploadParams;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Upload {

    private static final int MAX_DEPTH = 1;
    private static final String DEVELOPER_TOKEN = "";
    // DEVELOPER_TOKEN is something like "bHexasdYlNxBbU1zhEWfRqpHX3Uh2Qrk".
    // At the moment, we do not know Kristina's DEVELOPER_TOKEN hence we used one of our BOX accounts to verify results
    private static final int CHUNKED_UPLOAD_MINIMUM = 20000;
    private String directoryName;

    public static void upload(String directoryName) throws Exception {
          Path uploadFolderPath = Paths.get(directoryName);
          BoxAPIConnection client = new BoxAPIConnection(DEVELOPER_TOKEN);
          String parentFolderId = "0";
          String createdFolderId;
          BoxFolder createFolderInParentFolder = new BoxFolder(client, parentFolderId);
          try {
              BoxFolder.Info createdFolder = createFolderInParentFolder.createFolder(directoryName);
              System.out.println("Creating folder...");
              System.out.println(createdFolder.getID());
              createdFolderId = createdFolder.getID();
          } catch (BoxAPIException e) {
              String existingFolderId = getIdFromConflict(e.getMessage());
              System.out.println("Found existing folder...");
              System.out.println(existingFolderId);
              createdFolderId = existingFolderId;
          }
          ArrayList<BoxFile.Info> uploadedFiles = new ArrayList<>();
          try (DirectoryStream<Path> directory = Files.newDirectoryStream(uploadFolderPath)) {
              for (Path path: directory) {
                  String fileName = path.getFileName().toString();
                  System.out.println(path);
                  System.out.println(fileName);
                  byte[] fileBytes = Files.readAllBytes(path);
                  int fileSize = fileBytes.length;
                  boolean useChunkedUpload = (fileSize > CHUNKED_UPLOAD_MINIMUM) ? true : false;
                  uploadedFiles.add(uploadEachFile(client, createdFolderId, fileName, fileSize, fileBytes, useChunkedUpload));
              }
          }
          for (BoxFile.Info file: uploadedFiles) {
              System.out.println(file.getID());
          }
      }

      private static BoxFile.Info uploadEachFile(BoxAPIConnection client, String folderId, String fileName, int fileSize,
      byte[] fileBytes, boolean useChunkedUpload) throws IOException, InterruptedException, NoSuchAlgorithmException {
          try {
              BoxFolder folder = new BoxFolder(client, folderId);
              folder.canUpload(fileName, fileSize);
              if (useChunkedUpload) {
                  System.out.println("Using chunked upload...");
                  return folder.uploadLargeFile(new ByteArrayInputStream(fileBytes), fileName, fileSize);
              } else {
                  System.out.println("Using normal upload...");
                  MessageDigest md = MessageDigest.getInstance("SHA-1");
                  try (Formatter formatter = new Formatter()) {
                      for (byte b: md.digest(fileBytes)) {
                          formatter.format("%02x", b);
                      }
                      String fileSHA = formatter.toString();
                      FileUploadParams fileUpload = new FileUploadParams();
                      fileUpload.setContent(new ByteArrayInputStream(fileBytes));
                      fileUpload.setSHA1(fileSHA);
                      fileUpload.setName(fileName);
                      return folder.uploadFile(fileUpload);
                  }
              }
          } catch (BoxAPIException e) {
              if (e.getResponseCode() == 409) {
                  // You can use the ID returned from the conflict error to continue
                  String conflictId = getIdFromConflict(e.getResponse());
                  System.out.println("Found existing file: " + conflictId);
                  BoxFile uploadFileVersion = new BoxFile(client, conflictId);
                  if (useChunkedUpload) {
                      System.out.println("Using chunked upload...");
                      return uploadFileVersion.uploadLargeFile(new ByteArrayInputStream(fileBytes), fileSize);
                  } else {
                      System.out.println("Using normal upload...");
                      MessageDigest md = MessageDigest.getInstance("SHA-1");
                      try (Formatter formatter = new Formatter()) {
                          for (byte b: md.digest(fileBytes)) {
                              formatter.format("%02x", b);
                          }
                          String fileSHA = formatter.toString();
                          uploadFileVersion.uploadNewVersion(new ByteArrayInputStream(fileBytes), fileSHA);
                          return uploadFileVersion.getInfo();
                      }
                  }
              } else {
                  throw e;
              }
          }
      }

      private static String getIdFromConflict(String message) {
          String id = "";
          Pattern p = Pattern.compile("\"id\":\"[0-9]+\"");
          Pattern p2 = Pattern.compile("[0-9]+");
          Matcher m = p.matcher(message);
          if (m.find()) {
              String sub = m.group();
              Matcher m2 = p2.matcher(sub);
              if (m2.find()) {
                  id = m2.group();
              }
          }
          return id;
      }
}
