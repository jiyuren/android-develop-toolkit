/**
 *
 */
package com.allthelucky.examples.common;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * @description Utils for file operations
 * @auther steven-pan
 */
public class FileUtils {

	private static String path = Environment.getExternalStorageDirectory()
			.getPath()
			+ File.separator
			+ "GMYZ"
			+ File.separator
			+ "log"
			+ File.separator;

	private static String padFilePath = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "driver.ini";
	
	/**
	 * Get MD5 of one file:hex string,test OK!
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileMD5(File file) {
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	/**
	 * copy assets文件到指定目录。
	 * 
	 * @param context
	 *            Context
	 * @param assetName
	 *            assetName
	 * @param saveFilePath
	 */
	public static void copyAssetsFile(Context context, String assetName,
			String saveFilePath) {
		try {
			final File file = new File(saveFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
 
			InputStream is = context.getResources().getAssets().open(assetName);
			FileOutputStream fos = new FileOutputStream(saveFilePath);
			byte[] buffer = new byte[7168];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
 
			fos.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public static void copyRawFile( Context context, int rawid, String dbname) {
		File file = context.getDatabasePath(dbname);
		if (file.exists()) {
			return;
		}
 
		byte[] buf = new byte[1024];
		try {
			InputStream is=context.getResources().openRawResource(rawid);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
				file.createNewFile();
			}
			FileOutputStream os = new FileOutputStream(file);
			int count = -1;
			while ((count = is.read(buf)) != -1) {
				os.write(buf, 0, count);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			buf = null;
		}
	}
	
	/**
	 * 解压文件到指定目录
	 * 
	 * @param sourcePath
	 * @param targetDirPath
	 * @author isea533
	 */
	public static void unZipFiles(final String sourcePath, String targetDirPath) {
		ZipFile zip = null;
		try {
			zip = new ZipFile(new File(sourcePath));
			for (Enumeration<?> entries = zip.entries(); entries
					.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String zipEntryName = entry.getName();
				InputStream in = zip.getInputStream(entry);
				String outPath = (targetDirPath + File.separator + zipEntryName)
						.replaceAll("\\*", "/");
				// 判断路径是否存在,不存在则创建文件路径
				File file = new File(outPath.substring(0,
						outPath.lastIndexOf('/')));
				if (!file.exists()) {
					file.mkdirs();
				}
				// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
				if (new File(outPath).isDirectory()) {
					continue;
				}
 
				System.out.println("outPath:" + outPath);
				OutputStream out = new FileOutputStream(outPath);
				byte[] buf1 = new byte[1024];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
 
				in.close();
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zip != null) {
					zip.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 
	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @param context
	 * @param
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null)
			content = "";
		try {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			// e.printStackTrace();
			return "";
		}
	}

	private static String readInStream(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			// UIHelper.Log("e", "", "FileReadError", true);
		}
		return null;
	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeFile(byte[] buffer, String folder,
			String fileName) {
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		String folderPath = "";
		if (sdCardExist) {
			folderPath = Environment.getExternalStorageDirectory()
					+ File.separator + folder + File.separator;
		} else {
			writeSucc = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writeSucc;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (null == filePath || "".equals(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (null == filePath || "".equals(filePath)) {
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
				point);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (null == fileName || "".equals(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;
	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/"
								+ listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					// UIHelper.Log("i", "", fileName, true);
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					// UIHelper.Log("i", "", fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					se.printStackTrace();
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * @param msgID
	 * @param msgID
	 * @param log
	 * @param isLog
	 * @return
	 */
	public static String saveLog(String msgID, String log, boolean isLog,
			String fileName) {
		if (!isLog) {
			return null;
		}
		SimpleDateFormat logTime = new SimpleDateFormat("yyyyMMddHHmmss");
		Date logD = new Date(System.currentTimeMillis());
		StringBuffer sb = new StringBuffer();
		if (msgID != null && !msgID.equals("")) {
			sb.append(logTime.format(logD) + "【" + msgID + "】\n");
		} else {
			sb.append(logTime.format(logD) + "\t\t");
		}
		String logStr = "";
		String splitStr = "body";
		String[] tempStr = log.split(splitStr);
		if (tempStr.length > 1) {
			logStr += tempStr[0] + "\n" + splitStr;
			String body = tempStr[1].replaceAll(",", "");
			tempStr = body.split("]");
			for (int i = 0; i < tempStr.length; i++) {
				if (i != tempStr.length - 2) {
					logStr += tempStr[i] + "],\n";
				} else {
					logStr += tempStr[i] + "";
				}
			}
			sb.append(logStr.substring(0, logStr.length() - 4));
		} else {
			sb.append(log);
		}
		sb.append("\n");
		SimpleDateFormat logFt = new SimpleDateFormat("yyyyMMdd");
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		printWriter.close();
		try {
			long timestamp = System.currentTimeMillis();
			Date d = new Date(timestamp);
			String time = logFt.format(d);
			if (null == fileName || fileName.equals("")) {
				fileName = "log";
			}
			fileName += ("-" + time + ".txt");
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File f = new File(path + fileName);
				if (!f.exists()) {
					f.createNewFile();
				}
				FileOutputStream fost = new FileOutputStream(f, true);
				BufferedWriter myo = new BufferedWriter(new OutputStreamWriter(
						fost, "GBK"));
				myo.write(sb.toString());
				myo.close();
			}
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String saveLog(String log, boolean isLog, String fileName) {
		return saveLog(log, "", isLog, fileName);
	}

	public static String saveLog(String log, boolean isLog) {
		return saveLog(log, "", isLog, null);
	}

	public static List<File> list(File dir, String nametxt, String ext,
			String type, List<File> fs) {
		listFile(dir, nametxt, type, ext, fs);
		File[] all = dir.listFiles();
		for (int i = 0; i < all.length; i++) {
			File d = all[i];
			if (d.isDirectory()) {
				list(d, nametxt, ext, type, fs);
			}
		}
		return null;
	}

	/**
	 * @param dir
	 *            根目�?
	 * @param nametxt
	 *            文件名中包含的关键字
	 * @param type
	 *            文件夹的类型
	 * @param ext
	 *            后缀�?
	 * @param fs
	 *            返回的结�?
	 * @return
	 */
	private static List<File> listFile(File dir, String nametxt, String type,
			String ext, List<File> fs) {
		File[] all = dir.listFiles(new Fileter(ext));
		for (int i = 0; i < all.length; i++) {
			File d = all[i];
			if (d.getName().toLowerCase().indexOf(nametxt.toLowerCase()) >= 0) {
				if (type.equals("1")) {
					fs.add(d);
				} else if (d.isDirectory() && type.equals("2")) {
					fs.add(d);
				} else if (!d.isDirectory() && type.equals("3")) {
					fs.add(d);
				}
			}
		}
		return fs;
	}

	public static boolean delFile(String filePathAndName) {
		boolean bea = false;
		try {
			String filePath = filePathAndName;
			File myDelFile = new File(filePath);
			if (myDelFile.exists()) {
				myDelFile.delete();
				bea = true;
			} else {
				bea = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bea;
	}

	public static int copyFile(String oldPathFile, String newPathFile) {
		int bytesum = 0;
		try {
			int byteread = 0;
			File oldfile = new File(oldPathFile);
			if (oldfile.exists()) { // 文件存在
				InputStream inStream = new FileInputStream(oldPathFile); // 读入源文�?
				File n = new File(newPathFile);
				if (!n.exists()) {
					n.createNewFile();
				}
				FileOutputStream fs = new FileOutputStream(newPathFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节 文件大小
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytesum;
	}

	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * 修改文件权限
	 * 
	 * @param file
	 */
	public static void modifyFile(File file) {
		Process process = null;
		try {
			String command = "chmod -R 777 " + file.getAbsolutePath();
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(command);
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param dir
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public String readSDFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		File file = new File(padFilePath + "//" + fileName);
		if (!file.exists()) {
			return "";
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) != -1) {
				sb.append((char) c);
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	static class Fileter implements FilenameFilter {
		private final String ext;

		public Fileter(String ext) {
			this.ext = ext;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(ext);

		}
	}

}
