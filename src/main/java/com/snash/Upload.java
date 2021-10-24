// should add uploading here

public class Upload {


  BoxFileUploadSession.Info sessionInfo;
  if (/* uploading a new file */) {
    // Create the upload session for a new file
    BoxFolder rootFolder = BoxFolder.getRootFolder(api);
    sessionInfo = rootFolder.createUploadSession("New Large File.pdf", fileSize);
  } else if (/* uploading a new version of an exiting file */) {
    // Create the uplaod session for a new version of an existing file
    String fileID = "93465";
    BoxFile file = new BoxFile(api, fileID);
    sessionInfo = file.createUploadSession(fileSize);
  }

    //Get the session resource from the session info
    BoxFileUploadSession session = sessionInfo.getResource();

    //Create the Message Digest for the whole file
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException ae) {
      throw new BoxAPIException("Digest algorithm not found", ae);
  }
  BoxFolder rootFolder = BoxFolder.getRootFolder(api);
  FileInputStream stream = new FileInputStream("My File.txt");
  BoxFile.Info newFileInfo = rootFolder.uploadFile(stream, "My File.txt");
  stream.close();

  {
    public class UploadAllFilesInFolder {

 public static final int CHUNKED_UPLOAD_MINIMUM = 20000;

 public static void main(String[] args) throws Exception {
  String directoryName = "javaUploadFolder";
  Path configPath = Paths.get("config.json");
  Path uploadFolderPath = Paths.get(directoryName);
  try (BufferedReader reader = Files.newBufferedReader(configPath, Charset.forName("UTF-8"))) {
   BoxConfig boxConfig = BoxConfig.readFrom(reader);
   BoxDeveloperEditionAPIConnection client = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
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
   ArrayList < BoxFile.Info > uploadedFiles = new ArrayList < > ();
   try (DirectoryStream < Path > directory = Files.newDirectoryStream(uploadFolderPath)) {

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
 }

 private static BoxFile.Info uploadEachFile(BoxDeveloperEditionAPIConnection client, String folderId, String fileName,
  int fileSize, byte[] fileBytes, boolean useChunkedUpload)
 throws IOException, InterruptedException, NoSuchAlgorithmException {
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
      uploadFileVersion.uploadVersion(new ByteArrayInputStream(fileBytes), fileSHA);
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

  }
}
