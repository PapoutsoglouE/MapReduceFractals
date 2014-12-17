package support;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class OperationsHDFS {
	private Configuration config;
	private FileSystem dfs;
	private String hdfsPath;

	public OperationsHDFS() {
		config = new Configuration();
		//adding local hadoop configuration
		config.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
		config.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));
		hdfsPath = config.get("fs.default.name");
		try {
			dfs = FileSystem.get(new Configuration(config));
		} catch (IOException e) {
			System.out.println("Error initialising filesystem configuration (OperationsHDFS).");
		}
	}
	/**
	 * Copies a file from the local filesystem to HDFS.
	 * @param sourcePath file to be copied (on the local FS)
	 * @param destPath copy operation destination, (with filename)
	 * @return true on operation success, false otherwise
	 */
	public boolean FromLocalToHDFS(String sourcePath, String destPath) {
		// Check if the file already exists
		Path destination = new Path(hdfsPath + "/user/hduser/" + destPath);
		System.out.println("Attempting to copy " + sourcePath + " to " + destination + "...");
		
		try {
			if (dfs.exists(destination)) {
				System.out.println("File " + destination + " already exists");
			} else {
				dfs.copyFromLocalFile(new Path(sourcePath), destination); 
			}
		} catch (IOException e) {
			System.out.println("Couldn't copy file from local to distributed FS."); 
			return false;
		} catch (Exception e) {
			System.out.println("Something went wrong.");
			return false;
		}

		return true;
	}

	public boolean FromHdfsToLocal() {
		return true;
	}

}