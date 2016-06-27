import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.RandomAccessFile;
import java.util.ArrayList;

public class DataSet {
	public static final double EPSILON = 0.00000001;
	public static final String DATASET_PATH = "F://Data";
	static String ReleaseData_PATH = "D:/ReleaseDateSet";

	public static void main(String[] args) {
		File datasetDir = new File(DATASET_PATH);

		RandomAccessFile plt;
		String pltLine;
		String[] elementStrings;
		// double latitude, longitude;
		File[] userDirs = datasetDir.listFiles();
		for (File file : userDirs) { // 所有用户
			if (file.isDirectory()) {
				//File pltDir = new File(file, "Trajectory");
				Users user = new Users();
				user.userName = file.getName();
				String dirName = ReleaseData_PATH + "/" + user.userName;
				CreateFileUtil.createDir(dirName);
				File[] pltFiles = file.listFiles();
				for (File pltFile : pltFiles) { // 此用户下所有文件
int n=0;
					try {
						plt = new RandomAccessFile(pltFile, "r");
					//	plt.seek(96);
						while ((pltLine = plt.readLine()) != null) {
							n++;
							elementStrings = pltLine.split(",");
							GpsCoordinate gp = new GpsCoordinate();
							gp.setLat(Double.parseDouble(elementStrings[0]));
							gp.setLong(Double.parseDouble(elementStrings[1]));
							user.gps.add(gp);
							user.calander.add(elementStrings[4]);
							user.time.add(elementStrings[5]);
						}
						System.out.println("共"+n+"\t"+user.gps.size()); 

						try {
							plt.close();
						} catch (IOException e) {
							// TODO: handle exception while trying to close a
							// *.plt
							e.printStackTrace();
						}

					} catch (IOException e) {
						// TODO: handle exception while trying to open a *.plt
						e.printStackTrace();
					}
					String creatFileName = dirName + "/" + pltFile.getName()
							+ ".txt";
					Transformer trans = new Transformer(creatFileName);
					SDD sample = new SDD();
					List<GpsCoordinate> gpsList = new ArrayList<GpsCoordinate>();
					gpsList = trans.getGPS(sample.SSD(trans.getXY(user.gps)));
					CreateFileUtil.createFile(creatFileName); // 创建文件
					int size = gpsList.size();
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(
								creatFileName));
						bw.write(user.userName);
						for (int i = 0; i < size; i++) {
							bw.write(gpsList.get(i) + user.calander.get(i) + "\t"
									+ user.time.get(i));
							bw.newLine();
						}
						bw.flush();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}

	}

}
